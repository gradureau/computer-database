package com.excilys.gradureau.computer_database.persistance.dao.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Computer;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ComputerMapperTest {
    
    @Mock
    ResultSet resultSet;
    
    @BeforeAll
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void valueOfCompleteResultSet() throws SQLException {
        Long id = 1L;
        String name = "computer name";
        LocalDateTime introduced = LocalDateTime.now().minusMonths(6L);
        LocalDateTime discontinued = LocalDateTime.now();
        Long companyId = 1L;
        String companyName = "company name";
        
        //Mocking
        Mockito.doReturn(id).when(resultSet).getLong("id");
        Mockito.doReturn(name).when(resultSet).getString("name");
        Mockito.doReturn(Timestamp.valueOf(introduced)).when(resultSet).getTimestamp("introduced");
        Mockito.doReturn(Timestamp.valueOf(discontinued)).when(resultSet).getTimestamp("discontinued");
        Mockito.doReturn(companyId).when(resultSet).getLong("company_id");
        Mockito.doReturn(companyName).when(resultSet).getString("company_name");
        
        assertEquals(new Computer(
                id,
                name,
                introduced,
                discontinued,
                new Company(companyId, companyName)
                ),
                ComputerMapper.valueOf(resultSet));
    }
    
}
