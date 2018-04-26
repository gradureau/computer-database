package com.excilys.gradureau.computer_database.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet(urlPatterns = {
        MainServlet.DASHBOARD_URL,
        MainServlet.ADD_COMPUTER_URL,
        MainServlet.EDIT_COMPUTER_URL
}, loadOnStartup = 0)
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MainServlet.class);

	private ICrudCDB cdb;
	
	private static final String DB_CONFIG_FILEPATH = "database.properties";
	
	//URLS
	public static final String DASHBOARD_URL = "/dashboard";
	public static final String ADD_COMPUTER_URL = "/add-computer";
	public static final String EDIT_COMPUTER_URL = "/edit-computer";
	
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
		}
	}
	
	private void dashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    List<ComputerDTO> computers = cdb.listComputers().getContent().stream()
	    .map(c -> new ComputerDTO(c))
	    .collect(Collectors.toList());
	    request.setAttribute("computers", computers);
	    request.getRequestDispatcher(DASHBOARD_JSP).forward(request, response);  
	}
	
	private void addComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Computer computerData = null;
	    LOGGER.debug(ADD_COMPUTER_URL, request.getParameterMap().toString());
	    LOGGER.debug(ADD_COMPUTER_URL, request.getParameterNames());
	    LOGGER.debug(ADD_COMPUTER_URL, request.getParameter("computerName"));
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
            } finally {
                if(newComputer == null) {
                    // should maybe help prefill form
                    request.getRequestDispatcher(ADD_COMPUTER_JSP).forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + EDIT_COMPUTER_URL + "?pk=" + newComputer.getId());
                }
            }
	    }
    }
	
	private void editComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long computerId = Long.valueOf(
                request.getParameter("pk"));
        Computer computerData = new Computer();
        computerData.setId(computerId);
        try {
            computerData = cdb.showComputerDetails(computerData);
        } catch (WrongObjectStateException e) {
            e.printStackTrace();
        }
        request.setAttribute("computer", computerData);
        request.getRequestDispatcher(EDIT_COMPUTER_JSP).forward(request, response);
    }

}
