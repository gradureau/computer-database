package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;

public class ComputerDAO extends DAO<Computer> {
	
	private final String QUERY_FIND_ALL = "SELECT * FROM computer;";

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

	@Override
	public List<Computer> findAll() {
		List<Computer> computers = new LinkedList<Computer>();
		try {
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(QUERY_FIND_ALL);
			while(res.next()) {
				Company company = null;
				computers.add(
						new Computer(
								res.getLong("id"),
								res.getString("name"),
								timestamp2LocalDateTime(res.getTimestamp("introduced")),
								timestamp2LocalDateTime(res.getTimestamp("discontinued")),
								company
						)
				);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return computers;
	}
	
	private LocalDateTime timestamp2LocalDateTime(Timestamp t) {
		LocalDateTime res = null;
		if(t!=null) {
			LocalDateTime.ofInstant(
					Instant.ofEpochMilli(t.getTime()),
                    TimeZone.getDefault().toZoneId()
            );
		}
		return res;
	}

}
