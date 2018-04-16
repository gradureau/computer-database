package com.excilys.gradureau.computer_database.service;

import java.sql.Connection;
import java.util.List;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.dao.DAO;
import com.excilys.gradureau.computer_database.persistance.dao.DAOFactory;

public class ServiceCrudCDB implements ICrudCDB {
	
	private DAO<Company> companyDAO;
	private DAO<Computer> computerDAO;

	public ServiceCrudCDB(Connection connection) {
		DAOFactory daoFactory = DAOFactory.getInstance(connection);
		companyDAO = daoFactory.getCompanyDAO();
		computerDAO = daoFactory.getComputerDAO();
	}

	@Override
	public List<Computer> listComputers() {
		return computerDAO.findAll();
	}

	@Override
	public List<Company> listCompanies() {
		return companyDAO.findAll();
	}

	@Override
	public Computer showComputerDetails(Computer computer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Computer createComputer(Computer computer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Computer updateComputer(Computer computer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteComputer(Computer computer) {
		// TODO Auto-generated method stub

	}

}
