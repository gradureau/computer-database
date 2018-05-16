package com.excilys.gradureau.computer_database.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.excilys.gradureau.computer_database.dto.ComputerDTO;
import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.util.Page;

@WebServlet(urlPatterns = {
        MainServlet.DASHBOARD_URL,
        MainServlet.ADD_COMPUTER_URL,
        MainServlet.EDIT_COMPUTER_URL,
        MainServlet.DELETE_COMPUTER_URL
}, loadOnStartup = 0)
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainServlet.class);
    @Autowired(required = true)
    private ICrudCDB cdb;
    @Autowired(required = true)
    @Qualifier("COMPANIES")
    private List<Company> COMPANIES;

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
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
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
        
        Optional<Computer> optionalComputerData = Utilities.mapRequestToOptionalComputerWithoutCompany(request, false);
        
        if(!optionalComputerData.isPresent()) {
            request.setAttribute("companies", COMPANIES);
            request.getRequestDispatcher(ADD_COMPUTER_JSP).forward(request, response);
        } else {
            Computer computerData = optionalComputerData.get();
            
            Long companyId = null;
            if(request.getParameter("companyId") != null) {
                companyId = Long.valueOf(request.getParameter("companyId"));
            }
            // attempt to create computer, if we can't we redirect to ADD_COMPUTER_JSP, else to EDIT_COMPUTER_JSP
            Optional<Computer> optionalNewComputer = Optional.empty();
            try {
                optionalNewComputer = cdb.createComputer(computerData, companyId);
            } catch (WrongObjectStateException e) {
                LOGGER.debug(ADD_COMPUTER_URL,e);
                request.setAttribute("warning", e.getMessage());
            } finally {
                if(!optionalNewComputer.isPresent()) {
                    // known issue : front end does not check introducedDate <= discontinuedDate
                    LOGGER.error(ADD_COMPUTER_JSP + " FRONTEND VALIDATION SHOULD HAVE TAKEN CARE OF THIS");
                    request.setAttribute("companies", COMPANIES);
                    request.getRequestDispatcher(ADD_COMPUTER_JSP).forward(request, response);
                } else {
                    // redirect to edit page
                    response.sendRedirect(request.getContextPath() + EDIT_COMPUTER_URL + "?pk=" + optionalNewComputer.get().getId());
                }
            }
        }
    }

    private void editComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug(EDIT_COMPUTER_URL);
        request.setAttribute("companies", COMPANIES);
        
        Optional<Computer> optionalComputerData = Utilities.mapRequestToOptionalComputerWithoutCompany(request);
        
        if(!optionalComputerData.isPresent()) {
            fillEditFormDataAndForward(request, response);
        } else {
            Computer computerData = optionalComputerData.get();
            
            Long companyId = null;
            if(request.getParameter("companyId") != null) {
                companyId = Long.valueOf(request.getParameter("companyId"));
            }
            
            Optional<Computer> optionalUpdatedComputer = Optional.empty();
            try { // update the computer info in the database
                optionalUpdatedComputer = cdb.updateComputer(computerData, companyId);
            } catch (WrongObjectStateException e) {
                LOGGER.debug(EDIT_COMPUTER_URL,e);
                request.setAttribute("warning", e.getMessage());
            } finally { // return to edit page
                if(!optionalUpdatedComputer.isPresent()) {
                    request.setAttribute("computer", computerData);
                } else {
                    request.setAttribute("computer", optionalUpdatedComputer.get());
                    request.setAttribute("updatedWithSuccess", true);
                }
                request.getRequestDispatcher(EDIT_COMPUTER_JSP).forward(request, response);
            }
        }
        
    }

    private void fillEditFormDataAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Computer computerData;
        Long computerId = Long.valueOf(
                request.getParameter("pk"));
        computerData = new Computer();
        computerData.setId(computerId);
        try {
            computerData = cdb.showComputerDetails(computerData).orElse(null);
        } catch (WrongObjectStateException e) {
            e.printStackTrace();
            //impossible since we're sure to have an Id, given a wrong "pk" parameter gives a straitght 404 status code
        }
        if(computerData == null) {
            response.sendError(HttpStatus.SC_NOT_FOUND);
        } else {
            request.setAttribute("computer", computerData);
            request.getRequestDispatcher(EDIT_COMPUTER_JSP).forward(request, response);
        }
    }

}
