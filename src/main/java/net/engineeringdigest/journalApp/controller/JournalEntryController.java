package net.engineeringdigest.journalApp.controller;


import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> entries = user.getJournalEntries();
        if(entries != null && !entries.isEmpty()){
            return new ResponseEntity<>(entries , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveEntry(myEntry , userName);
            return new ResponseEntity<>(myEntry , HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @DeleteMapping()
//    public ResponseEntity<Void> deleteAll(){
//        String userName = SecurityContextHolder.getContext()
//                            .getAuthentication()
//                            .getName();
//        journalEntryService.deleteAllByUserName(userName);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }

    @GetMapping("/id/{requestedId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId requestedId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> result = user.getJournalEntries().stream().filter(x -> x.getId().equals(requestedId)).collect(Collectors.toList());
        if(!result.isEmpty()){
            Optional<JournalEntry> journalEntry = journalEntryService.getById(requestedId);
            if(journalEntry.isPresent()){
                return new ResponseEntity<>(journalEntry.get() , HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{requestedId}")
    public ResponseEntity<Void> deleteJournalEntryById(@PathVariable ObjectId requestedId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed =  journalEntryService.deleteById(requestedId, userName);
        if(removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/id/{requestedId}")
    public ResponseEntity<JournalEntry> updateJournalEntryById(
            @PathVariable ObjectId requestedId,
            @RequestBody JournalEntry newEntry)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUserName(userName);
            List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(requestedId)).collect(Collectors.toList());
            if(!collect.isEmpty()) {
                JournalEntry oldEntry = journalEntryService.getById(requestedId).orElse(null);
                if (oldEntry != null) {
                    if (newEntry.getTitle() != null && !newEntry.getTitle().isEmpty()) {
                        oldEntry.setTitle(newEntry.getTitle());
                    }
                    if (newEntry.getContent() != null && !newEntry.getContent().isEmpty()) {
                        oldEntry.setContent(newEntry.getContent());
                    }
                    journalEntryService.saveEntry(oldEntry);
                    return new ResponseEntity<>(oldEntry, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
