package com.excilys.gradureau.computer_database.service;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.util.Page;

public interface ICrudCDB {
	/**
	 * @return fresh computer list from database
	 */
	default Page<Computer> listComputers() {
		return listComputers(1, 20);
	}
	Page<Computer> listComputers(int start, int resultsNumber);
	/**
	 * @return fresh company list from database
	 */
	default Page<Company> listCompanies() {
		return listCompanies(1, 20);
	}
	Page<Company> listCompanies(int start, int resultsCount);
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
	 * @param Computer computer without a defined Long id
	 * @param Long companyId is the id of a Company object present in database
	 * @return the computer object with a defined Long id used in database
	 */
	Computer createComputer(Computer computer, Long companyId);
	/**
	 * @param computer with a defined Long id
	 * @return the computer after having updated database values
	 */
	Computer updateComputer(Computer computer);
	/**
	 * @param computer with a defined Long id
	 * @param Long companyId is the id of a Company object present in database
	 * @return the computer after having updated database values
	 */
	Computer updateComputer(Computer computer, Long companyId);
	/**
	 * @param computer to be deleted in database
	 */
	void deleteComputer(Computer computer);
}
