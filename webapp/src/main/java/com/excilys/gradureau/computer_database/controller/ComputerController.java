package com.excilys.gradureau.computer_database.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.excilys.gradureau.computer_database.dto.ComputerDTO;
import com.excilys.gradureau.computer_database.exception.WrongObjectStateException;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.util.Page;

@Controller
@RequestMapping("/")
public class ComputerController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerController.class);
    
    //URLS
    public static final String DASHBOARD_URL = "dashboard";
    public static final String ADD_COMPUTER_URL = "add-computer";
    public static final String EDIT_COMPUTER_URL = "edit-computer";
    public static final String DELETE_COMPUTER_URL = "delete-computers";

    //JSP FILES
    public static final String DASHBOARD_JSP = "dashboard";
    public static final String ADD_COMPUTER_JSP = "addComputer";
    public static final String EDIT_COMPUTER_JSP = "editComputer";
    
    @Autowired(required = true)
    private ICrudCDB cdb;
    @Autowired(required = true)
    @Qualifier("COMPANIES")
    private List<Company> COMPANIES;
    
    /*
     * DASHBOARD
     */
    
    @GetMapping(path = { "/", DASHBOARD_URL })
    public String dashboard(ModelMap model,
            // pagination
            @RequestParam(name = "pageNo", defaultValue = "1") int requestedPage,
            @RequestParam(name = "resultsPerPage", defaultValue = "10") int resultsPerPage,
            //search
            @RequestParam(name = "search", required = false) String searchedKeyWords) {
        
        int offset = (requestedPage - 1) * resultsPerPage;
        Page<Computer> page;
        
        // search logic
        if(searchedKeyWords != null) {
            page = cdb.filterByName(searchedKeyWords, offset, resultsPerPage);
            model.addAttribute("searchedKeyWords", searchedKeyWords);
            model.addAttribute("uri", DASHBOARD_URL + "?search=" + searchedKeyWords);
        } else {
            page = cdb.listComputers(
                    offset,
                    resultsPerPage
                    );
            model.addAttribute("uri", DASHBOARD_URL);
        }
        List<ComputerDTO> computers = page.getContent().stream()
                .map(c -> new ComputerDTO(c))
                .collect(Collectors.toList());
        
        model.addAttribute("resultsFound", page.getTotal(true));
        model.addAttribute("page", page);
        model.addAttribute("computers", computers);
        
        return DASHBOARD_JSP;
    }
    
    /*
     * ADD COMPUTER
     */
    
    @RequestMapping(path = ADD_COMPUTER_URL, method = RequestMethod.GET)
    public String addComputer(ModelMap model) {
        model.addAttribute("companies", COMPANIES);
        return ADD_COMPUTER_JSP;
    }
    
    @RequestMapping(path = ADD_COMPUTER_URL, method = RequestMethod.POST, params = {
            "computerName",
            "introduced",
            "discontinued",
            "companyId"
    })
    public String addComputer(ModelMap model,
            @ModelAttribute("computerData") ComputerForm computerData) {
        //if not valid do 
        // attempt to create computer, if we can't we redirect to ADD_COMPUTER_JSP, else to EDIT_COMPUTER_JSP
        Optional<Computer> optionalNewComputer;
        try {
            optionalNewComputer = cdb.createComputer(computerData.toComputer());
        } catch (WrongObjectStateException e) {
            model.addAttribute("warning", e.getMessage());
            LOGGER.error(ADD_COMPUTER_JSP + " FRONTEND VALIDATION SHOULD HAVE TAKEN CARE OF THIS");
            model.addAttribute("companies", COMPANIES);
            return ADD_COMPUTER_JSP;
        }
        return "redirect:" + EDIT_COMPUTER_URL + "/" + optionalNewComputer.get().getId().toString();
    }
    
    /*
     * EDIT COMPUTER
     */
    
    @RequestMapping(path = EDIT_COMPUTER_URL + "/{pk:\\d+}", method = RequestMethod.GET)
    public ModelAndView editComputer(ModelMap model,
            @PathVariable(name = "pk") Long computerId) {
        Computer computerData = new Computer();
        computerData.setId(computerId);
        try {
            computerData = cdb.showComputerDetails(computerData).orElse(null);
        } catch (WrongObjectStateException e) {return new ModelAndView(EDIT_COMPUTER_JSP, HttpStatus.NOT_FOUND);} // impossible since we're sure to have an Id
        if(computerData == null)
            return new ModelAndView(EDIT_COMPUTER_JSP, HttpStatus.NOT_FOUND);
        model.addAttribute("companies", COMPANIES);
        model.addAttribute("computer", computerData);
        return new ModelAndView(EDIT_COMPUTER_JSP) ;
    }
    
    @RequestMapping(path = EDIT_COMPUTER_URL + "/{pk:\\d+}", method = RequestMethod.POST, params = {
            "id",
            "computerName",
            "introduced",
            "discontinued",
            "companyId"
            })
    public ModelAndView editComputer(ModelMap model,
            @PathVariable(name = "pk") Long computerId,
            @ModelAttribute("computerData") ComputerForm computerData) {
        if(!computerId.equals(computerData.getId()))
            return new ModelAndView(EDIT_COMPUTER_JSP, HttpStatus.BAD_REQUEST);
        Optional<Computer> optionalUpdatedComputer = Optional.empty();
        try {
            optionalUpdatedComputer = cdb.updateComputer(computerData.toComputer());
        } catch (WrongObjectStateException e) {
            LOGGER.debug(EDIT_COMPUTER_URL,e);
            model.addAttribute("warning", e.getMessage());
        } finally {
            if(!optionalUpdatedComputer.isPresent()) {
                model.addAttribute("computer", computerData);
            } else {
                model.addAttribute("computer", optionalUpdatedComputer.get());
                model.addAttribute("updatedWithSuccess", true);
            }
        }
        model.addAttribute("companies", COMPANIES);
        return new ModelAndView(EDIT_COMPUTER_JSP, model);
    }
    
    /*
     * DELETE COMPUTER
     */
    
    @PostMapping(path = DELETE_COMPUTER_URL, params = { "selection" })
    public String deleteComputer(ModelMap model,
            @RequestParam(name = "selection") String computerIdsToDelete) {
        for(String computerId : computerIdsToDelete.split(",")) {
            Computer computerToDelete = new Computer();
            try {
                computerToDelete.setId(Long.valueOf(computerId));
                cdb.deleteComputer(computerToDelete);
            } catch (WrongObjectStateException | NumberFormatException e) {
                LOGGER.debug(DELETE_COMPUTER_URL, e);
            }
        }
        return "redirect:" + DASHBOARD_URL;
    }
    
    /*
     * FORM BEAN
     */

    public static class ComputerForm {
        private Long id;
        private String computerName;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate introduced;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate discontinued;
        private Long companyId;
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getComputerName() {
            return computerName;
        }
        public void setComputerName(String computerName) {
            this.computerName = computerName;
        }
        public LocalDate getIntroduced() {
            return introduced;
        }
        public void setIntroduced(LocalDate introduced) {
            this.introduced = introduced;
        }
        public LocalDate getDiscontinued() {
            return discontinued;
        }
        public void setDiscontinued(LocalDate discontinued) {
            this.discontinued = discontinued;
        }
        public Long getCompanyId() {
            return companyId;
        }
        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }
        public Computer toComputer() {
            return new Computer(id, computerName,
                    (introduced == null ? null : introduced.atStartOfDay()),
                    (discontinued == null ? null : discontinued.atStartOfDay()),
                    companyId <= 0 ? null : new Company(companyId,null));
        }
    }
    
}
