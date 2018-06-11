package com.excilys.gradureau.computer_database.webservice.cdb;

import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private WebTarget computersEndpoint;
    private Invocation.Builder computersInvocationBuilder;
    
    
    @Autowired
    public WebServiceBasedCDB (@Qualifier("java8jacksonmapper") ObjectMapper objectMapper) {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(objectMapper);
        Client client = ClientBuilder.newClient(new ClientConfig(jacksonProvider));
        
        WebTarget generalEndpoint = client.target(HOST + GENERAL_API_ENDPOINT);
        computersEndpoint = generalEndpoint.path(COMPUTERS_ENDPOINT);
        computersInvocationBuilder = computersEndpoint.request(MediaType.APPLICATION_JSON);
    }

    @Override
    public Page<Computer> listComputers(int start, int resultsNumber) {
        return new Page<>(
                Arrays.asList(
                        computersInvocationBuilder.get(Computer[].class)
                ),
                start,
                resultsNumber
                );
    }

    @Override
    public Page<Company> listCompanies(int start, int resultsCount) {
        throw new UnsupportedOperationException();
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
    public Optional<Computer> createComputer(Computer computer) throws WrongObjectStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Computer> createComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Computer> updateComputer(Computer computer) throws WrongObjectStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Computer> updateComputer(Computer computer, Long companyId) throws WrongObjectStateException {
        throw new UnsupportedOperationException();
        
    }

    @Override
    public boolean deleteComputer(Computer computer) throws WrongObjectStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<Computer> filterByName(String nameFilter, int start, int resultsCount) {
        throw new UnsupportedOperationException();
        
    }

    @Override
    public boolean deleteCompany(Company company) throws WrongObjectStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int countCompanies() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int countComputers() {
        throw new UnsupportedOperationException();
    }

}
