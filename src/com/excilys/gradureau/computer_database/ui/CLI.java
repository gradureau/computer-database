package com.excilys.gradureau.computer_database.ui;

import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.util.Pageable;

public class CLI {

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

}
