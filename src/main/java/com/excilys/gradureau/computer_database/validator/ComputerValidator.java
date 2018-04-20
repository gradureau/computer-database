package com.excilys.gradureau.computer_database.validator;

import java.time.LocalDateTime;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.excilys.gradureau.computer_database.model.Computer;

public class ComputerValidator {

	// private static final Logger logger =
	// LoggerFactory.getLogger(ComputerValidator.class);

	public static void check(Computer computer) throws WrongObjectStateException {
		hasName(computer);
		LocalDateTime introduced = computer.getIntroduced();
		LocalDateTime discontinued = computer.getDiscontinued();
		if (introduced != null && discontinued != null) {
			if (introduced.compareTo(discontinued) > 0)
				throw new WrongObjectStateException("A computer can only be discontinued after its introduced date.");
		}
	}

	public static void checkId(Computer computer, boolean shouldHave) throws WrongObjectStateException {
		Long id = computer.getId();
		if (id == null == shouldHave) {
			String errorMessage = "id specified in Computer object";
			if (!shouldHave) {
				errorMessage = "No " + errorMessage;
			}
			throw new WrongObjectStateException(errorMessage);
		}
	}

	public static void checkId(Computer computer) throws WrongObjectStateException {
		checkId(computer, true);
	}
	
	public static void hasName(Computer computer) throws WrongObjectStateException {
		if(computer.getName() != null || computer.getName().length() == 0)
			throw new WrongObjectStateException("missing name in Computer object");
	}

}
