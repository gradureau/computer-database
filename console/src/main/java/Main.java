import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.springconfig.JacksonConfig;
import com.excilys.gradureau.computer_database.ui.CLI;
import com.excilys.gradureau.computer_database.webservice.springconfig.WebServiceBasedConfig;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        LOGGER.warn("Application in Beta, please report any anomaly.");

        System.out.println("Hello World !");
        
        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
        //springContext.register(ServiceConfig.class);
        springContext.register(WebServiceBasedConfig.class, JacksonConfig.class);
        springContext.refresh();
        
        ICrudCDB cdb = springContext.getBean(
                "ws_cdb"
                //"dao_cdb"
                ,ICrudCDB.class);

        try {
            CLI.interactive(cdb);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn(e.getMessage());
            LOGGER.warn("An error occured.");
        }
        
        springContext.close();

        System.out.println("Thank you for using our service !");
    }

}
