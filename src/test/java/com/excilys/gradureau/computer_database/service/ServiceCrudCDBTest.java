package com.excilys.gradureau.computer_database.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

public class ServiceCrudCDBTest {
    static Properties databaseProperties = PropertyFileUtility.readPropertyFile("database.properties");
    static Connection connection = ConnectionMysqlSingleton.getInstance(databaseProperties.getProperty("DB_URL"),
            databaseProperties.getProperty("DB_USER"), databaseProperties.getProperty("DB_PASSWORD"));

    static final ServiceCrudCDB CDB = new ServiceCrudCDB(connection);

    @Test
    public void createComputerWithName() throws WrongObjectStateException {
        Computer niceComputer = new Computer(null, "computerName", null, null, null);
        assertTrue(CDB.createComputer(niceComputer) instanceof Computer);
    }

    @Test
    public void createComputerWithNameAndNiceDates() throws WrongObjectStateException {
        LocalDateTime now = LocalDateTime.now().minusYears(30L);
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
        LocalDateTime now = LocalDateTime.now().minusYears(30L);
        LocalDateTime beforeNow = now.minusDays(1);
        Computer computerWithWrongCombinationOfDates = new Computer(null, "computerName", now, beforeNow, null);
        assertThrows(WrongObjectStateException.class, () -> CDB.createComputer(computerWithWrongCombinationOfDates));
    }

    @Test
    public void createComputerWithExistingCompanyId() throws WrongObjectStateException {
        Computer computerWithExistingCompanyId = new Computer(null, "computerName", null, null, null);
        assertTrue(CDB.createComputer(computerWithExistingCompanyId, 1L).getCompany().getName().equals("Apple Inc."));
    }

    @Test
    public void createComputerWithNonExistingCompanyId() throws WrongObjectStateException {
        Computer computerWithNonExistingCompanyId = new Computer(null, "computerName", null, null, null);
        assertTrue(CDB.createComputer(computerWithNonExistingCompanyId, Long.MAX_VALUE).getCompany() == null);
    }

    @Test
    public void showComputerDetailsFindComputerData() throws WrongObjectStateException {
        Computer computerWithId = new Computer();
        computerWithId.setId(1L);
        Computer computerFound = CDB.showComputerDetails(computerWithId);
        assertTrue(computerFound.getName().equals("MacBook Pro 15.4 inch"));
    }
    
    @Test
    public void showComputerDetailsFindIntroducedDate() throws WrongObjectStateException {
        Computer computerWithIntroducedDate = new Computer();
        computerWithIntroducedDate.setId(43L);
        Computer computerFound = CDB.showComputerDetails(computerWithIntroducedDate);
        assertEquals(LocalDate.parse("1993-10-21", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                computerFound.getIntroduced());
    }

    @Test
    public void showComputerDetailsFindCompanyData() throws WrongObjectStateException {
        Computer computerWithId = new Computer();
        computerWithId.setId(1L);
        Computer computerFound = CDB.showComputerDetails(computerWithId);
        assertTrue(computerFound.getCompany().getName().equals("Apple Inc."));
    }

    @Test
    public void showComputerDetailsNoId() {
        Computer computerWithNoId = new Computer();
        assertThrows(WrongObjectStateException.class, () -> CDB.showComputerDetails(computerWithNoId));
    }

    @Test
    public void deleteComputerWithExistingComputerId() throws WrongObjectStateException {
        Computer computerWithExistingId = new Computer();
        computerWithExistingId.setId(7L);
        CDB.deleteComputer(computerWithExistingId);
    }

    @Test
    public void deleteComputerWithNonExistingComputerId() throws WrongObjectStateException {
        Computer computerWithNonExistingId = new Computer();
        computerWithNonExistingId.setId(Long.MAX_VALUE);
        CDB.deleteComputer(computerWithNonExistingId);
    }

    @Test
    public void deleteComputerWithNoComputerId() {
        Computer computerWithNoId = new Computer();
        assertThrows(WrongObjectStateException.class, () -> CDB.deleteComputer(computerWithNoId));
    }

    @Test
    public void updateComputerWithNoComputerId() {
        Computer computerWithNoId = new Computer();
        assertThrows(WrongObjectStateException.class, () -> CDB.updateComputer(computerWithNoId));
    }

    @Test
    public void updateComputerWithComputerId() throws WrongObjectStateException {
        Computer computerWithId = new Computer(3L, "updatedName", null, null, null);
        assertEquals("updatedName", CDB.updateComputer(computerWithId).getName());
    }
    
    @Test
    public void updateComputerIntroducedDate() throws WrongObjectStateException {
        LocalDateTime date = LocalDateTime.now().minusYears(30L);
        Computer computerWithNewIntroducedDate = new Computer(3L, "updatedName", date, null, null);
        assertEquals(date, CDB.updateComputer(computerWithNewIntroducedDate).getIntroduced());
    }

    @Test
    public void updateComputerWithNonExistingComputerId() throws WrongObjectStateException {
        Computer computerWithId = new Computer(Long.MAX_VALUE, "updatedName", null, null, null);
        assertEquals(null, CDB.updateComputer(computerWithId));
    }

    @Test
    public void updateComputerWithCompanyId() throws WrongObjectStateException {
        Computer computerWithExistingCompanyId = new Computer(4L, "updated computer name", null, null, null);
        Computer result = CDB.updateComputer(computerWithExistingCompanyId, 1L);
        assertEquals("updated computer name", result.getName());
        assertEquals("Apple Inc.", result.getCompany().getName());
    }

    @Test
    public void listCompaniesDefault() {
        int defaultPageSize = 20;
        Long firstCompanyIdInAlphanumericOrder = 22L;
        Page<Company> companiesPage = CDB.listCompanies();
        assertSame(defaultPageSize, companiesPage.getContent().size());
        assertSame(firstCompanyIdInAlphanumericOrder, companiesPage.getContent().get(0).getId());
    }

    @Test
    public void listCompaniesOffset() {
        int offset = 3;
        int pageSize = 15;
        Long expectedCompanyId = 38L;
        Page<Company> companiesPage = CDB.listCompanies(offset, pageSize);
        assertSame(pageSize, companiesPage.getContent().size());
        assertSame(expectedCompanyId, companiesPage.getContent().get(0).getId());
    }

    @Test
    public void listComputersDefault() {
        int defaultPageSize = 20;
        Long firstComputerIdInAlphanumericOrder = 27L;
        Page<Computer> computersPage = CDB.listComputers();
        assertSame(defaultPageSize, computersPage.getContent().size());
        assertSame(firstComputerIdInAlphanumericOrder, computersPage.getContent().get(0).getId());
    }

    @Test
    public void listComputersOffset() {
        int offset = 3;
        int pageSize = 15;
        Long expectedComputerId = 25L;
        Page<Computer> computersPage = CDB.listComputers(offset, pageSize);
        assertSame(pageSize, computersPage.getContent().size());
        assertSame(expectedComputerId, computersPage.getContent().get(0).getId());
    }

}
