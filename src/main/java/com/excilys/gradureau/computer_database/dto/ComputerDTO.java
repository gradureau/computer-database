package com.excilys.gradureau.computer_database.dto;

import java.time.LocalDateTime;

public class ComputerDTO {
    private Long id;
    private String name;
    private LocalDateTime introduced;
    private LocalDateTime discontinued;
    private String companyName;
    public ComputerDTO() {
        super();
    }
    public ComputerDTO(Long id, String name, LocalDateTime introduced, LocalDateTime discontinued, String companyName) {
        super();
        this.id = id;
        this.name = name;
        this.introduced = introduced;
        this.discontinued = discontinued;
        this.companyName = companyName;
    }
    @Override
    public String toString() {
        return "ComputerDTO [id=" + id + ", name=" + name + ", introduced=" + introduced + ", discontinued="
                + discontinued + ", companyName=" + companyName + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
        result = prime * result + ((discontinued == null) ? 0 : discontinued.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((introduced == null) ? 0 : introduced.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComputerDTO other = (ComputerDTO) obj;
        if (companyName == null) {
            if (other.companyName != null)
                return false;
        } else if (!companyName.equals(other.companyName))
            return false;
        if (discontinued == null) {
            if (other.discontinued != null)
                return false;
        } else if (!discontinued.equals(other.discontinued))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (introduced == null) {
            if (other.introduced != null)
                return false;
        } else if (!introduced.equals(other.introduced))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDateTime getIntroduced() {
        return introduced;
    }
    public void setIntroduced(LocalDateTime introduced) {
        this.introduced = introduced;
    }
    public LocalDateTime getDiscontinued() {
        return discontinued;
    }
    public void setDiscontinued(LocalDateTime discontinued) {
        this.discontinued = discontinued;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
