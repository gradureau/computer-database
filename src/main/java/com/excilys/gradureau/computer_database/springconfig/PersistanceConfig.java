package com.excilys.gradureau.computer_database.springconfig;

import java.util.Properties;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.excilys.gradureau.computer_database.persistance.dao")
@Import(DataSourceConfig.class)
public class PersistanceConfig {
    DataSourceConfig dataSourceConfig = new DataSourceConfig();
    
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(
                dataSourceConfig.hikariDataSource()
                );
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(){
       JpaTransactionManager transactionManager
         = new JpaTransactionManager();
       transactionManager.setEntityManagerFactory(
         entityManagerFactory().getObject() );
       return transactionManager;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
       LocalContainerEntityManagerFactoryBean em 
         = new LocalContainerEntityManagerFactoryBean();
       em.setDataSource(dataSourceConfig.hikariDataSource());
       em.setPackagesToScan(new String[] { "com.excilys.gradureau.computer_database.model" });
  
       JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
       em.setJpaVendorAdapter(vendorAdapter);
       em.setJpaProperties(additionalProperties());
  
       return em;
    }
    
    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory().getObject().createEntityManager();
    }
    
    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.setProperty(
                "hibernate.hbm2ddl.auto", "validate");
        return properties;
    }
    
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
