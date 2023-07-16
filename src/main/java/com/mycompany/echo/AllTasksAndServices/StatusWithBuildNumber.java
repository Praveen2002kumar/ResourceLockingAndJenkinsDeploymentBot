package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllRepositories.JenkinsTokenRepo;
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
    JenkinsTokenRepo jenkinsTokenRepo;

    @Autowired
    AlertCard alertCard;
    public String getStatus(String jobName, String buildNumber, TurnContext turnContext){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail = teamsAcc.getEmail();
//        String jenkinsUrl="https://qa4-build.sprinklr.com/jenkins";
//        String username="praveen.kumar@sprinklr.com";
//        String password="1120ed4c398b26347643d298081be50185";

//        String jenkinsUrl = "http://localhost:8080";
        String jenkinsUrl="https://81fb-2400-80c0-3001-12fd-00-1.ngrok-free.app";
//
        String username = userEmail;
//
        if(jenkinsTokenRepo.findByEmail(username)==null)return "Access token not found use command : add token tokenvalue";
        String password = jenkinsTokenRepo.findByEmail(username).getToken();
//
        username="Praveen_Kumar";

       try{
           RestTemplate restTemplate = new RestTemplate();

           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);

           // Set the Basic Authentication header
           String authHeader = username + ":" + password;
           String base64AuthHeader = Base64.getEncoder().encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
           headers.set("Authorization", "Basic " + base64AuthHeader);

           String apiUrl = jenkinsUrl + "/job/" + jobName + "/"+ buildNumber+"/api/json";
           ResponseEntity<String> statusResponse = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

           // Extract the build status from the response
           String buildStatus = extractBuildStatus(statusResponse.getBody());
           if(statusResponse.getBody().contains("\"inProgress\":true"))buildStatus="In Progress";

           alertCard.showAlert("🔔🔔Job Status",buildStatus+" : "+jobName,turnContext);
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
