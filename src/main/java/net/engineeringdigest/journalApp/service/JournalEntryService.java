package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry entry, String userName){
        try {
            User user = userService.findByUserName(userName);
            entry.setDate(LocalDateTime.now());
            JournalEntry savedEntry = journalEntryRepository.save(entry);
            user.getJournalEntries().add(savedEntry);
            userService.saveUser(user);
        } catch(Exception e){
            log.error("Exception " , e);
            throw new RuntimeException("An error occured while saving the entry." , e);
        }
    }
    public void saveEntry(JournalEntry entry){
        try {
            journalEntryRepository.save(entry);
        } catch(Exception e){
            log.error("Exception " , e);
        }
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getById(ObjectId requestedId){
        return journalEntryRepository.findById(requestedId);
    }

//    public void deleteAllByUserName(String userName){
//        User user = userService.findByUserName(userName);
//        if(user != null){
//            for(JournalEntry entry : user.getJournalEntries()){
//            }
//            user.getJournalEntries().clear();
//            userService.saveUser(user);
//        }
//
//    }

    @Transactional
    public boolean deleteById(ObjectId requestedId, String userName){
        boolean removed = false;
        try{
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(requestedId));
            if(removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(requestedId);
            }
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occured while delete the entry." , e);
        }

        return removed;
    }
}
