package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.dao.mapper.ComputerMapper;
import com.excilys.gradureau.computer_database.persistance.dao.mapper.TimeMapper;
import com.excilys.gradureau.computer_database.util.Page;

public class ComputerDAO extends DAO<Computer> {

    private static final String QUERY_FIND_ALL = "SELECT pc.id as id, pc.name as name, introduced, discontinued, company_id, co.name as company_name "
            + "FROM computer AS pc LEFT JOIN company AS co on pc.company_id = co.id";
    private static final String QUERY_FIND = "SELECT id, name, introduced, discontinued, company_id FROM computer WHERE id = ?;";
    private static final String QUERY_CREATE = "INSERT INTO computer (name,introduced,discontinued,company_id) VALUES (?,?,?,?);";
    private static final String QUERY_UPDATE = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?;";
    private static final String QUERY_DELETE = "DELETE FROM computer WHERE id = ?;";
    private static final String QUERY_LIMIT_ALL = QUERY_FIND_ALL + " LIMIT ?, ?;";

    DAO<Company> companyDao;

    public ComputerDAO(Connection connection) {
        super(connection);
        companyDao = new CompanyDAO(connection);
    }

    @Override
    public Computer find(long id) {
        Computer computer = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_FIND);
            ps.setLong(1, id);
            ResultSet res = ps.executeQuery();
            if (res.first()) {
                Company company = null;
                if (res.getLong("company_id") != 0) {
                    company = companyDao.find(id);
                }
                computer = new Computer(res.getLong("id"), res.getString("name"),
                        TimeMapper.timestamp2LocalDateTime(res.getTimestamp("introduced")),
                        TimeMapper.timestamp2LocalDateTime(res.getTimestamp("discontinued")), company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return computer;
    }

    @Override
    public Computer create(Computer computer) {
        /*
         * Might as well return a boolean to inform about the outcome and set the Long
         * id of the Computer parameter object.
         */
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, computer.getName());
            ps.setTimestamp(2, computer.getIntroduced() == null ? null : Timestamp.valueOf(computer.getIntroduced()));
            ps.setTimestamp(3,
                    computer.getDiscontinued() == null ? null : Timestamp.valueOf(computer.getDiscontinued()));
            // if a computer has a company, it is supposed to be already present in the
            // database, as the company list is known to be constant.
            if (computer.getCompany() == null) {
                ps.setNull(4, Types.BIGINT);
            } else {
                ps.setLong(4, computer.getCompany().getId());
            }
            ps.executeUpdate();

            ResultSet autoGenerated = ps.getGeneratedKeys();
            if (autoGenerated.next()) {
                computer = find(autoGenerated.getLong(1)); // fill the Company part
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return computer;
    }

    @Override
    public Computer update(Computer computer) {
        // id presence is not implemented
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_UPDATE);
            ps.setString(1, computer.getName());
            ps.setTimestamp(2, computer.getIntroduced() == null ? null : Timestamp.valueOf(computer.getIntroduced()));
            ps.setTimestamp(3,
                    computer.getDiscontinued() == null ? null : Timestamp.valueOf(computer.getDiscontinued()));
            // if a computer has a company, it is supposed to be already present in the
            // database, as the company list is known to be constant.
            if (computer.getCompany() == null) {
                ps.setNull(4, Types.BIGINT);
            } else {
                ps.setLong(4, computer.getCompany().getId());
            }
            if (computer.getId() == null) {
                ps.setNull(5, Types.BIGINT);
            } else {
                ps.setLong(5, computer.getId());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return computer;
    }

    @Override
    public void delete(Computer computer) {
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_DELETE);
            ps.setLong(1, computer.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Computer> findAll() {
        List<Computer> computers = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet res = stmt.executeQuery(QUERY_FIND_ALL);
            while (res.next()) {
                computers.add(ComputerMapper.valueOf(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return computers;
    }

    @Override
    public Page<Computer> pagination(int start, int resultsCount) {
        List<Computer> computers = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_LIMIT_ALL);
            ps.setInt(1, start);
            ps.setInt(2, resultsCount);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                computers.add(ComputerMapper.valueOf(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Page<>(computers, start, resultsCount);
    }

}
