package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;

public class DAOFactory {
    /**
     * Singleton Factory class
     * A database connection can be specified with
     * DAOFactory::getInstance(Connection connection)
     * or later with
     * DAOFactory::setConnection(Connection connection)
     * The connection reference is then shared by all DAO<T> requested.
     */

    private Connection connection;
    private static DAOFactory instance;

    private DAOFactory(Connection connection) {
        this.connection = connection;
    }

    public static DAOFactory getInstance(Connection connection) {
        if (DAOFactory.instance == null) {
            DAOFactory.instance = new DAOFactory(connection);
        } else {
            if (!connection.equals(DAOFactory.instance.connection)) {
                DAOFactory.instance.connection = connection;
            }
        }
        return DAOFactory.instance;
    }

    public static DAOFactory getInstance() {
        return getInstance(null);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public DAO<Company> getCompanyDAO() {
        return new CompanyDAO(DAOFactory.instance.connection);
    }

    public DAO<Computer> getComputerDAO() {
        return new ComputerDAO(DAOFactory.instance.connection);
    }

}
