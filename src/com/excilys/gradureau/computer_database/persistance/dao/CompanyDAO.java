package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;

import com.excilys.gradureau.computer_database.model.Company;

public class CompanyDAO extends DAO<Company> {

	public CompanyDAO(Connection connection) {
		super(connection);
	}

	@Override
	public Company find(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Company create(Company obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Company update(Company obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Company delete(Company obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
