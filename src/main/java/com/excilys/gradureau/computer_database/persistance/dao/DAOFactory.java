package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.util.Optional;
import java.util.function.Supplier;

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

    private Supplier<Optional<Connection>> connectionSupplier;
    private static DAOFactory instance;

    private DAOFactory(Supplier<Optional<Connection>> supplier) {
        this.connectionSupplier = supplier;
    }

    public static DAOFactory getInstance(Supplier<Optional<Connection>> supplier) {
        if (DAOFactory.instance == null) {
            DAOFactory.instance = new DAOFactory(supplier);
        } else {
            if (!supplier.equals(DAOFactory.instance.connectionSupplier)) {
                DAOFactory.instance.connectionSupplier = supplier;
            }
        }
        return DAOFactory.instance;
    }

    public static DAOFactory getInstance() {
        return getInstance(null);
    }

    public Supplier<Optional<Connection>> getConnectionSupplier() {
        return connectionSupplier;
    }

    public void setConnectionSupplier(Supplier<Optional<Connection>> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    public DAO<Company> getCompanyDAO() {
        return new CompanyDAO(DAOFactory.instance.connectionSupplier);
    }

    public DAO<Computer> getComputerDAO() {
        return new ComputerDAO(DAOFactory.instance.connectionSupplier);
    }

}
