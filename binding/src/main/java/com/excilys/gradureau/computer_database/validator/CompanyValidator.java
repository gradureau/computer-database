package com.excilys.gradureau.computer_database.validator;

import java.util.Properties;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.util.PropertyFileUtility;

public class CompanyValidator {

    private static final String MISSING_NAME_IN_COMPANY_OBJECT;
    private static final String NO;
    private static final String ID_SPECIFIED_IN_COMPANY_OBJECT;
    static {
        Properties strings = PropertyFileUtility.readPropertyFile("strings/validators.properties");
        MISSING_NAME_IN_COMPANY_OBJECT = strings.getProperty("companyValidator.MISSING_NAME_IN_COMPANY_OBJECT");
        NO = strings.getProperty("companyValidator.NO");
        ID_SPECIFIED_IN_COMPANY_OBJECT = strings.getProperty("companyValidator.ID_SPECIFIED_IN_COMPANY_OBJECT");
    }

    public static void check(Company company) throws WrongObjectStateException {
        checkName(company);
    }

    public static void checkId(Company company, boolean shouldHave) throws WrongObjectStateException {
        Long id = company.getId();
        if (id == null == shouldHave) {
            String errorMessage = ID_SPECIFIED_IN_COMPANY_OBJECT;
            if (!shouldHave) {
                errorMessage = NO + errorMessage;
            }
            throw new WrongObjectStateException(errorMessage);
        }
    }

    public static void checkId(Company company) throws WrongObjectStateException {
        checkId(company, true);
    }

    public static void checkName(Company company) throws WrongObjectStateException {
        if (company.getName() == null || company.getName().length() == 0) {
            throw new WrongObjectStateException(MISSING_NAME_IN_COMPANY_OBJECT);
        }
    }

}