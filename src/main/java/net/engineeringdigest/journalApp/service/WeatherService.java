package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.api.respone.WeatherResponse;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.constants.Placeholders;
import net.engineeringdigest.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
    private static final String API = "https://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

//    public WeatherResponse getWeather(String city){
//       String finalAPI =  API.replace("CITY",city).replace("API_KEY", apiKey);
//        ResponseEntity<WeatherResponse> response  = restTemplate.exchange(finalAPI , HttpMethod.GET , null , WeatherResponse.class );
//        WeatherResponse weatherResponse = response.getBody();
//       return weatherResponse;
//    }
    public WeatherResponse getWeather(String city){
        WeatherResponse redisResponse = redisService.get("weather_of_" + city , WeatherResponse.class);
        if(redisResponse != null){
            return redisResponse;
        }else{
            String finalAPI =  appCache.appCache.get(AppCache.keys.WEATHER_API.toString()).replace(Placeholders.CITY,city).replace(Placeholders.API_KEY, apiKey);
            ResponseEntity<WeatherResponse> response  = restTemplate.exchange(finalAPI , HttpMethod.GET , null , WeatherResponse.class );
            WeatherResponse weatherResponse = response.getBody();
            if(weatherResponse != null){
                redisService.set("weather_of_" + city , weatherResponse , 300l);
            }
            return weatherResponse;
        }

    }
    public WeatherResponse postWeather(String city){
        String finalAPI =  API.replace("CITY",city).replace("API_KEY", apiKey);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("key", "value");
        User user = User.builder().userName("Vaibhav").password("1").build();
        HttpEntity<User> httpEntity = new HttpEntity<>(user);
        ResponseEntity<WeatherResponse> response  = restTemplate.exchange(finalAPI , HttpMethod.POST , httpEntity , WeatherResponse.class );
        WeatherResponse weatherResponse = response.getBody();
        return weatherResponse;
    }
}