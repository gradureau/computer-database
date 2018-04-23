package com.excilys.gradureau.computer_database.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class CompanyTest {
    @Before
    public void setUp() {
    	MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void constructorsTest() {
		assertTrue( new Company() instanceof Company);
		assertTrue( new Company(1L,"testName") instanceof Company);
		assertTrue( new Company(null,"testName") instanceof Company);
		assertTrue( new Company(1L,null) instanceof Company);
		assertTrue( new Company(null,null) instanceof Company);
	}
}
