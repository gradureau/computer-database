package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;

import com.excilys.gradureau.computer_database.model.Computer;

public class ComputerDAO extends DAO<Computer> {

	public ComputerDAO(Connection connection) {
		super(connection);
	}

	@Override
	public Computer find(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Computer create(Computer obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Computer update(Computer obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Computer obj) {
		// TODO Auto-generated method stub
	}

}
