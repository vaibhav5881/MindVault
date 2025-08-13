package net.engineeringdigest.journalApp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserRespositoryImplTests {

    @Autowired
    private UserRespositoryImpl userRespository;

    @Test
    public void getUserForSATest(){
        assertNotNull(userRespository.getUserForSA());
    }
}
