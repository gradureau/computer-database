package com.excilys.gradureau.computer_database.springconfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.service.ICrudCDB;

@Configuration
@Import(PersistanceConfig.class)
@ComponentScan("com.excilys.gradureau.computer_database.service")
public class ServiceConfig {
    
    @Bean("COMPANIES")
    List<Company> companies(@Autowired@Qualifier("dao_cdb") ICrudCDB cdb) {
        return cdb.listCompanies(0, cdb.countCompanies()).getContent();
    }

}
