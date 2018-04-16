package com.excilys.gradureau.computer_database.service;

import java.util.List;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;

public interface ICrudCDB {
	List<Computer> listComputers();
	List<Company> listCompanies();
	void showComputerDetails(Computer computer);
	void createComputer(Computer computer);
	void updateComputer(Computer computer);
	void deleteComuter(Computer computer);
}
