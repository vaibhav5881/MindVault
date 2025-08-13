package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
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
    public void createAdmin(@RequestBody User admin){
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
