import java.util.Scanner;

import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.service.ServiceCrudCDB;

public class Main {
	
	public static void listerActions() {
		System.out.println(
				"0   Quit\n" +
				"1   List computers\n" + 
				"2   List companies\n" + 
				"3   Show computer details (the detailed information of only one computer)\n" + 
				"4   Create a computer\n" + 
				"5   Update a computer\n" + 
				"6   Delete a computer\n"
		);
	}

	public static void main(String[] args) {
		System.out.println("Hello World !");
		Scanner scan = new Scanner(System.in);
		
		ICrudCDB cdb = new ServiceCrudCDB(ConnectionMysqlSingleton.getInstance());
		
		MAIN_LOOP: while(true) {
			listerActions();
			switch(scan.nextInt()) {
			default:
			case 0: break MAIN_LOOP;
			case 1:
				for(Computer computer : cdb.listComputers())
					System.out.println("Computer id = " + computer.getId() + " name = " + computer.getName());
				break;
			case 2:
				cdb.listCompanies()
				.forEach(System.out::println);
				break;
			case 3:
				cdb.showComputerDetails(null);
				break;
			case 4:
				cdb.createComputer(null);
				break;
			case 5:
				cdb.updateComputer(null);
				break;
			case 6:
				cdb.deleteComputer(null);
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
