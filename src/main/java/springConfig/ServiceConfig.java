package springConfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;

@Configuration
@Import(DataSourceConfig.class)
public class ServiceConfig {
    
    @Bean
    ICrudCDB iCrudCDB() {
        return new ServiceCrudCDB(new DataSourceConfig().connectionSupplier());
    }
    
    @Bean
    @Qualifier("COMPANIES")
    List<Company> companies() {
        ICrudCDB cdb = iCrudCDB();
        return cdb.listCompanies(0, cdb.countCompanies()).getContent();
    }

}
