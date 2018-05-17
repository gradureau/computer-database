import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.ui.CLI;

import springConfig.ServiceConfig;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        LOGGER.warn("Application in Beta, please report any anomaly.");

        System.out.println("Hello World !");
        
        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
        springContext.register(ServiceConfig.class);
        springContext.refresh();
        
        ICrudCDB cdb = springContext.getBean(ICrudCDB.class);

        boolean displayMenu = true;

        while (displayMenu) {
            try {
                displayMenu = false;
                CLI.interactive(cdb);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.warn(e.getMessage());
                LOGGER.warn("An error occured, you are redirected to main menu.");
                displayMenu = true;
            }
        }
        
        springContext.close();

        System.out.println("Thank you for using our service !");
    }

}
