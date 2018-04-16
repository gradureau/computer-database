package com.excilys.gradureau.computer_database.service;

import java.util.List;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;

public interface ICrudCDB {
	/**
	 * @return fresh computer list from database
	 */
	List<Computer> listComputers();
	/**
	 * @return fresh company list from database
	 */
	List<Company> listCompanies();
	/**
	 * @param computer with a defined Long id
	 * @return the computer object with updated values from database
	 */
	Computer showComputerDetails(Computer computer);
	/**
	 * @param computer without a defined Long id
	 * @return the computer object with a defined Long id used in database
	 */
	Computer createComputer(Computer computer);
	/**
	 * @param computer with a defined Long id
	 * @return the computer after having updated database values
	 */
	Computer updateComputer(Computer computer);
	/**
	 * @param computer to be deleted in database
	 */
	void deleteComputer(Computer computer);
}
