package com.excilys.gradureau.computer_database.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.excilys.gradureau.computer_database.dto.ComputerDTO;
import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.service.ICrudCDB;
import com.excilys.gradureau.computer_database.util.Page;

@Controller
@RequestMapping("/")
public class ComputerController {
    
  //URLS
    public static final String DASHBOARD_URL = "dashboard";
    public static final String ADD_COMPUTER_URL = "/add-computer";
    public static final String EDIT_COMPUTER_URL = "/edit-computer";
    public static final String DELETE_COMPUTER_URL = "/delete-computers";

    //JSP FILES
    public static final String DASHBOARD_JSP = "dashboard";
    public static final String ADD_COMPUTER_JSP = "addComputer";
    public static final String EDIT_COMPUTER_JSP = "editComputer";
    
    @Autowired(required = true)
    private ICrudCDB cdb;
    @Autowired(required = true)
    @Qualifier("COMPANIES")
    private List<Company> COMPANIES;
    
    @RequestMapping(path=DASHBOARD_URL, method = RequestMethod.GET)
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
}
