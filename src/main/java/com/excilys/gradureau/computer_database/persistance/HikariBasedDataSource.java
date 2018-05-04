package com.excilys.gradureau.computer_database.persistance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariBasedDataSource {

    private static Optional<HikariDataSource> optionalDataSource = Optional.empty();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HikariBasedDataSource.class);
    
    private static final String ERROR_MESSAGE_NO_DATASOURCE_SET = "No DataSource Set";
    private static final String WARNING_MESSAGE_COULD_NOT_GET_SQL_CONNECTION = "Could not get sql Connection";

    public static Supplier<Optional<Connection>> init(final String HikariCPDatasourceProperties) {
        optionalDataSource.ifPresent(dataSource -> dataSource.close());
        HikariConfig config = new HikariConfig(
                ClassLoader.getSystemClassLoader().getResource(HikariCPDatasourceProperties).getFile()
                );
        optionalDataSource = Optional.of(new HikariDataSource(config));
        return () -> getConnection();
    }

    public static Optional<Connection> getConnection() throws IllegalStateException {
        Optional<Connection> optionalConnection = Optional.empty();
        try {
            optionalConnection = Optional.of(optionalDataSource.orElseThrow(() -> {
                LOGGER.error(ERROR_MESSAGE_NO_DATASOURCE_SET);
                throw new IllegalStateException(ERROR_MESSAGE_NO_DATASOURCE_SET);
            }).getConnection());
        } catch (SQLException e) {
            LOGGER.warn(WARNING_MESSAGE_COULD_NOT_GET_SQL_CONNECTION, e);
        }
        return optionalConnection;
    }
}
