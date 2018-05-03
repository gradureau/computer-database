package com.excilys.gradureau.computer_database.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Computer> showComputerDetails(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        return computerDAO.find(computer.getId());
    }

    @Override
    public Optional<Computer> createComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer, false);
        ComputerValidator.check(computer);
        return computerDAO.create(computer);
    }

    @Override
    public Optional<Computer> createComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        ComputerValidator.checkId(computer, false);
        ComputerValidator.check(computer);
        if (companyId != null) {
            companyDAO.find(companyId)
            .ifPresent(company -> computer.setCompany(company));
        }
        return computerDAO.create(computer);
    }

    @Override
    public Optional<Computer> updateComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        ComputerValidator.check(computer);
        return computerDAO.update(computer);
    }

    @Override
    public Optional<Computer> updateComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        ComputerValidator.check(computer);
        if (companyId != null) {
            companyDAO.find(companyId)
            .ifPresent(company -> computer.setCompany(company));
        }
        return computerDAO.update(computer);
    }

    @Override
    public boolean deleteComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        return computerDAO.delete(computer);
    }

    @Override
    public Page<Computer> filterByName(String nameFilter, int start, int resultsCount) {
        String fieldName = "pc.name";
        Map<String,String> criterias = new HashMap<>();
        criterias.put(fieldName, nameFilter);
        return computerDAO.filterBy(criterias, start, resultsCount);
    }

}
