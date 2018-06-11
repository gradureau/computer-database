package com.excilys.gradureau.computer_database.service;

import java.util.Optional;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.util.Page;

public interface ICrudCDB {
    /**
     * @return fresh computer list from database
     */
    default Page<Computer> listComputers() {
        return listComputers(0, 20);
    }

    Page<Computer> listComputers(int start, int resultsNumber);

    /**
     * @return fresh company list from database
     */
    default Page<Company> listCompanies() {
        return listCompanies(0, 20);
    }

    Page<Company> listCompanies(int start, int resultsCount);

    /** 
     * @param computer
     *            with a defined Long id
     * @return the computer object with updated values from database
     * @throws WrongObjectStateException if Computer.id not set
     */
    Optional<Computer> showComputerDetails(Computer computer) throws WrongObjectStateException;
    
    /** 
     * @param company
     *            with a defined Long id
     * @return the company object with updated values from database
     * @throws WrongObjectStateException if Company.id not set
     */
    Optional<Company> showCompanyDetails(Company computer);

    /**
     * @param computer
     *            without a defined Long id
     * @return the computer object with a defined Long id used in database
     * @throws WrongObjectStateException if Computer.id is already set, does not have a name
     * or discontinued date is before introduced date
     */
    Optional<Computer> createComputer(Computer computer) throws WrongObjectStateException;

    /**
     * @param computer Computer without a defined Long id
     * @param companyId Long is the id of a Company object present in database
     * @return the computer object with a defined Long id used in database
     * @throws WrongObjectStateException if Computer.id is already set, does not have a name
     * or discontinued date is before introduced date
     */
    Optional<Computer> createComputer(Computer computer, Long companyId) throws WrongObjectStateException;

    /**
     * @param computer
     *            with a defined Long id
     * @return the computer after having updated database values
     * @throws WrongObjectStateException if Computer.id is already set, does not have a name
     * or discontinued date is before introduced date
     */
    Optional<Computer> updateComputer(Computer computer) throws WrongObjectStateException;

    /**
     * @param computer Computer with a defined Long id
     * @param companyId Long is the id of a Company object present in database
     * @return the computer after having updated database values
     * @throws WrongObjectStateException if Computer.id is already set, does not have a name
     * or discontinued date is before introduced date
     */
    Optional<Computer> updateComputer(Computer computer, Long companyId) throws WrongObjectStateException;

    /**
     * @param computer Computer to be deleted in database with a defined Long id
     * @throws WrongObjectStateException if Computer.id is not set.
     */
    boolean deleteComputer(Computer computer) throws WrongObjectStateException;
    
    /**
     * @param nameFilter
     * @return 
     */
    Page<Computer> filterByName(String nameFilter, int start, int resultsCount);
    default Page<Computer> filterByName(String nameFilter) {
        return filterByName(nameFilter, 0, 20);
    }
    
    /**
     * @param company Company to be deleted in database with a defined Long id
     * @throws WrongObjectStateException if Company.id is not set.
     */
    boolean deleteCompany(Company company) throws WrongObjectStateException;
    
    /**
     * @return number of companies in database
     */
    int countCompanies();
    
    /**
     * @return number of computers in database
     */
    int countComputers();
}
