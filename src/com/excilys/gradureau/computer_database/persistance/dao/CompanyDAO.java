package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.util.Page;

public class CompanyDAO extends DAO<Company> {
	
	private static final String QUERY_FIND_ALL = "SELECT id, name FROM company;";
	private static final String QUERY_FIND = "SELECT id, name FROM company WHERE id = ?;";
	private static final String QUERY_LIMIT_ALL = "SELECT id, name FROM company LIMIT ?, ?;";

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
		List<Company> companies = new ArrayList<Company>();
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
	
	@Override
	public Page<Company> pagination(int start, int resultsCount) {
		List<Company> companies = new ArrayList<Company>();
		try {
			PreparedStatement ps = connection.prepareStatement(QUERY_LIMIT_ALL);
			ps.setInt(1, start);
			ps.setInt(2, resultsCount);
			ResultSet res = ps.executeQuery();
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
		return new Page<Company>(companies, start, resultsCount);
	}

}
