package com.excilys.gradureau.computer_database.springConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.excilys.gradureau.computer_database.persistance.dao")
@Import(DataSourceConfig.class)
public class PersistanceConfig {
}
