package com.excilys.gradureau.computer_database.dto;

import com.excilys.gradureau.computer_database.model.Computer;

public class DTOMapper {
    
    public static ComputerDTO toComputerDTO(Computer computer) {
        return new ComputerDTO(
                computer.getId(),
                computer.getName(),
                computer.getIntroduced(),
                computer.getDiscontinued(),
                computer.getCompany() == null ? null
                        : computer.getName()
                );
    }
    
}
