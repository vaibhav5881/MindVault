package net.engineeringdigest.journalApp.scheduler;

import ch.qos.logback.core.encoder.EchoEncoder;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.model.SentimentData;
import net.engineeringdigest.journalApp.repository.UserRespositoryImpl;
import net.engineeringdigest.journalApp.service.EmailService;
import net.engineeringdigest.journalApp.service.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRespositoryImpl userRespository;
    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;
    @Autowired
    private AppCache appCache;
    @Autowired
    private KafkaTemplate<String , SentimentData> kafkaTemplate;


    public void fetchUsersAndSendSaMail(){
        List<User> users= userRespository.getUserForSA();
        for(User user : users){
            List<Sentiment> sentiments = user.getJournalEntries().stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7 , ChronoUnit.DAYS)))
                    .map(x -> x.getSentiment()).collect(Collectors.toList());
            Map<Sentiment , Integer> sentimentMap = new HashMap<>();
            for(Sentiment sentiment : sentiments){
                if(sentiment != null){
                    sentimentMap.put(sentiment , sentimentMap.getOrDefault(sentiment , 0) + 1);
                }
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for(Map.Entry<Sentiment , Integer> entry : sentimentMap.entrySet()){
                if(entry.getValue() > maxCount){
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }
//            if(mostFrequentSentiment != null){
//                emailService.sendEmail(
//                        user.getEmail(),
//                        "Sentiment for last 7 days ",
//                        mostFrequentSentiment.toString());
//            }
            if (mostFrequentSentiment != null){
                SentimentData sentimentData = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment("Sentiment for last 7 days " + mostFrequentSentiment.toString())
                        .build();
                try{
                    kafkaTemplate.send("weekly-sentiments" , sentimentData.getEmail() , sentimentData);
                }catch (Exception e){
                    emailService.sendEmail(sentimentData.getEmail() , "Sentiment for previous week ", sentimentData.getSentiment());
                }
            }

        }
    }

//    @Scheduled(cron = "0 * * * * *")
//    public void fetchUsersAndSendSaMail(){
//        List<User> users = userRespository.getUserForSA();
//        for(User user : users){
//            List<String> journalEntries = user
//                    .getJournalEntries()
//                    .stream()
//                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7 , ChronoUnit.DAYS)))
//                    .map(x -> x.getContent())
//                    .collect(Collectors.toList());
//            String entry = String.join(" ", journalEntries);
//            String sentiment = sentimentAnalysisService.getSentiment(entry);
//            emailService.sendEmail(user.getEmail() , "Sentiment for last 7 days", sentiment);
//
//        }
//    }

    @Scheduled(cron = "0 */5 * * * *")
    public void reloadAppCache(){
        appCache.init();
    }
}
