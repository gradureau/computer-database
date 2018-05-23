package com.excilys.gradureau.computer_database.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.excilys.gradureau.computer_database.persistance.dao")
@Import(DataSourceConfig.class)
public class PersistanceConfig implements TransactionManagementConfigurer {
    DataSourceConfig dataSourceConfig = new DataSourceConfig();
    
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(
                dataSourceConfig.hikariDataSource()
                );
    }
    
    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(
                dataSourceConfig.hikariDataSource()
                );
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }
}
