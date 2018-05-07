package com.excilys.gradureau.computer_database.validator;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;

public class CompanyValidator {

    public static void check(Company company) throws WrongObjectStateException {
        checkName(company);
    }

    public static void checkId(Company company, boolean shouldHave) throws WrongObjectStateException {
        Long id = company.getId();
        if (id == null == shouldHave) {
            String errorMessage = "id specified in company object";
            if (!shouldHave) {
                errorMessage = "No " + errorMessage;
            }
            throw new WrongObjectStateException(errorMessage);
        }
    }

    public static void checkId(Company company) throws WrongObjectStateException {
        checkId(company, true);
    }

    public static void checkName(Company company) throws WrongObjectStateException {
        if (company.getName() == null || company.getName().length() == 0) {
            throw new WrongObjectStateException("missing name in company object");
        }
    }

}