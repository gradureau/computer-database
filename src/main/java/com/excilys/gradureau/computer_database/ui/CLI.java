package com.excilys.gradureau.computer_database.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.util.Pageable;

public class CLI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CLI.class);

    private static final int ACTION_QUIT = 0;
    private static final int ACTION_LIST_COMPUTERS = 1;
    private static final int ACTION_LIST_COMPANIES = 2;
    private static final int ACTION_SHOW_COMPUTER_DETAILS = 3;
    private static final int ACTION_CREATE_COMPUTER = 4;
    private static final int ACTION_UPDATE_COMPUTER = 5;
    private static final int ACTION_DELETE_COMPUTER = 6;

    private static void listerActions() {
        System.out.println(ACTION_QUIT + "   Quit\n" + ACTION_LIST_COMPUTERS + "   List computers\n"
                + ACTION_LIST_COMPANIES + "   List companies\n" + ACTION_SHOW_COMPUTER_DETAILS
                + "   Show computer details (the detailed information of only one computer)\n" + ACTION_CREATE_COMPUTER
                + "   Create a computer\n" + ACTION_UPDATE_COMPUTER + "   Update a computer\n" + ACTION_DELETE_COMPUTER
                + "   Delete a computer\n");
    }

    private static void readPage(int pageNumber, int pageSize, Pageable pageable) {
        int start = (pageNumber - 1) * pageSize;
        Page<?> page = pageable.pagination(start, pageSize);
        page.forEach(System.out::println);
        System.out.print("offset: " + page.getStart());
        System.out.print(" _ " + page.getContent().size());
        System.out.println(" of " + page.getResultsCount() + " requested");
    }

    private static LocalDateTime readDate(Scanner scan, String dateName) {
        System.out.println("Enter the date of " + dateName + " (format is Year-Month-Day eg: 2018-04-18).");
        String dateInput = scan.nextLine();
        LocalDate date = null;
        try {
            date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            LOGGER.debug("We could not get the date.");
        }
        LOGGER.debug(dateName + " = " + (date != null ? date.toString() : "null"));
        return date == null ? null : date.atStartOfDay();
    }

    private static Computer readUpdateComputerInfo(Scanner scan, Computer computer) {

        if (computer != null) {
            System.out.println("Known data : " + computer);
        } else {
            computer = new Computer();
        }

        System.out.println("Enter a name");
        String name = "";
        while (name.length() == 0) {
            name = scan.nextLine();
        }
        computer.setName(name);
        LOGGER.debug("name = " + name);

        LocalDateTime introducedDateTime = readDate(scan, "introduced");
        computer.setIntroduced(introducedDateTime);

        LocalDateTime discontinuedDateTime = readDate(scan, "discontinued");
        computer.setDiscontinued(discontinuedDateTime);

        return computer;
    }

    private static Long readId(Scanner scan, String type) {
        System.out.println("Enter a known " + type + " id");
        String longInput = scan.nextLine();
        Long id = null;
        try {
            id = Long.valueOf(longInput);
        } catch (NumberFormatException e) {
            LOGGER.debug("We could not get the " + type + " id.");
        }
        LOGGER.debug(type + " id = " + id);
        return id;
    }

    public static void interactive(ICrudCDB cdb) throws WrongObjectStateException {
        Scanner scan = new Scanner(System.in);

        try {
            MAIN_LOOP: while (true) {
                listerActions();
                switch (Integer.parseInt(scan.nextLine())) {
                default:
                    continue;
                case ACTION_QUIT:
                    break MAIN_LOOP;
                case ACTION_LIST_COMPUTERS:
                    LOGGER.info("list computers");
                    listComputers(cdb, scan);
                    break;
                case ACTION_LIST_COMPANIES:
                    LOGGER.info("list companies");
                    listCompanies(cdb, scan);
                    break;
                case ACTION_SHOW_COMPUTER_DETAILS:
                    LOGGER.info("show computer details");
                    showComputerDetails(cdb, scan);
                    break;
                case ACTION_CREATE_COMPUTER:
                    LOGGER.info("create computer"); createComputer(cdb, scan);
                    break;
                case ACTION_UPDATE_COMPUTER:
                    LOGGER.info("update computer"); updateComputer(cdb, scan);
                    break;
                case ACTION_DELETE_COMPUTER:
                    LOGGER.info("delete computer"); deleteComputer(cdb, scan);
                    break;
                }
                System.out.println("Press [ENTER] to continue.");
                scan.nextLine();
            }
        } catch (Exception e) {
            scan.close();
            throw e;
        } finally {
            scan.close();
        }
    }

    private static void deleteComputer(ICrudCDB cdb, Scanner scan) throws WrongObjectStateException {
        Computer computer = new Computer();
        System.out.println("Enter a known computer id.");
        computer.setId(scan.nextLong());
        cdb.deleteComputer(computer);
        scan.nextLine(); // move the Scanner cursor to the right position
    }

    private static void updateComputer(ICrudCDB cdb, Scanner scan) throws WrongObjectStateException {
        Long computerId = readId(scan, "computer");
        if (computerId != null) {
            Computer computer = new Computer();
            computer.setId(computerId);
            computer = cdb.showComputerDetails(computer);
            if (computer != null) {
                computer = readUpdateComputerInfo(scan, computer);
                Long companyId = readId(scan, "company");
                LOGGER.debug("data = " + computer);
                computer = cdb.updateComputer(computer, companyId);
                LOGGER.info("Computer updated :\n\t" + computer);
            } else {
                LOGGER.warn("could not find computer");
            }
        } else {
            LOGGER.warn("could not read computer id");
        }
    }

    private static void createComputer(ICrudCDB cdb, Scanner scan) throws WrongObjectStateException {
        Computer newComputer = readUpdateComputerInfo(scan, new Computer());
        Long companyId = readId(scan, "company");
        newComputer = cdb.createComputer(newComputer, companyId);
        LOGGER.info("new Computer created :\n\t" + newComputer);
    }

    private static void showComputerDetails(ICrudCDB cdb, Scanner scan) throws WrongObjectStateException {
        Long computerId = readId(scan, "computer");
        if (computerId != null) {
            Computer computer = new Computer();
            computer.setId(computerId);
            System.out.println(cdb.showComputerDetails(computer));
        }
    }

    private static void listCompanies(ICrudCDB cdb, Scanner scan) {
        System.out.println("Enter a page number");
        int pageNumber = scan.nextInt();
        int pageSize = 20;
        readPage(pageNumber, pageSize, cdb::listCompanies);
        scan.nextLine(); // move the Scanner cursor to the right position
    }

    private static void listComputers(ICrudCDB cdb, Scanner scan) {
        System.out.println("Enter a page number");
        int pageNumber = scan.nextInt();
        int pageSize = 20;
        readPage(pageNumber, pageSize, cdb::listComputers);
        scan.nextLine(); // move the Scanner cursor to the right position
    }

}
