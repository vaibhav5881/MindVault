package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;
    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        try {
            boolean isSaved = userService.saveNewUser(newUser);

            if (isSaved) {
                return new ResponseEntity<>(newUser, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            // Optional: log the exception
            // log.error("Error while creating user", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/health-check")
    public String healthCheck(){
        return "OK";
    }
}
