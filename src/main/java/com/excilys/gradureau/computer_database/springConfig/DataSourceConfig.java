package com.excilys.gradureau.computer_database.springConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
    public static final String HikariCPDatasourceProperties = "hikari.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariConfig config = new HikariConfig(
                ClassLoader.getSystemClassLoader().getResource(HikariCPDatasourceProperties).getFile());
        return new HikariDataSource(config);
    }
    
    @Bean(name = "connectionSupplier")
    public Supplier<Connection> connectionSupplier() {
        HikariDataSource ds = hikariDataSource();
        return () -> {
            try {
                return ds.getConnection();
            } catch (SQLException e) {
                LOGGER.trace("Cannot get a connection from Hikari Connection Pool", e);
            }
            return null;
        };
    }

}
