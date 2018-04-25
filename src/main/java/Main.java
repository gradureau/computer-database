import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.ui.CLI;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String DB_CONFIG_FILEPATH = "database.properties";

    public static void main(String[] args) {

        LOGGER.warn("Application in Beta, no Validation implemented yet !");

        System.out.println("Hello World !");

        final Properties databaseConnectionProperties = PropertyFileUtility.readPropertyFile(DB_CONFIG_FILEPATH);

        ICrudCDB cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance(
                databaseConnectionProperties.getProperty("DB_URL"),
                databaseConnectionProperties.getProperty("DB_USER"),
                databaseConnectionProperties.getProperty("DB_PASSWORD")
                ));

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
