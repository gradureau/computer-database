package com.excilys.gradureau.computer_database.springconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.excilys.gradureau.computer_database.webservice")
public class WebServiceConfig {
    
}
