package com.excilys.gradureau.computer_database.springconfig;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.excilys.gradureau.computer_database.model.Computer;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringJUnitConfig(JacksonConfig.class)
public class JacksonConfigTest  {
    @Autowired
    ObjectMapper objectMapper;
    
    @Test
    public void canSerializeLocalDateTime() {
        assertTrue(objectMapper.canSerialize(LocalDateTime.class));
    }
    
    @Test
    public void canDeserializeLocalDateTime() {
        assertTrue(objectMapper.canDeserialize( objectMapper.constructType(LocalDateTime.class) ) );
    }
    
    @Test
    public void serializeLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        assertDoesNotThrow( () -> objectMapper.writeValueAsString(now));
    }
    
    @Test
    public void serializeUnserializeLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        assertDoesNotThrow( () -> 
        objectMapper.readValue(
            objectMapper.writeValueAsString(now),
            LocalDateTime.class
            )
        );
    }
    
    @Test
    public void serializeUnserializeComputerWithLocalDateTime() {
        Computer computer = new Computer();
        computer.setIntroduced( LocalDateTime.now() );
        assertDoesNotThrow( () -> 
        objectMapper.readValue(
            objectMapper.writeValueAsString(computer),
            Computer.class
            )
        );
    }
    
}
