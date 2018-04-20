package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

public class ServiceCrudCDBTest {
	private static final String DB_CONFIG_FILEPATH = "ressources/database.properties";
	private static ServiceCrudCDB cdb;
	@BeforeAll
	public static void initService() {
		final Properties databaseConnectionProperties = PropertyFileUtility.readPropertyFile(DB_CONFIG_FILEPATH);
		cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance(
				databaseConnectionProperties.getProperty("DB_URL"),
				databaseConnectionProperties.getProperty("DB_USER"),
				databaseConnectionProperties.getProperty("DB_PASSWORD")
				));
	}
	
	@Test
	public void listComputersTest() {
		Page<Computer> page = cdb.listComputers();
		assertNotNull(page);
		assertNotNull(page.getContent());
		assertFalse(page.getContent().isEmpty());
	}
}
