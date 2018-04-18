package com.excilys.gradureau.computer_database.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.util.Pageable;

public class CLI {
	
	private static final Logger logger = LoggerFactory.getLogger(CLI.class);

	public static final int ACTION_QUIT = 0;
	public static final int ACTION_LIST_COMPUTERS = 1;
	public static final int ACTION_LIST_COMPANIES = 2;
	public static final int ACTION_SHOW_COMPUTER_DETAILS = 3;
	public static final int ACTION_CREATE_COMPUTER = 4;
	public static final int ACTION_UPDATE_COMPUTER = 5;
	public static final int ACTION_DELETE_COMPUTER = 6;
	
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
	
	public static void readPage(int pageNumber, int pageSize, Pageable pageable) {
		int start = (pageNumber - 1) * pageSize + 1;
		Page<?> page = pageable.pagination(start, pageSize);
		page.forEach(System.out::println);
		System.out.print("offset: " + page.getStart());
		System.out.print(" _ " + page.getContent().size());
		System.out.println(" of " + page.getResultsCount() + " requested");
	}

	public static LocalDateTime readDate(Scanner scan, String dateName) {
		System.out.println("Enter the date of " + dateName +" (format is Year-Month-Day eg: 2018-04-18).");
		String dateInput = scan.nextLine();
		LocalDate date = null;
		try {
			date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
		}catch(DateTimeParseException e) {
			logger.debug("We could not get the date.");
		}
		logger.debug(dateName + " = " +
				( date != null ? date.toString() : "null")
				);	
		return date == null ? null : date.atStartOfDay();
	}

	public static void interactive(ICrudCDB cdb) {
		Scanner scan = new Scanner(System.in);
	
		MAIN_LOOP: while(true) {
			listerActions();
			switch(scan.nextInt()) {
			default:
			case ACTION_QUIT: break MAIN_LOOP;
			case ACTION_LIST_COMPUTERS:
				logger.info("list computers");
				{
					System.out.println("Enter a page number");
					int pageNumber = scan.nextInt();
					int pageSize = 20;
					readPage(pageNumber, pageSize, cdb::listComputers);
					scan.nextLine(); // move the Scanner cursor to the right position
				}
				break;
			case ACTION_LIST_COMPANIES:
				logger.info("list companies");
				{
					System.out.println("Enter a page number");
					int pageNumber = scan.nextInt();
					int pageSize = 20;
					readPage(pageNumber, pageSize, cdb::listCompanies);
					scan.nextLine(); // move the Scanner cursor to the right position
				}
				break;
			case ACTION_SHOW_COMPUTER_DETAILS:
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
			case ACTION_CREATE_COMPUTER:
				{
					System.out.println("Enter a name");
					String name = scan.next();
					logger.debug("name = " + name);
					scan.nextLine(); // move the Scanner cursor to the right position
					LocalDateTime introducedDateTime = readDate(scan, "introduced");
					LocalDateTime discontinuedDateTime = readDate(scan, "discontinued");
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
			case ACTION_UPDATE_COMPUTER:
				logger.info("not implemented");
				//cdb.updateComputer(null);
				break;
			case ACTION_DELETE_COMPUTER:
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
		
		scan.close();
	}

}
