package com.excilys.gradureau.computer_database.springconfig;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
    public static final String HikariCPDatasourceProperties = "hikari.properties";

    @Bean
    public DataSource hikariDataSource() {
        HikariConfig config = new HikariConfig(
                ClassLoader.getSystemClassLoader().getResource(HikariCPDatasourceProperties).getFile());
        return new HikariDataSource(config);
    }

}
