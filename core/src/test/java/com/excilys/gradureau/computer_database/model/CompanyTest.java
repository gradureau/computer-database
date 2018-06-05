package com.excilys.gradureau.computer_database.model;


import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompanyTest {

    @Test
    public void constructorsTest() {
        assertTrue(new Company() instanceof Company);
        assertTrue(new Company(1L, "testName") instanceof Company);
        assertTrue(new Company(null, "testName") instanceof Company);
        assertTrue(new Company(1L, null) instanceof Company);
        assertTrue(new Company(null, null) instanceof Company);
    }
}
