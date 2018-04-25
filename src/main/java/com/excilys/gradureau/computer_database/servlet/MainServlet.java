package com.excilys.gradureau.computer_database.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.dto.DTOMapper;
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
	    cdb.listComputers().getContent().stream()
	    .map(DTOMapper::toComputerDTO)
	    .forEach(c -> LOGGER.debug(c.toString()));
	    request.getRequestDispatcher(DASHBOARD_JSP).forward(request, response);  
	}
	
	private void addComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(ADD_COMPUTER_JSP).forward(request, response);  
    }
	
	private void editComputer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(EDIT_COMPUTER_JSP).forward(request, response);  
    }


}
