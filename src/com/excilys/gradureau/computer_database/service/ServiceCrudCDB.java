package com.excilys.gradureau.computer_database.service;

import java.sql.Connection;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.dao.DAO;
import com.excilys.gradureau.computer_database.persistance.dao.DAOFactory;
import com.excilys.gradureau.computer_database.util.Page;

public class ServiceCrudCDB implements ICrudCDB {
	
	private DAO<Company> companyDAO;
	private DAO<Computer> computerDAO;

	public ServiceCrudCDB(Connection connection) {
		DAOFactory daoFactory = DAOFactory.getInstance(connection);
		companyDAO = daoFactory.getCompanyDAO();
		computerDAO = daoFactory.getComputerDAO();
	}

	@Override
	public Page<Computer> listComputers(int start, int resultsCount) {
		return computerDAO.pagination(start, resultsCount);
	}

	@Override
	public Page<Company> listCompanies(int start, int resultsCount) {
		return companyDAO.pagination(start, resultsCount);
	}

	@Override
	public Computer showComputerDetails(Computer computer) {
		return computerDAO.find(computer.getId());
	}

	@Override
	public Computer createComputer(Computer computer) {
		return computerDAO.create(computer);
	}

	@Override
	public Computer updateComputer(Computer computer) {
		return computerDAO.update(computer);
	}

	@Override
	public void deleteComputer(Computer computer) {
		computerDAO.delete(computer);
	}

}
