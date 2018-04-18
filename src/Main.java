import java.time.LocalDateTime;
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

		logger.warn("Application in Beta, no Validation implemented yet !");

		System.out.println("Hello World !");
		Scanner scan = new Scanner(System.in);

		ICrudCDB cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance());

		MAIN_LOOP: while(true) {
			CLI.listerActions();
			switch(scan.nextInt()) {
			default:
			case CLI.ACTION_QUIT: break MAIN_LOOP;
			case CLI.ACTION_LIST_COMPUTERS:
				logger.info("list computers");
				{
					System.out.println("Enter a page number");
					int pageNumber = scan.nextInt();
					int pageSize = 20;
					CLI.readPage(pageNumber, pageSize, cdb::listComputers);
					scan.nextLine(); // move the Scanner cursor to the right position
				}
				break;
			case CLI.ACTION_LIST_COMPANIES:
				logger.info("list companies");
				{
					System.out.println("Enter a page number");
					int pageNumber = scan.nextInt();
					int pageSize = 20;
					CLI.readPage(pageNumber, pageSize, cdb::listCompanies);
					scan.nextLine(); // move the Scanner cursor to the right position
				}
				break;
			case CLI.ACTION_SHOW_COMPUTER_DETAILS:
				logger.info("show computer details");
				{
					Computer computer = new Computer();
					System.out.println("Enter a known computer id.");
					computer.setId(scan.nextLong());
					System.out.println(
							cdb.showComputerDetails(computer)
							);
					scan.nextLine(); // move the Scanner cursor to the right position
				}
				break;
			case CLI.ACTION_CREATE_COMPUTER:
				{
					System.out.println("Enter a name");
					String name = scan.next();
					logger.debug("name = " + name);
					scan.nextLine(); // move the Scanner cursor to the right position
					LocalDateTime introducedDateTime = CLI.readDate(scan, "introduced");
					LocalDateTime discontinuedDateTime = CLI.readDate(scan, "discontinued");
					System.out.println("Enter an eventual company id");
					String longInput = scan.nextLine();
					Long companyId = null;
					try {
						companyId = Long.valueOf(longInput);
					}catch(NumberFormatException e) {
						logger.debug("We could not get the company id.");
					}
					logger.debug("company id = " + companyId);
					Computer newComputer = cdb.createComputer( new Computer(
							null,
							name,
							introducedDateTime,
							discontinuedDateTime,
							null
							),
							companyId);
					logger.info("new Computer created :\n\t" + newComputer);
				}
				break;
			case CLI.ACTION_UPDATE_COMPUTER:
				logger.info("not implemented");
				//cdb.updateComputer(null);
				break;
			case CLI.ACTION_DELETE_COMPUTER:
				logger.info("delete computer");
				{
					Computer computer = new Computer();
					System.out.println("Enter a known computer id.");
					computer.setId(scan.nextLong());
					cdb.deleteComputer(computer);
					scan.nextLine(); // move the Scanner cursor to the right position
				}
				break;
			}
			System.out.println("Press [ENTER] to continue.");
			scan.nextLine();
		}

		System.out.println("Thank you for using our service !");
		scan.close();
	}

}
