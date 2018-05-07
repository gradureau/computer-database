package com.excilys.gradureau.computer_database.persistance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.dao.mapper.ComputerMapper;
import com.excilys.gradureau.computer_database.util.Page;

public class ComputerDAO extends DAO<Computer> {

    private static final String QUERY_FIND_ALL = "SELECT pc.id as id, pc.name as name, introduced, discontinued, company_id, co.name as company_name "
            + "FROM computer AS pc LEFT JOIN company AS co on pc.company_id = co.id";
    private static final String QUERY_FIND = "SELECT pc.id as id, pc.name as name, introduced, discontinued, company_id, co.name as company_name "
            + "FROM computer AS pc LEFT JOIN company AS co on pc.company_id = co.id WHERE pc.id = ?;";
    private static final String QUERY_CREATE = "INSERT INTO computer (name,introduced,discontinued,company_id) VALUES (?,?,?,?);";
    private static final String QUERY_UPDATE = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?;";
    private static final String QUERY_DELETE = "DELETE FROM computer WHERE id = ?;";
    private static final String QUERY_LIMIT_ALL = QUERY_FIND_ALL + " order by introduced desc, id LIMIT ?, ?;";
    private static final String QUERY_COUNT = "SELECT Count(pc.id) as total FROM computer pc";
    
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

    public ComputerDAO(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @Override
    public Optional<Computer> find(long id) {
        Computer computer = null;
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_FIND)) {
            ps.setLong(1, id);
            try(ResultSet res = ps.executeQuery()) {
                if (res.first()) {
                    computer = ComputerMapper.valueOf(res);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(computer);
    }

    @Override
    public Optional<Computer> create(Computer computer) {
        /*
         * Might as well return a boolean to inform about the outcome and set the Long
         * id of the Computer parameter object.
         */
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS)) {
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

            try(ResultSet autoGenerated = ps.getGeneratedKeys()) {
                if (autoGenerated.next()) { // add the eventual empty company name
                    return find(autoGenerated.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Computer> update(Computer computer) {
        boolean wasUpdated = false;
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_UPDATE)) {
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
            wasUpdated = ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wasUpdated ? Optional.of(computer) : Optional.empty();
    }

    @Override
    public boolean delete(Computer computer) {
        boolean wasDeleted = false;
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_DELETE)) {
            ps.setLong(1, computer.getId());
            wasDeleted = ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wasDeleted;
    }

    @Override
    public List<Computer> findAll() {
        List<Computer> computers = new ArrayList<>();
        try(Connection connection = connectionSupplier.get();
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(QUERY_FIND_ALL)) {
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
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_LIMIT_ALL)) {
            ps.setInt(1, start);
            ps.setInt(2, resultsCount);
            try(ResultSet res = ps.executeQuery()) {
                while (res.next()) {
                    computers.add(ComputerMapper.valueOf(res));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Page<Computer> page = new Page<>(computers, start, resultsCount);
        page.setPageable(this::pagination);
        page.setTotalResultsCounter(this::count);
        return  page;
    }
    
    @Override
    public long count() {
        Long count = null;
        try(Connection connection = connectionSupplier.get();
                ResultSet rs = connection.createStatement().executeQuery(QUERY_COUNT)) {
            if(rs.next()) {
                count = rs.getLong("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public Page<Computer> filterBy(Map<String, String> criterias, int start, int resultsCount, boolean inclusive) {
        List<Computer> filteredComputers = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( " WHERE " );
        
        int criteriasSize = criterias.size();
        int fieldIndex = 1;
        Map<Integer,String> parametersToEscape = new HashMap<>(criteriasSize);
        for( String fieldName : criterias.keySet() ) {
            stringBuilder.append( fieldName );
            stringBuilder.append( " LIKE ?");
            parametersToEscape.put(fieldIndex, criterias.get(fieldName));
            if( fieldIndex++ < criteriasSize ) {
                stringBuilder.append(inclusive ? "OR " : "AND ");
            }
        }
        final String finalQuery = QUERY_FIND_ALL + stringBuilder.toString() + " order by introduced desc, id LIMIT ?, ?";
        try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(finalQuery)) {
            for(int i = 1; i <= criteriasSize; ++i) {
                ps.setString(i, "%" + parametersToEscape.get(i) + "%");
            }
            ps.setInt(criteriasSize + 1, start);
            ps.setInt(criteriasSize + 2, resultsCount);
            try(ResultSet res = ps.executeQuery()) {
                while (res.next()) {
                    filteredComputers.add(ComputerMapper.valueOf(res));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Page<Computer> page = new Page<>(filteredComputers, start, resultsCount);
        page.setPageable( (_start, _resultsCount) -> filterBy(criterias, _start, _resultsCount) );
        page.setTotalResultsCounter( () -> {
            Long count = null;
            try(Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(QUERY_COUNT + " LEFT JOIN company co ON pc.company_id = co.id " + stringBuilder.toString())) {
                for(int i = 1; i <= criteriasSize; ++i) {
                    ps.setString(i, "%" + parametersToEscape.get(i) + "%");
                }
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        count = rs.getLong("total");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return count;
        });
        return  page;
    }

}
