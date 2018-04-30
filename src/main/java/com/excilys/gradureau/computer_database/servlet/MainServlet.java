package com.excilys.gradureau.computer_database.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.dto.ComputerDTO;
import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet(urlPatterns = {
        MainServlet.DASHBOARD_URL,
        MainServlet.ADD_COMPUTER_URL,
        MainServlet.EDIT_COMPUTER_URL,
        MainServlet.DELETE_COMPUTER_URL
}, loadOnStartup = 0)
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainServlet.class);
    private static final int COMPANIES_COUNT = 42;
    private ICrudCDB cdb;
    private final List<Company> COMPANIES;

    private static final String DB_CONFIG_FILEPATH = "database.properties";

    //URLS
    public static final String DASHBOARD_URL = "/dashboard";
    public static final String ADD_COMPUTER_URL = "/add-computer";
    public static final String EDIT_COMPUTER_URL = "/edit-computer";
    public static final String DELETE_COMPUTER_URL = "/delete-computers";

    //JSP FILES
    public static final String DASHBOARD_JSP = "jsp/dashboard.jsp";
    public static final String ADD_COMPUTER_JSP = "jsp/addComputer.jsp";
    public static final String EDIT_COMPUTER_JSP = "jsp/editComputer.jsp";

    public MainServlet() {
        super();
        Properties databaseConnectionProperties = PropertyFileUtility.readPropertyFile(DB_CONFIG_FILEPATH);
        cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance(
                databaseConnectionProperties.getProperty("DB_URL"),
                databaseConnectionProperties.getProperty("DB_USER"),
                databaseConnectionProperties.getProperty("DB_PASSWORD")
                ));
        COMPANIES = cdb.listCompanies(0, COMPANIES_COUNT).getContent();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String URL_PREFIX = request.getContextPath();
        final String URL = request.getRequestURI().replaceFirst(URL_PREFIX, "");
        switch(URL) {
        default:
        case DASHBOARD_URL:
            dashboard(request, response);
            break;
        case ADD_COMPUTER_URL:
            addComputer(request, response);
            break;
        case EDIT_COMPUTER_URL:
            editComputer(request, response);
            break;
        case DELETE_COMPUTER_URL:
            deleteComputer(request, response);
            break;
        }
    }

    private void deleteComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug(DELETE_COMPUTER_URL);
        String[] computerIdsToDelete = request.getParameter("selection").split(",");
        for(String computerId : computerIdsToDelete) {
            Computer computerToDelete = new Computer();
            try {
                computerToDelete.setId(Long.valueOf(computerId));
                cdb.deleteComputer(computerToDelete);
            } catch (WrongObjectStateException | NumberFormatException e) {
                LOGGER.debug(DELETE_COMPUTER_URL, e);
            }
        }
        response.sendRedirect(request.getContextPath() + DASHBOARD_URL);
    }

    private void dashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug(DASHBOARD_URL);
        
        // pagination logic
        String pageNoParameter = request.getParameter("pageNo");
        String resultsPerPageParameter = request.getParameter("resultsPerPage");
        int requestedPage = Integer.parseInt(
                pageNoParameter == null ? "1" : pageNoParameter);
        int resultsPerPage = Integer.parseInt(
                resultsPerPageParameter == null ? "10" : resultsPerPageParameter);
        int offset = (requestedPage - 1) * resultsPerPage;
        
        Page<Computer> page;
        
        // search logic
        String searchedKeyWords = request.getParameter("search");
        if(searchedKeyWords != null) {
            page = cdb.filterByName(searchedKeyWords, offset, resultsPerPage);
            request.setAttribute("searchedKeyWords", searchedKeyWords);
            request.setAttribute("uri", request.getRequestURI() + "?search=" + searchedKeyWords);
        } else {
            page = cdb.listComputers(
                    offset,
                    resultsPerPage
                    );
            request.setAttribute("uri", request.getRequestURI());
        }
        List<ComputerDTO> computers = page.getContent().stream()
                .map(c -> new ComputerDTO(c))
                .collect(Collectors.toList());
        
        request.setAttribute("resultsFound", page.getTotal(true));
        request.setAttribute("page", page);
        request.setAttribute("computers", computers);
        request.getRequestDispatcher(DASHBOARD_JSP).forward(request, response);  
    }

    private void addComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug(ADD_COMPUTER_URL);
        Computer computerData = null;
        //check if we got form data from request
        if(request.getParameter("computerName") != null
                && request.getParameter("computerName").length() != 0
                && request.getParameter("introduced") != null
                && request.getParameter("discontinued") != null) {	        
            computerData = new Computer(
                    null,
                    request.getParameter("computerName"),
                    request.getParameter("introduced").length() == 0 ? null
                            : LocalDate.parse(request.getParameter("introduced"), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                            request.getParameter("discontinued").length() == 0 ? null
                                    : LocalDate.parse(request.getParameter("discontinued"), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                                    null
                    );
        }
        //if we did not get form data we dispatch the add_computer jsp file.
        if(computerData == null) {
            request.setAttribute("companies", COMPANIES);
            request.getRequestDispatcher(ADD_COMPUTER_JSP).forward(request, response);
        } else {
            // read the company id from request
            Long companyId = null;
            if(request.getParameter("companyId") != null) {
                companyId = Long.valueOf(request.getParameter("companyId"));
            }
            // attempt to create computer, if we can't we redirect to ADD_COMPUTER_JSP, else to EDIT_COMPUTER_JSP
            Computer newComputer = null;
            try {
                newComputer = cdb.createComputer(computerData, companyId);
            } catch (WrongObjectStateException e) {
                LOGGER.debug(ADD_COMPUTER_URL,e);
                request.setAttribute("warning", e.getMessage());
            } finally {
                if(newComputer == null) {
                    // should maybe help prefill form
                    request.setAttribute("companies", COMPANIES);
                    request.getRequestDispatcher(ADD_COMPUTER_JSP).forward(request, response);
                } else {
                    // redirect to edit page
                    response.sendRedirect(request.getContextPath() + EDIT_COMPUTER_URL + "?pk=" + newComputer.getId());
                }
            }
        }
    }

    private void editComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug(EDIT_COMPUTER_URL);
        request.setAttribute("companies", COMPANIES);
        
        Computer computerData = null;
        //check if we got form data from request
        if(request.getParameter("id") != null
                && request.getParameter("computerName") != null
                && request.getParameter("computerName").length() != 0
                && request.getParameter("introduced") != null
                && request.getParameter("discontinued") != null) {          
            computerData = new Computer(
                    Long.valueOf(request.getParameter("id")),
                    request.getParameter("computerName"),
                    request.getParameter("introduced").length() == 0 ? null
                            : LocalDate.parse(request.getParameter("introduced"), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                            request.getParameter("discontinued").length() == 0 ? null
                                    : LocalDate.parse(request.getParameter("discontinued"), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                                    null
                    );
        }
        //if we did not get form data we get the computer data frmo database
        if(computerData == null) {
            Long computerId = Long.valueOf(
                    request.getParameter("pk"));
            computerData = new Computer();
            computerData.setId(computerId);
            try {
                computerData = cdb.showComputerDetails(computerData);
            } catch (WrongObjectStateException e) {
                e.printStackTrace();
            }
            request.setAttribute("computer", computerData);
            request.getRequestDispatcher(EDIT_COMPUTER_JSP).forward(request, response);
        } else { // if we got form data
            // read the company id from request
            Long companyId = null;
            if(request.getParameter("companyId") != null) {
                companyId = Long.valueOf(request.getParameter("companyId"));
            }
            Computer updatedComputer = null;
            try { // update the computer info in the database
                updatedComputer = cdb.updateComputer(computerData, companyId);
                LOGGER.debug(updatedComputer.toString());
            } catch (WrongObjectStateException e) {
                LOGGER.debug(EDIT_COMPUTER_URL,e);
                request.setAttribute("warning", e.getMessage());
            } finally { // return to edit page
                if(updatedComputer == null) {
                    request.setAttribute("computer", computerData);
                } else {
                    request.setAttribute("computer", updatedComputer);
                    request.setAttribute("updatedWithSuccess", true);
                }
                request.getRequestDispatcher(EDIT_COMPUTER_JSP).forward(request, response);
            }
        }
    }

}
