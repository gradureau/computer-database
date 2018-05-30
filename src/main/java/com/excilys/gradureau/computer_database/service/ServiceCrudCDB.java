package com.excilys.gradureau.computer_database.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.dao.ComputerDAO;
import com.excilys.gradureau.computer_database.persistance.dao.DAO;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.validator.CompanyValidator;
import com.excilys.gradureau.computer_database.validator.ComputerValidator;

@Service
public class ServiceCrudCDB implements ICrudCDB {

    @Autowired
    private DAO<Company> companyDAO;
    @Autowired
    private DAO<Computer> computerDAO;

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
        Map<String,String> criterias = new HashMap<>();
        criterias.put(ComputerDAO.Fields.COMPUTER_NAME.getSqlAlias(), nameFilter);
        criterias.put(ComputerDAO.Fields.COMPANY_NAME.getSqlAlias(), nameFilter);
        return computerDAO.filterBy(criterias, start, resultsCount);
    }
    
    @Override
    public boolean deleteCompany(Company company) throws WrongObjectStateException {
        CompanyValidator.checkId(company);
        return companyDAO.delete(company);
    }

    @Override
    public int countCompanies() {
        return (int) companyDAO.count();
    }

    @Override
    public int countComputers() {
        return (int) computerDAO.count();
    }

}
