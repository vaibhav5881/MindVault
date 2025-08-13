package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.api.respone.WeatherResponse;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.service.WeatherService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        List<User> users = userService.getAll();
        if(users != null && !users.isEmpty()){
            return new ResponseEntity<>(users , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/id/{requestedId}")
    public ResponseEntity<User> getById(@PathVariable ObjectId requestedId){
        Optional<User> user = userService.findById(requestedId);
        if(user.isPresent()){
            return new ResponseEntity<>(user.get() , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(){
        userService.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/id/{requestedId}")
    public ResponseEntity<Void> deleteById(@PathVariable ObjectId requestedId){
        userService.deleteById(requestedId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<User> updateUser( @RequestBody User newUser ){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User oldUser = userService.findByUserName(userName);
            if (oldUser != null) {
                if (newUser.getUserName() != null && !newUser.getUserName().isEmpty()) {
                    oldUser.setUserName(newUser.getUserName());
                }
                if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
                    oldUser.setPassword(newUser.getPassword());
                }
                userService.saveNewUser(oldUser);
                return new ResponseEntity<>(oldUser, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/greeting")
    public ResponseEntity<?> greeting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather("Azamgarh");
        String greeting = "";
        if(weatherResponse != null){
            greeting = " , Weather feels like " + weatherResponse.getCurrent().getFeelslike();
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + greeting , HttpStatus.OK);
    }
//    @PutMapping
//    public ResponseEntity<User> updatePassword( @RequestBody User newUser){
//        try {
//            User oldUser = userService.findByUserName(newUser.getUserName());
//            if (oldUser != null) {
//                if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
//                    oldUser.setPassword(newUser.getPassword());
//                }
//                userService.saveUser(oldUser);
//                return new ResponseEntity<>(oldUser, HttpStatus.OK);
//            }
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (Exception e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
}
