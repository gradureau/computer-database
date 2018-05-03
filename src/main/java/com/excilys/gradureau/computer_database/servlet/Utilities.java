package com.excilys.gradureau.computer_database.servlet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.excilys.gradureau.computer_database.model.Computer;

public abstract class Utilities {
    public static boolean allParametersAreNotNull(HttpServletRequest request, String... parameterNames ) {
        return Arrays.asList(parameterNames).stream().allMatch(parameterName -> request.getParameter(parameterName) != null);
    }
    
    public static Optional<Computer> mapRequestToOptionalComputerWithoutCompany(HttpServletRequest request, boolean withId) {
        Computer computerData = null;
        if(allParametersAreNotNull(request, "computerName", "introduced", "discontinued")
                && withId == (request.getParameter("id") != null)
                && request.getParameter("computerName").length() != 0) {
            computerData = new Computer(
                    !withId ? null : Long.valueOf(request.getParameter("id")),
                    request.getParameter("computerName"),
                    request.getParameter("introduced").length() == 0 ? null
                            : LocalDate.parse(request.getParameter("introduced"), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                            request.getParameter("discontinued").length() == 0 ? null
                                    : LocalDate.parse(request.getParameter("discontinued"), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(),
                                    null
                    );
        }
        return Optional.ofNullable(computerData);        
    }
    public static Optional<Computer> mapRequestToOptionalComputerWithoutCompany(HttpServletRequest request) {
        return mapRequestToOptionalComputerWithoutCompany(request, true);
    }
}
