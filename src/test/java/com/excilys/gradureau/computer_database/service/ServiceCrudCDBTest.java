package com.excilys.gradureau.computer_database.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.HikariBasedDataSource;
import com.excilys.gradureau.computer_database.util.Page;

public class ServiceCrudCDBTest {
    private static final int INITIAL_NUMBER_OF_COMPANIES = 42;
    static final ServiceCrudCDB CDB = new ServiceCrudCDB(
            HikariBasedDataSource.init("hikari.properties")
            );

    @Test
    public void createComputerWithName() throws WrongObjectStateException {
        Computer niceComputer = new Computer(null, "computerName", null, null, null);
        assertTrue(CDB.createComputer(niceComputer).orElse(null) instanceof Computer);
    }

    @Test
    public void createComputerWithNameAndNiceDates() throws WrongObjectStateException {
        LocalDateTime now = LocalDateTime.now().minusYears(30L);
        LocalDateTime beforeNow = now.minusDays(1);
        Computer niceComputerWithDates = new Computer(null, "computerName", beforeNow, now, null);
        assertTrue(CDB.createComputer(niceComputerWithDates).orElse(null) instanceof Computer);
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
        assertTrue(CDB.createComputer(computerWithExistingCompanyId, 1L).orElse(null).getCompany().getName().equals("Apple Inc."));
    }

    @Test
    public void createComputerWithNonExistingCompanyId() throws WrongObjectStateException {
        Computer computerWithNonExistingCompanyId = new Computer(null, "computerName", null, null, null);
        assertTrue(CDB.createComputer(computerWithNonExistingCompanyId, Long.MIN_VALUE).orElse(null).getCompany() == null);
    }

    @Test
    public void showComputerDetailsFindComputerData() throws WrongObjectStateException {
        Computer computerWithId = new Computer();
        computerWithId.setId(1L);
        Computer computerFound = CDB.showComputerDetails(computerWithId).orElse(null);
        assertTrue(computerFound.getName().equals("MacBook Pro 15.4 inch"));
    }
    
    @Test
    public void showComputerDetailsFindIntroducedDate() throws WrongObjectStateException {
        Computer computerWithIntroducedDate = new Computer();
        computerWithIntroducedDate.setId(43L);
        Computer computerFound = CDB.showComputerDetails(computerWithIntroducedDate).orElse(null);
        assertEquals(LocalDate.parse("1993-10-21", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                computerFound.getIntroduced());
    }

    @Test
    public void showComputerDetailsFindCompanyData() throws WrongObjectStateException {
        Computer computerWithId = new Computer();
        computerWithId.setId(1L);
        Computer computerFound = CDB.showComputerDetails(computerWithId).orElse(null);
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
        assertTrue(CDB.deleteComputer(computerWithExistingId));
    }

    @Test
    public void deleteComputerWithNonExistingComputerId() throws WrongObjectStateException {
        Computer computerWithNonExistingId = new Computer();
        computerWithNonExistingId.setId(Long.MIN_VALUE);
        assertFalse(CDB.deleteComputer(computerWithNonExistingId));
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
        assertEquals("updatedName", CDB.updateComputer(computerWithId).orElse(null).getName());
    }
    
    @Test
    public void updateComputerIntroducedDate() throws WrongObjectStateException {
        LocalDateTime date = LocalDateTime.now().minusYears(30L);
        Computer computerWithNewIntroducedDate = new Computer(3L, "updatedName", date, null, null);
        assertEquals(date, CDB.updateComputer(computerWithNewIntroducedDate).orElse(null).getIntroduced());
    }

    @Test
    public void updateComputerWithNonExistingComputerId() throws WrongObjectStateException {
        Computer computerWithId = new Computer(Long.MIN_VALUE, "updatedName", null, null, null);
        assertEquals(Optional.empty(), CDB.updateComputer(computerWithId));
    }

    @Test
    public void updateComputerWithCompanyId() throws WrongObjectStateException {
        Computer computerWithExistingCompanyId = new Computer(4L, "updated computer name", null, null, null);
        Computer result = CDB.updateComputer(computerWithExistingCompanyId, 1L).orElse(null);
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
    
    @Test
    public void deleteCompanyWithExistingCompanyId() throws WrongObjectStateException {
        Company companyWithExistingId = new Company();
        companyWithExistingId.setId(7L);
        Computer knownComputerFromDeletedCompany = new Computer(56L,null,null,null, companyWithExistingId);
        
        assertTrue(CDB.deleteCompany(companyWithExistingId));
        
        assertFalse(CDB.showComputerDetails(knownComputerFromDeletedCompany).isPresent());
    }
    
    @Test
    public void deleteCompanyWithNonExistingCompanyId() throws WrongObjectStateException {
        Company companyWithNonExistingId = new Company();
        companyWithNonExistingId.setId(Long.MIN_VALUE);
        assertFalse(CDB.deleteCompany(companyWithNonExistingId));
    }
    
    @Test
    public void deleteCompanyWithNoCompanyId() {
        Company companyWithNoId = new Company();
        assertThrows(WrongObjectStateException.class, () -> CDB.deleteCompany(companyWithNoId));
    }
    
    @Test
    public void filterByName() {
        Page<Computer> page = CDB.filterByName("ok", 0, 5);
        assertSame(8L, page.getTotal(true));
        assertSame(3, page.getNextPage().getContent().size());
    }
    
    @Test
    public void countCompanies() {
        int numberOfCompanies = CDB.countCompanies();
        assertTrue( numberOfCompanies == INITIAL_NUMBER_OF_COMPANIES
                || numberOfCompanies == INITIAL_NUMBER_OF_COMPANIES-1);
    }
    
    @Test
    public void countComputers() {
        int numberOfComputers = CDB.countComputers();
        assertTrue(60L < numberOfComputers && numberOfComputers < 65L);
    }

}
