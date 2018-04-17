package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.excilys.gradureau.computer_database.model.Company;

public class CompanyDAO extends DAO<Company> {
	
	private final String QUERY_FIND_ALL = "SELECT * FROM company;";
	private final String QUERY_FIND = "SELECT * FROM company WHERE id = ?;";

	public CompanyDAO(Connection connection) {
		super(connection);
	}

	@Override
	public Company find(long id) {
		Company company = null;
		try {
			PreparedStatement ps = connection.prepareStatement(QUERY_FIND);
			ps.setLong(1, id);
			ResultSet res = ps.executeQuery();
			if(res.first())
				company = new Company(
						id, 
						res.getString("name")
						);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return company;
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
	public void delete(Company obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Company> findAll() {
		List<Company> companies = new LinkedList<Company>();
		try {
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(QUERY_FIND_ALL);
			while(res.next()) {
				companies.add(
						new Company(
								res.getLong("id"),
								res.getString("name")
						)
				);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return companies;
	}

}
