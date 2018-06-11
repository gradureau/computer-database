package com.excilys.gradureau.computer_database.webservice.cdb;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.util.Page;
import com.excilys.gradureau.computer_database.validator.ComputerValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("ws_cdb")
public class WebServiceBasedCDB implements ICrudCDB {
    
    public final String HOST = "http://localhost:8080/console";
    public final String GENERAL_API_ENDPOINT = "/api/v1.0.0";
    public final String COMPUTERS_ENDPOINT = "/computers";
    public final String COMPANIES_ENDPOINT = "/companies";
    private WebTarget computersEndpoint;
    private WebTarget companiesEndpoint;
    
    
    @Autowired
    public WebServiceBasedCDB (@Qualifier("java8jacksonmapper") ObjectMapper objectMapper) {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(objectMapper);
        Client client = ClientBuilder.newClient(new ClientConfig(jacksonProvider));
        
        WebTarget generalEndpoint = client.target(HOST + GENERAL_API_ENDPOINT);
        computersEndpoint = generalEndpoint.path(COMPUTERS_ENDPOINT);
        companiesEndpoint = generalEndpoint.path(COMPUTERS_ENDPOINT);
    }

    @Override
    public Page<Computer> listComputers(int start, int resultsNumber) {
        if(resultsNumber <= 0) resultsNumber = 10;
        int pageNumber = start/resultsNumber + 1;
        return new Page<>(
            Arrays.stream(
                    computersEndpoint
                    .queryParam("page", pageNumber).queryParam("resultsPerPage", resultsNumber)
                    .request(MediaType.APPLICATION_JSON).get(Computer[].class)
            ).collect(Collectors.toList()),
            start,
            resultsNumber
        );
    }

    @Override
    public Page<Company> listCompanies(int start, int resultsCount) {
        if(resultsCount <= 0) resultsCount = 10;
        int pageNumber = start/resultsCount + 1;
        return new Page<>(
            Arrays.stream(
                    companiesEndpoint
                    .queryParam("page", pageNumber).queryParam("resultsPerPage", resultsCount)
                    .request(MediaType.APPLICATION_JSON).get(Company[].class)
            ).collect(Collectors.toList()),
            start,
            resultsCount
        );
    }

    @Override
    public Optional<Computer> showComputerDetails(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        Computer result = computersEndpoint.path("/" + computer.getId())
        .request(MediaType.APPLICATION_JSON)
        .get().readEntity(Computer.class);
        return Optional.ofNullable(result);
    }
    
    @Override
    public Optional<Company> showCompanyDetails(Company company) {
        Company result = companiesEndpoint.path("/" + company.getId())
        .request(MediaType.APPLICATION_JSON)
        .get().readEntity(Company.class);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Computer> createComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer, false);
        ComputerValidator.check(computer);
        Response response = computersEndpoint.request().post(Entity.entity(computer, MediaType.APPLICATION_JSON));
        if(response.getStatus() == HttpStatus.CREATED.value()) {
            Long generatedId = response.readEntity(Long.class);
            computer.setId(generatedId);
            return Optional.of(computer);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Computer> createComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        ComputerValidator.checkId(computer, false);
        ComputerValidator.check(computer);
        if (companyId != null) {
            Company foundCompany = new Company(companyId, null);
            showCompanyDetails(foundCompany)
            .ifPresent(company -> computer.setCompany(company));
        }
        Response response = computersEndpoint.request().post(Entity.entity(computer, MediaType.APPLICATION_JSON));
        if(response.getStatus() == HttpStatus.CREATED.value()) {
            Long generatedId = response.readEntity(Long.class);
            computer.setId(generatedId);
            return Optional.of(computer);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Computer> updateComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        ComputerValidator.check(computer);
        Response response = 
                computersEndpoint.path("/" + computer.getId()).request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(computer, MediaType.APPLICATION_JSON));
        if(response.getStatus() == HttpStatus.NO_CONTENT.value()) {
            return Optional.of(computer);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Computer> updateComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        ComputerValidator.check(computer);
        if (companyId != null) {
            Company foundCompany = new Company(companyId, null);
            showCompanyDetails(foundCompany)
            .ifPresent(company -> computer.setCompany(company));
        }
        Response response = 
                computersEndpoint.path("/" + computer.getId()).request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(computer, MediaType.APPLICATION_JSON));
        if(response.getStatus() == HttpStatus.NO_CONTENT.value()) {
            return Optional.of(computer);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteComputer(Computer computer) throws WrongObjectStateException {
        ComputerValidator.checkId(computer);
        return computersEndpoint.path("/" + computer.getId()).request(MediaType.APPLICATION_JSON)
                .delete().getStatus() == HttpStatus.NO_CONTENT.value();
    }

    @Override
    public Page<Computer> filterByName(String nameFilter, int start, int resultsCount) {
        if(resultsCount <= 0) resultsCount = 10;
        int pageNumber = start/resultsCount + 1;
        return new Page<>(
                Arrays.stream(
                        computersEndpoint
                        .queryParam("page", pageNumber).queryParam("resultsPerPage", resultsCount)
                        .queryParam("search", nameFilter)
                        .request(MediaType.APPLICATION_JSON).get(Computer[].class)
                ).collect(Collectors.toList()),
                start,
                resultsCount
            );
    }

    @Override
    public boolean deleteCompany(Company company) throws WrongObjectStateException {
        return computersEndpoint.path("/" + company.getId()).request(MediaType.APPLICATION_JSON)
                .delete().getStatus() == HttpStatus.NO_CONTENT.value();
    }

    @Override
    public int countCompanies() {
        return companiesEndpoint.queryParam("count", true)
                .request(MediaType.APPLICATION_JSON).get(Integer.class);
    }

    @Override
    public int countComputers() {
        return computersEndpoint.queryParam("count", true)
                .request(MediaType.APPLICATION_JSON).get(Integer.class);
    }

}
