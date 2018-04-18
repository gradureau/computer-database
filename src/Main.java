import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.ui.CLI;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		System.out.println("Hello World !");
		Scanner scan = new Scanner(System.in);

		ICrudCDB cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance());

		MAIN_LOOP: while(true) {
			CLI.listerActions();
			switch(scan.nextInt()) {
			default:
			case CLI.ACTION_QUIT: break MAIN_LOOP;
			case CLI.ACTION_LIST_COMPUTERS:
			{
				System.out.println("Enter a page number");
				int pageNumber = scan.nextInt();
				int pageSize = 20;
				CLI.readPage(pageNumber, pageSize, cdb::listComputers);
			}
			break;
			case CLI.ACTION_LIST_COMPANIES:
			{
				System.out.println("Enter a page number");
				int pageNumber = scan.nextInt();
				int pageSize = 20;
				CLI.readPage(pageNumber, pageSize, cdb::listCompanies);
			}
			break;
			case CLI.ACTION_SHOW_COMPUTER_DETAILS:
			{
				Computer computer = new Computer();
				System.out.println("Enter a known computer id.");
				computer.setId(scan.nextLong());
				System.out.println(
						cdb.showComputerDetails(computer)
						);
			}
			break;
			case CLI.ACTION_CREATE_COMPUTER:
				logger.info("not implemented");
				//cdb.createComputer(null);
				break;
			case CLI.ACTION_UPDATE_COMPUTER:
				logger.info("not implemented");
				//cdb.updateComputer(null);
				break;
			case CLI.ACTION_DELETE_COMPUTER:
			{
				Computer computer = new Computer();
				System.out.println("Enter a known computer id.");
				computer.setId(scan.nextLong());
				cdb.deleteComputer(computer);
			}
			break;
			}
			scan.nextLine();
			System.out.println("Press [ENTER] to continue.");
			scan.nextLine();
		}

		System.out.println("Thank you for using our service !");
		scan.close();
	}

}
