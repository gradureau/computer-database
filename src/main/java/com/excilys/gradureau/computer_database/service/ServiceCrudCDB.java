package com.excilys.gradureau.computer_database.service;

import java.sql.Connection;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.dao.DAO;
import com.excilys.gradureau.computer_database.persistance.dao.DAOFactory;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.validator.ComputerValidator;

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
    public Computer showComputerDetails(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        return computerDAO.find(computer.getId());
    }

    @Override
    public Computer createComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer, false);
        ComputerValidator.check(computer);
        return computerDAO.create(computer);
    }

    @Override
    public Computer createComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        ComputerValidator.checkId(computer, false);
        ComputerValidator.check(computer);
        if (companyId != null) {
            Company company = companyDAO.find(companyId);
            computer.setCompany(company);
        }
        return computerDAO.create(computer);
    }

    @Override
    public Computer updateComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        ComputerValidator.check(computer);
        return computerDAO.update(computer);
    }

    @Override
    public Computer updateComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        ComputerValidator.check(computer);
        if (companyId != null) {
            Company company = companyDAO.find(companyId);
            computer.setCompany(company);
        }
        return computerDAO.update(computer);
    }

    @Override
    public void deleteComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        computerDAO.delete(computer);
    }

}
