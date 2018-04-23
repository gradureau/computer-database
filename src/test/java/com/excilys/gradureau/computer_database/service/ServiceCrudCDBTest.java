package com.excilys.gradureau.computer_database.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Properties;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

public class ServiceCrudCDBTest {
    static Properties databaseProperties = PropertyFileUtility.readPropertyFile("database.properties");
    
    static Connection connection = ConnectionMysqlSingleton.getInstance(
            databaseProperties.getProperty("DB_URL"),
            databaseProperties.getProperty("DB_USER"),
            databaseProperties.getProperty("DB_PASSWORD"));
    @Spy
    static final ServiceCrudCDB CDB = new ServiceCrudCDB(connection);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createComputerWithName() throws WrongObjectStateException {
        Computer niceComputer = new Computer(null, "computerName", null, null, null);
        assertTrue(CDB.createComputer(niceComputer) instanceof Computer);
    }

    @Test
    public void createComputerWithNameAndNiceDates() throws WrongObjectStateException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beforeNow = now.minusDays(1);
        Computer niceComputerWithDates = new Computer(null, "computerName", beforeNow, now, null);
        assertTrue(CDB.createComputer(niceComputerWithDates) instanceof Computer);
    }

    @Test
    public void createComputerWithNoName() {
        Computer computerWithNoName = new Computer(null, null, null, null, null);
        assertThrows(WrongObjectStateException.class, () -> CDB.createComputer(computerWithNoName));
    }

    @Test
    public void createComputerWithId() {
        Computer computerWithId = new Computer(1L, "computerName", null, null, null);
        assertThrows(WrongObjectStateException.class, () -> CDB.createComputer(computerWithId));
    }

    @Test
    public void createComputerWithWrongCombinationOfDates() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beforeNow = now.minusDays(1);
        Computer computerWithWrongCombinationOfDates = new Computer(null, "computerName", now, beforeNow, null);
        assertThrows(WrongObjectStateException.class, () -> CDB.createComputer(computerWithWrongCombinationOfDates));
    }
}
