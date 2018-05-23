package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.util.Page;

@Repository
@Transactional(readOnly=true)
public class CompanyDAO extends DAO<Company> {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String QUERY_FIND_ALL = "SELECT id, name FROM company;";
    private static final String QUERY_FIND = "SELECT id, name FROM company WHERE id = ?;";
    private static final String QUERY_LIMIT_ALL = "SELECT id, name FROM company order by name LIMIT ?, ? ;";
    private static final String QUERY_DELETE = "DELETE FROM company WHERE id = ?;";
    private static final String QUERY_DELETE_CHILDREN = "DELETE FROM computer WHERE company_id = ?;";
    private static final String QUERY_COUNT = "SELECT Count(id) as total FROM company";
    
    private static RowMapper<Company> companyRowMapper = (ResultSet rs, int rowNum) -> {
        Company company = new Company();
        company.setId(rs.getLong("id"));
        company.setName(rs.getString("name"));
        return company;
    };

    @Override
    public Optional<Company> find(long id) {
        try {
            Company company = jdbcTemplate.queryForObject(
                    QUERY_FIND,
                    new Object[]{ id },
                    companyRowMapper
                    );
            return Optional.of(company);
        } catch(EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly=false)
    public Optional<Company> create(Company obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly=false)
    public Optional<Company> update(Company obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly=false)
    public boolean delete(Company company) {
        jdbcTemplate.update(QUERY_DELETE_CHILDREN, new Object[] { company.getId() });
        return jdbcTemplate.update(QUERY_DELETE, new Object[] { company.getId() }) == 1;
    }

    @Override
    public List<Company> findAll() {
        return jdbcTemplate.query(QUERY_FIND_ALL, companyRowMapper);
    }

    @Override
    public Page<Company> pagination(int start, int resultsCount) {
        List<Company> companies = jdbcTemplate.query(
                QUERY_LIMIT_ALL,
                new Object[] {start, resultsCount},
                companyRowMapper
                );
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
