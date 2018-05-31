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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
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
    private static final String QUERY_FIND = "SELECT pc.id as id, pc.name as name, introduced, discontinued, company_id, co.name as company_name "
            + "FROM computer AS pc LEFT JOIN company AS co on pc.company_id = co.id WHERE pc.id = ?;";
    private static final String QUERY_UPDATE = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?;";
    private static final String QUERY_DELETE = "DELETE FROM computer WHERE id = ?;";
    private static final String QUERY_LIMIT_ALL = QUERY_FIND_ALL + " order by introduced desc, id LIMIT ?, ?;";
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
            Computer computer = jdbcTemplate.queryForObject(
                    QUERY_FIND,
                    new Object[]{ id },
                    computerRowMapper
                    );
            return Optional.of(computer);
        } catch(EmptyResultDataAccessException e) {
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
        return jdbcTemplate.update(QUERY_DELETE, computer.getId())  == 1;
    }

    @Override
    public List<Computer> findAll() {
        return jdbcTemplate.query(QUERY_FIND_ALL, computerRowMapper);
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        
//        CriteriaQuery<Computer> criteriaQuery = criteriaBuilder.createQuery(Computer.class);
//        Root<Computer> from = criteriaQuery.from(Computer.class);
//        Join<Computer,Company> joinNode = criteriaQuery.from(Computer.class).join(Computer_.company);
//        
//        criteriaQuery.select(from);
//        TypedQuery<Computer> query = entityManager.createQuery(criteriaQuery);
//        return query.getResultList();
    }

    @Override
    public Page<Computer> pagination(int start, int resultsCount) {
        List<Computer> computers = jdbcTemplate.query(
                QUERY_LIMIT_ALL,
                new Object[] {start, resultsCount},
                computerRowMapper);
        Page<Computer> page = new Page<>(computers, start, resultsCount);
        page.setPageable(this::pagination);
        page.setTotalResultsCounter(this::count);
        return  page;
    }
    
    @Override
    public long count() {
        return jdbcTemplate.queryForObject(QUERY_COUNT, Long.class);
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
