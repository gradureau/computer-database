package com.excilys.gradureau.computer_database.persistance.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;

public class ComputerMapper {
	/**
	 * @param res, a java.sql.ResultSet filled
	 * and with the cursor set on the row containing the useful data.
	 * @return a Computer object filled with data
	 * @throws SQLException 
	 */
	public static Computer valueOf(ResultSet res) throws SQLException {
		Company company = null;
		if(res.getLong("company_id") != 0) {
			company = new Company(
					res.getLong("company_id"),
					res.getString("company_name")
					);
		}
		return new Computer(
				res.getLong("id"),
				res.getString("name"),
				TimeMapper.timestamp2LocalDateTime(res.getTimestamp("introduced")),
				TimeMapper.timestamp2LocalDateTime(res.getTimestamp("discontinued")),
				company
				);
	}
}
