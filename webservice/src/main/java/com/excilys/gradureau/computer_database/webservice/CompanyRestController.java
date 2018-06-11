package com.excilys.gradureau.computer_database.webservice;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.service.ICrudCDB;

@RestController
@RequestMapping(path = "/api/v1.0.0/companies", produces = "application/json")
public class CompanyRestController {
    
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyRestController.class);
    private static final int MAX_RESULTS_PER_PAGE = 100;
    private static final int DEFAULT_RESULTS_PER_PAGE = 10;
    private final ICrudCDB CDB;
    
    @Autowired
    public CompanyRestController(@Qualifier("dao_cdb") final ICrudCDB CDB) {
        this.CDB = CDB;
    }
    
    @GetMapping("/{pk:\\d+}")
    public ResponseEntity<Company> getCompany(@PathVariable("pk") Long pk) {
        Company companyData = new Company();
        companyData.setId(pk);
        Optional<Company> result = CDB.showCompanyDetails(companyData);
        if(result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping
    public List<Company> listCompanys(Optional<Integer> page, Optional<Integer> resultsPerPage, Optional<String> search) {
        int nResults = DEFAULT_RESULTS_PER_PAGE;
        if(resultsPerPage.isPresent()) {
            int r = resultsPerPage.get();
            if(r <= MAX_RESULTS_PER_PAGE)
                nResults = r;
        }
        int offset = (page.orElse(1) - 1) * nResults;
        return CDB.listCompanies(offset, nResults).getContent();
    }
    
    @GetMapping(params = "count")
    public int countCompanys(Boolean count) {
        return CDB.countCompanies();
    }
    
    @DeleteMapping("/{pk:\\d+}")
    public ResponseEntity<String> deleteCompany(@PathVariable("pk") Long pk) {
        Company companyData = new Company();
        companyData.setId(pk);
        try {
            CDB.deleteCompany(companyData);
        } catch (WrongObjectStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
