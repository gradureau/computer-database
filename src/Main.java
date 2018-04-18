import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;
import com.excilys.gradureau.computer_database.util.Page;

public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static final int
	ACTION_QUIT = 0,
	ACTION_LIST_COMPUTERS = 1,
	ACTION_LIST_COMPANIES = 2,
	ACTION_SHOW_COMPUTER_DETAILS = 3,
	ACTION_CREATE_COMPUTER = 4,
	ACTION_UPDATE_COMPUTER = 5,
	ACTION_DELETE_COMPUTER = 6;
	
	public static void listerActions() {
		System.out.println(
				ACTION_QUIT +
				"   Quit\n" +
				ACTION_LIST_COMPUTERS +
				"   List computers\n" + 
				ACTION_LIST_COMPANIES +
				"   List companies\n" + 
				ACTION_SHOW_COMPUTER_DETAILS +
				"   Show computer details (the detailed information of only one computer)\n" + 
				ACTION_CREATE_COMPUTER +
				"   Create a computer\n" + 
				ACTION_UPDATE_COMPUTER +
				"   Update a computer\n" + 
				ACTION_DELETE_COMPUTER +
				"   Delete a computer\n"
		);
	}

	public static void main(String[] args) {
		System.out.println("Hello World !");
		Scanner scan = new Scanner(System.in);
		
		ICrudCDB cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance());
		
		MAIN_LOOP: while(true) {
			listerActions();
			Computer computer;
			switch(scan.nextInt()) {
			default:
			case ACTION_QUIT: break MAIN_LOOP;
			case ACTION_LIST_COMPUTERS:
				cdb.listComputers()
				.forEach(System.out::println);
				break;
			case ACTION_LIST_COMPANIES:
				System.out.println("Enter a page number");
				int pageNumber = scan.nextInt();
				int pageSize = 20;
				int start = (pageNumber - 1) * pageSize + 1;
				Page<Company> companiesPage = cdb.listCompanies(start, pageSize);
				companiesPage.forEach(System.out::println);
				System.out.print("offset: " + companiesPage.getStart());
				System.out.print(" _ " + companiesPage.getContent().size());
				System.out.println(" of " + companiesPage.getResultsCount() + " requested");
				break;
			case ACTION_SHOW_COMPUTER_DETAILS:
				computer = new Computer();
				System.out.println("Enter a known computer id.");
				computer.setId(scan.nextLong());
				System.out.println(
						cdb.showComputerDetails(computer)
				);
				break;
			case ACTION_CREATE_COMPUTER:
				logger.info("not implemented");
				//cdb.createComputer(null);
				break;
			case ACTION_UPDATE_COMPUTER:
				logger.info("not implemented");
				//cdb.updateComputer(null);
				break;
			case ACTION_DELETE_COMPUTER:
				computer = new Computer();
				System.out.println("Enter a known computer id.");
				computer.setId(scan.nextLong());
				cdb.deleteComputer(computer);
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
