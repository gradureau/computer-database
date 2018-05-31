package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.model.Computer_;
import com.excilys.gradureau.computer_database.persistance.dao.mapper.TimeMapper;
import com.excilys.gradureau.computer_database.util.Page;

@Repository
@Transactional(readOnly=true)
public class ComputerDAO extends DAO<Computer> {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    EntityManager entityManager;

    private static final String QUERY_FIND_ALL = "SELECT pc.id as id, pc.name as name, introduced, discontinued, company_id, co.name as company_name "
            + "FROM computer AS pc LEFT JOIN company AS co on pc.company_id = co.id";
    private static final String QUERY_UPDATE = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?;";
    private static final String QUERY_COUNT = "SELECT Count(pc.id) as total FROM computer pc";
    
    private static RowMapper<Computer> computerRowMapper = (ResultSet rs, int rowNum) -> {
        Company company = null;
        if (rs.getLong("company_id") != 0) {
            company = new Company(rs.getLong("company_id"), rs.getString("company_name"));
        }
        return new Computer(rs.getLong("id"), rs.getString("name"),
                TimeMapper.timestamp2LocalDateTime(rs.getTimestamp("introduced")),
                TimeMapper.timestamp2LocalDateTime(rs.getTimestamp("discontinued")), company);
    };
    public static enum Fields {
        COMPUTER_NAME("pc.name"),
        COMPANY_NAME("co.name");
        
        private String sqlAlias;
        Fields(String sqlAlias) {
            this.sqlAlias = sqlAlias;
        }
        public String getSqlAlias() {
            return sqlAlias;
        }
    }

    @Override
    public Optional<Computer> find(long id) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Computer> cquery = cb.createQuery(Computer.class);
            Root<Computer> computerNode = cquery.from(Computer.class);
            ParameterExpression<Long> idParameter = cb.parameter(Long.class, "id");
            cquery.select(computerNode).where(cb.equal(computerNode.get(Computer_.ID), idParameter));
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
    public Optional<Computer> create(Computer computer) {
        /*
         * Might as well return a boolean to inform about the outcome and set the Long
         * id of the Computer parameter object.
         */
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("computer").usingGeneratedKeyColumns("id");
        Map<String,Object> parameterSource = new HashMap<>();
        parameterSource.put("name", computer.getName());
        parameterSource.put("introduced", computer.getIntroduced() == null ? null : Timestamp.valueOf(computer.getIntroduced()));
        parameterSource.put("discontinued", computer.getDiscontinued() == null ? null : Timestamp.valueOf(computer.getDiscontinued()));
        parameterSource.put("company_id", computer.getCompany() == null ? null : computer.getCompany().getId());
        try {
            Long generatedId = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();
            // add the eventual empty company name
            return find(generatedId);
        } catch(DataIntegrityViolationException e) {  }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly=false)
    public Optional<Computer> update(Computer computer) {
        boolean wasUpdated = jdbcTemplate.update(QUERY_UPDATE,
                computer.getName(),
                (computer.getIntroduced() == null ? null : Timestamp.valueOf(computer.getIntroduced())),                   
                (computer.getDiscontinued() == null ? null : Timestamp.valueOf(computer.getDiscontinued())),
                (computer.getCompany() == null ? null : computer.getCompany().getId()),
                computer.getId()
                ) == 1;
        return wasUpdated ? Optional.of(computer) : Optional.empty();
    }

    @Override
    @Transactional(readOnly=false)
    public boolean delete(Computer computer) {
        entityManager.joinTransaction();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Computer> cdelete =  cb.createCriteriaDelete(Computer.class);
        ParameterExpression<Long> computerIdParameter = cb.parameter(Long.class, "computerId");
        Root<Computer> computerNode = cdelete.from(Computer.class);
        cdelete.where(cb.equal(computerNode.get(Computer_.ID), computerIdParameter));
        return entityManager.createQuery(cdelete).setParameter("computerId", computer.getId())
                .executeUpdate() == 1;
    }

    @Override
    public List<Computer> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Computer> cquery = cb.createQuery(Computer.class);
        return entityManager.createQuery(
                cquery.select(cquery.from(Computer.class))
                ).getResultList();
    }

    @Override
    public Page<Computer> pagination(int start, int resultsCount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Computer> cquery = cb.createQuery(Computer.class);
        Root<Computer> computerNode = cquery.from(Computer.class);
        cquery.select(computerNode);
        cquery.orderBy(
                cb.desc(computerNode.get(Computer_.INTRODUCED)),
                cb.asc(computerNode.get(Computer_.ID)));
        List<Computer> computers = entityManager
                .createQuery(cquery)
                .setFirstResult(start)
                .setMaxResults(resultsCount)
                .getResultList();
        Page<Computer> page = new Page<>(computers, start, resultsCount);
        page.setPageable(this::pagination);
        page.setTotalResultsCounter(this::count);
        return  page;
    }
    
    @Override
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cquery = cb.createQuery(Long.class);
        cquery.select(cb.count(cquery.from(Computer.class)));
        return entityManager.createQuery(cquery).getSingleResult();
    }

    @Override
    public Page<Computer> filterBy(Map<String, String> criterias, int start, int resultsCount, boolean inclusive) {
        int criteriasSize = criterias.size();
        List<String> parametersToEscape = new ArrayList<>(criteriasSize);
        
        final String sqlCriterias = buildSqlCriterias(criterias, inclusive, parametersToEscape, criteriasSize);
        final String finalQuery = QUERY_FIND_ALL + sqlCriterias + " order by introduced desc, id LIMIT ?, ?";       
        
        List<Computer> filteredComputers = readFilteredResults(start, resultsCount, parametersToEscape, finalQuery);
        
        Page<Computer> page = new Page<>(filteredComputers, start, resultsCount);
        page.setPageable( (_start, _resultsCount) -> filterBy(criterias, _start, _resultsCount) );
        page.setTotalResultsCounter( () -> {
            return countFilteredResults(parametersToEscape, sqlCriterias);
        });
        return  page;
    }
    

    private String buildSqlCriterias(Map<String, String> criterias, boolean inclusive, List<String> parametersToEscape, int criteriasSize) {
        StringBuilder stringBuilder = new StringBuilder(" WHERE ");
        int fieldIndex = 1;
        for( String fieldName : criterias.keySet() ) {
            stringBuilder.append( fieldName );
            stringBuilder.append( " LIKE ?");
            parametersToEscape.add(criterias.get(fieldName));
            if( fieldIndex++ < criteriasSize ) {
                stringBuilder.append(inclusive ? "OR " : "AND ");
            }
        }
        return stringBuilder.toString();
    }

    private List<Computer> readFilteredResults(int start, int resultsCount,
            List<String> parametersToEscape, final String finalQuery) {
        return jdbcTemplate.query(finalQuery, 
                Stream.concat(
                        parametersToEscape.stream().map(keywords -> "%"+keywords+"%"),
                        Stream.of(start, resultsCount)
                ).toArray(),
                computerRowMapper);
    }
    
    private Long countFilteredResults(List<String> parametersToEscape, final String sqlCriterias) {
        return jdbcTemplate.queryForObject(
                QUERY_COUNT + " LEFT JOIN company co ON pc.company_id = co.id " + sqlCriterias,
                parametersToEscape.stream().map(keywords -> "%"+keywords+"%").toArray(),
                Long.class
                );
    }

}
