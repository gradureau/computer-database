package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Company_;
import com.excilys.gradureau.computer_database.util.Page;

@Repository
@Transactional(readOnly=true)
public class CompanyDAO extends DAO<Company> {
    /*
     * https://docs.oracle.com/javaee/6/tutorial/doc/gjivm.html
     */
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    EntityManager entityManager;

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
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Company> cquery = cb.createQuery(Company.class);
            Root<Company> companyNode = cquery.from(Company.class);
            ParameterExpression<Long> idParameter = cb.parameter(Long.class, "id");
            cquery.select(companyNode).where(cb.equal(companyNode.get(Company_.ID), idParameter));
            return Optional.of(
                    entityManager
                    .createQuery(cquery)
                    .setParameter("id", id)
                    .getSingleResult()
                    );
        } catch(NoResultException e) {
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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> criteriaQuery = criteriaBuilder.createQuery(Company.class);
        Root<Company> from = criteriaQuery.from(Company.class);
        criteriaQuery.select(from);
        TypedQuery<Company> query = entityManager.createQuery(criteriaQuery);
        List<Company> allitems = query.getResultList();
        return allitems;
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
