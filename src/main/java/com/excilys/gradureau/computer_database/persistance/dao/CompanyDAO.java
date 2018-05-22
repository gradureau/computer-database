package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.util.Page;

@Repository
public class CompanyDAO extends DAO<Company> {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String QUERY_FIND_ALL = "SELECT id, name FROM company;";
    private static final String QUERY_FIND = "SELECT id, name FROM company WHERE id = ?;";
    private static final String QUERY_LIMIT_ALL = "SELECT id, name FROM company order by name LIMIT ?, ? ;";
    private static final String QUERY_DELETE = "DELETE FROM company WHERE id = ?;";
    private static final String QUERY_DELETE_CHILDREN = "DELETE FROM computer WHERE company_id = ?;";
    private static final String QUERY_COUNT = "SELECT Count(id) as total FROM company";

    @Autowired
    public CompanyDAO(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @Override
    public Optional<Company> find(long id) {
        Company company = null;
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_FIND)) {
            ps.setLong(1, id);
            try(ResultSet res = ps.executeQuery()) {
                if (res.first()) {
                    company = new Company(id, res.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(company);
    }

    @Override
    public Optional<Company> create(Company obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Company> update(Company obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Company company) {
        boolean wasDeleted = false;
        try(Connection connection = connectionSupplier.get();
                PreparedStatement deleteCompanyStatement = connection.prepareStatement(QUERY_DELETE);
                PreparedStatement deleteChildrenStatement = connection.prepareStatement(QUERY_DELETE_CHILDREN)) {
            
            connection.setAutoCommit(false);
            
            deleteChildrenStatement.setLong(1, company.getId());
            deleteChildrenStatement.executeUpdate();
            
            deleteCompanyStatement.setLong(1, company.getId());
            wasDeleted = deleteCompanyStatement.executeUpdate() == 1;
            
            if(wasDeleted)
                connection.commit();
            
        } catch (SQLException e) {
            e.printStackTrace();
            //HikariCP should rollback transaction and reset autoCommit to True automatically
        }
        return wasDeleted;
    }

    @Override
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        try(Connection connection = connectionSupplier.get();
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(QUERY_FIND_ALL)) {
            while (res.next()) {
                companies.add(new Company(res.getLong("id"), res.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }

    @Override
    public Page<Company> pagination(int start, int resultsCount) {
        List<Company> companies = new ArrayList<>();
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_LIMIT_ALL)) {
            ps.setInt(1, start);
            ps.setInt(2, resultsCount);
            try(ResultSet res = ps.executeQuery()) {
                while (res.next()) {
                    companies.add(new Company(res.getLong("id"), res.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Page<>(companies, start, resultsCount);
    }

    @Override
    public Page<Company> filterBy(Map<String, String> criterias, int start, int resultsCount, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject(QUERY_COUNT, Long.class);
    }

}
