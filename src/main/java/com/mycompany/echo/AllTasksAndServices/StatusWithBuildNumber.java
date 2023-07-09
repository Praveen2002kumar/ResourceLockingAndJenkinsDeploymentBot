package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class StatusWithBuildNumber {
    @Autowired
    AlertCard alertCard;
    public String getStatus(String jobName, String buildNumber, TurnContext turnContext){

        String jenkinsUrl = "http://localhost:8080";
//        String jenkinsUrl = "https://qa4-build.sprinklr.com/jenkins";
        String username = "Praveen_Kumar";
//        String username = "praveen.kumar@sprinklr.com";
        String password = "11526c2640716f0683072286fe8c801ae5";
//        String password = "11cac87e679a977391343de33757fdf4ae";

       try{
           RestTemplate restTemplate = new RestTemplate();

           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);

           // Set the Basic Authentication header
           String authHeader = username + ":" + password;
           String base64AuthHeader = Base64.getEncoder().encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
           headers.set("Authorization", "Basic " + base64AuthHeader);

           String buildUrl2 = jenkinsUrl + "/job/" + jobName + "/" + buildNumber + "/api/json";
           ResponseEntity<String> statusResponse = restTemplate.exchange(buildUrl2, HttpMethod.GET, new HttpEntity<>(headers), String.class);

           // Extract the build status from the response
           String buildStatus = extractBuildStatus(statusResponse.getBody());



           alertCard.showAlert("🔔🔔Status",buildStatus+" : "+jobName,turnContext);
           return buildStatus+" : "+jobName;
       }catch (HttpClientErrorException e){
           System.out.println(e);
       }
       return "Invalid jobname or build number";

    }
    private String extractBuildStatus(String statusResponse) {

        return statusResponse.contains("\"result\":\"SUCCESS\"") ? "SUCCESS" : "FAILURE";
    }
}
