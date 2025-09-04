package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.dto.UserDto;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs" , description = "Read User , Create Admin & Reload App Cache")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers(){
        List<User> users = userService.getAll();
        if (users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin")
    public void createAdmin(@RequestBody UserDto adminDto){
        User admin = new User();
        admin.setUserName(adminDto.getUserName());
        admin.setPassword(adminDto.getPassword());
        admin.setEmail(adminDto.getEmail());
        admin.setSentimentAnalysis(adminDto.isSentimentAnalysis());
        userService.saveNewAdmin(admin);
    }

    @GetMapping("/reload-app-cache")
    public ResponseEntity<String> reloadAppCache(){
        String message;
        try {
            appCache.init();
            message = "AppCache reloaded.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (Exception e){
            message = "AppCache reloading failed.";
            return new ResponseEntity<>(message , HttpStatus.EXPECTATION_FAILED);
        }
    }

}
