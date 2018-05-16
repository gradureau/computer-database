package com.excilys.gradureau.computer_database.persistance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariBasedDataSource {
    
    @Autowired(required = true)
    private static Optional<HikariDataSource> optionalDataSource = Optional.empty();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HikariBasedDataSource.class);
    
    private static final String ERROR_MESSAGE_NO_DATASOURCE_SET = "No DataSource Set";
    private static final String WARNING_MESSAGE_COULD_NOT_GET_SQL_CONNECTION = "Could not get sql Connection";

    public static Supplier<Connection> init(final String HikariCPDatasourceProperties) {
        optionalDataSource.ifPresent(dataSource -> dataSource.close());
        HikariConfig config = new HikariConfig(
                ClassLoader.getSystemClassLoader().getResource(HikariCPDatasourceProperties).getFile()
                );
        optionalDataSource = Optional.of(new HikariDataSource(config));
        return () -> getConnection();
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            if(!optionalDataSource.isPresent()) {
                LOGGER.error(ERROR_MESSAGE_NO_DATASOURCE_SET);
                throw new IllegalStateException(ERROR_MESSAGE_NO_DATASOURCE_SET);
            }
            connection = optionalDataSource.get().getConnection();
        } catch (SQLException e) {
            LOGGER.warn(WARNING_MESSAGE_COULD_NOT_GET_SQL_CONNECTION, e);
        }
        return connection;
    }
}
