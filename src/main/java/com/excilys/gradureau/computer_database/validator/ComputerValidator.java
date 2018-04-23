package com.excilys.gradureau.computer_database.validator;

import java.time.LocalDateTime;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;

import com.excilys.gradureau.computer_database.model.Computer;

public class ComputerValidator {

    public static void check(Computer computer) throws WrongObjectStateException {
        checkName(computer);
        LocalDateTime introduced = computer.getIntroduced();
        LocalDateTime discontinued = computer.getDiscontinued();
        if (introduced != null && discontinued != null) {
            if (introduced.compareTo(discontinued) > 0) {
                throw new WrongObjectStateException("A computer can only be discontinued after its introduced date.");
            }
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

    public static void checkName(Computer computer) throws WrongObjectStateException {
        if (computer.getName() == null || computer.getName().length() == 0) {
            throw new WrongObjectStateException("missing name in Computer object");
        }
    }

}
