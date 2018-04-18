import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.ui.CLI;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {

		logger.warn("Application in Beta, no Validation implemented yet !");

		System.out.println("Hello World !");
		
		ICrudCDB cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance());
		
		CLI.interactive(cdb);
		
		System.out.println("Thank you for using our service !");
	}

}
