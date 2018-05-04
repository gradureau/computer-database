import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.persistance.HikariBasedDataSource;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.ui.CLI;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String HIKARI_CONFIG_FILEPATH = "hikari.properties";

    public static void main(String[] args) {

        LOGGER.warn("Application in Beta, no Validation implemented yet !");

        System.out.println("Hello World !");

        ICrudCDB cdb = new ServiceCrudCDB(
                HikariBasedDataSource.init(HIKARI_CONFIG_FILEPATH)
                );

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

        System.out.println("Thank you for using our service !");
    }

}
