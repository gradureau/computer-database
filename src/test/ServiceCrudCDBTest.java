package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.util.Page;

public class ServiceCrudCDBTest {
	private static ServiceCrudCDB cdb;
	@BeforeAll
	public static void initService() {
		cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance());
	}
	
	@Test
	public void listComputersTest() {
		Page<Computer> page = cdb.listComputers();
		assertNotNull(page);
		assertNotNull(page.getContent());
		assertFalse(page.getContent().isEmpty());
	}
}
