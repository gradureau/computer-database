package com.excilys.gradureau.computer_database.persistance.dao;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;

public class DAOFactory {
    private DAOFactory() {}
    private static DAOFactory INSTANCE = new DAOFactory();
    public static DAOFactory getINSTANCE() {
        return INSTANCE;
    }

    public DAO<Company> getCompanyDAO() {
        return new CompanyDAO();
    }

    public DAO<Computer> getComputerDAO() {
        return new ComputerDAO();
    }

}
