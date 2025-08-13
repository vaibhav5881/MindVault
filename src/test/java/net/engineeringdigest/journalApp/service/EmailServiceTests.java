package net.engineeringdigest.journalApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail(){
        String to = "vaibhavkumar5881@gmail.com";
        String subject = "Testing Java mail sender";
        String body = "OK report";
        emailService.sendEmail(to , subject , body);
    }
}
