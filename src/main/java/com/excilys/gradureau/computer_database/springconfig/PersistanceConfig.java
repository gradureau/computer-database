package com.excilys.gradureau.computer_database.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan("com.excilys.gradureau.computer_database.persistance.dao")
@Import(DataSourceConfig.class)
public class PersistanceConfig {
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(
                new DataSourceConfig().hikariDataSource()
                );
    }
}
