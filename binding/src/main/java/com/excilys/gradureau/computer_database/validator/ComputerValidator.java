package com.excilys.gradureau.computer_database.validator;

import java.time.LocalDateTime;
import java.util.Properties;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

public class ComputerValidator {
    
    private static final String MISSING_NAME_IN_COMPUTER_OBJECT;
    private static final String NO;
    private static final String ID_SPECIFIED_IN_COMPUTER_OBJECT;
    private static final String A_COMPUTER_CAN_ONLY_BE_DISCONTINUED_AFTER_ITS_INTRODUCED_DATE;
    static {
        Properties strings = PropertyFileUtility.readPropertyFile("strings/validators.properties");
        MISSING_NAME_IN_COMPUTER_OBJECT = strings.getProperty("computerValidator.MISSING_NAME_IN_COMPUTER_OBJECT");
        NO = strings.getProperty("computerValidator.NO");
        ID_SPECIFIED_IN_COMPUTER_OBJECT = strings.getProperty("computerValidator.ID_SPECIFIED_IN_COMPUTER_OBJECT");
        A_COMPUTER_CAN_ONLY_BE_DISCONTINUED_AFTER_ITS_INTRODUCED_DATE = strings.getProperty("computerValidator.A_COMPUTER_CAN_ONLY_BE_DISCONTINUED_AFTER_ITS_INTRODUCED_DATE");
    }

    public static void check(Computer computer) throws WrongObjectStateException {
        checkName(computer);
        LocalDateTime introduced = computer.getIntroduced();
        LocalDateTime discontinued = computer.getDiscontinued();
        if (introduced != null && discontinued != null) {
            if (introduced.compareTo(discontinued) > 0) {
                throw new WrongObjectStateException(A_COMPUTER_CAN_ONLY_BE_DISCONTINUED_AFTER_ITS_INTRODUCED_DATE);
            }
        }
    }

    public static void checkId(Computer computer, boolean shouldHave) throws WrongObjectStateException {
        Long id = computer.getId();
        if (id == null == shouldHave) {
            String errorMessage = ID_SPECIFIED_IN_COMPUTER_OBJECT;
            if (!shouldHave) {
                errorMessage = NO + errorMessage;
            }
            throw new WrongObjectStateException(errorMessage);
        }
    }

    public static void checkId(Computer computer) throws WrongObjectStateException {
        checkId(computer, true);
    }

    public static void checkName(Computer computer) throws WrongObjectStateException {
        if (computer.getName() == null || computer.getName().length() == 0) {
            throw new WrongObjectStateException(MISSING_NAME_IN_COMPUTER_OBJECT);
        }
    }

}
