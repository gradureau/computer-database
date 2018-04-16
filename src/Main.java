import java.util.Scanner;

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
		
		ICrudCDB cdb = new ServiceCrudCDB();
		
		MAIN_LOOP: while(true) {
			listerActions();
			switch(scan.nextInt()) {
			default:
			case 0: break MAIN_LOOP;
			case 1:
				cdb.listComputers();
				break;
			case 2:
				cdb.listCompanies();
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
		}
		
		System.out.println("Thanks for using our service !");
		scan.close();
	}

}
