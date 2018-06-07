package com.excilys.gradureau.computer_database.webservice;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.service.ICrudCDB;

@RestController
@RequestMapping(path = "/api/v1.0.0/computers", produces = "application/json")
public class ComputerRestController {
    
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerRestController.class);
    private static final int MAX_RESULTS_PER_PAGE = 100;
    private static final int DEFAULT_RESULTS_PER_PAGE = 10;
    private static final String IDS_DO_NOT_MATCH = "request id and object id do not match";
    private final ICrudCDB CDB;
    
    @Autowired
    public ComputerRestController(final ICrudCDB CDB) {
        this.CDB = CDB;
    }
    
    @GetMapping("/{pk:\\d+}")
    public ResponseEntity<Computer> getComputer(@PathVariable("pk") Long pk) throws WrongObjectStateException {
        Computer computerData = new Computer();
        computerData.setId(pk);
        Optional<Computer> result = CDB.showComputerDetails(computerData);
        if(result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping
    public List<Computer> listComputers(Optional<Integer> page, Optional<Integer> resultsPerPage, Optional<String> search) {
        int nResults = DEFAULT_RESULTS_PER_PAGE;
        if(resultsPerPage.isPresent()) {
            int r = resultsPerPage.get();
            if(r <= MAX_RESULTS_PER_PAGE)
                nResults = r;
        }
        int offset = (page.orElse(1) - 1) * nResults;
        return CDB.filterByName( search.orElse(""), offset, nResults)
                .getContent();
    }
    
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createComputer(@RequestBody Computer computer) {
        try {
            CDB.createComputer(computer);
        } catch (WrongObjectStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    @PutMapping(path = "/{pk:\\d+}", consumes = "application/json")
    public ResponseEntity<String> updateComputer(
            @PathVariable("pk") Long pk,
            @RequestBody Computer computer) {
        
        if(!pk.equals(computer.getId()))
            return new ResponseEntity<>(IDS_DO_NOT_MATCH, HttpStatus.BAD_REQUEST);
        try {
            CDB.updateComputer(computer);
        } catch (WrongObjectStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @DeleteMapping("/{pk:\\d+}")
    public ResponseEntity<String> deleteComputer(@PathVariable("pk") Long pk) {
        Computer computerData = new Computer();
        computerData.setId(pk);
        try {
            CDB.deleteComputer(computerData);
        } catch (WrongObjectStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
