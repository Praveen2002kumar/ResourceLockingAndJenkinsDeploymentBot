package com.mycompany.echo.AllTasksAndServices;


import com.microsoft.bot.builder.TurnContext;

import com.mycompany.echo.AllModels.UserBuildJobModel;
import com.mycompany.echo.AllRepositories.UserBuildJobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TriggerJobStatus{

    @Autowired
    AlertCard alertCard;

    @Autowired
    UserBuildJobRepo userBuildJobRepo;
    @Autowired
    UserBuildJobModel userBuildJobModel;


    public void getStatus(String jobName,String itemId,Long buildNumberLong,TurnContext turnContext)  {
//                String jenkinsUrl = "http://localhost:8080";
        String jenkinsUrl = "https://qa4-build.sprinklr.com/jenkins";
//        String username = "Praveen_Kumar";
        String username = "praveen.kumar@sprinklr.com";
//        String password = "11526c2640716f0683072286fe8c801ae5";
        String password = "11cac87e679a977391343de33757fdf4ae";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the Basic Authentication header
        boolean itemInQueue=true;

        while(itemInQueue) {

            String authHeader = username + ":" + password;
            String base64AuthHeader = Base64.getEncoder().encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + base64AuthHeader);


            // Get the queue item status
            String queueItemUrl = jenkinsUrl + "/queue/item/" + itemId + "/api/json";
            ResponseEntity<String> queueItemResponse = restTemplate.exchange(queueItemUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String queueItemStatus = queueItemResponse.getBody();

            // Check if the item is still in the queue
            if (queueItemStatus.contains("\"why\":null")) {
                try {
                    Thread.sleep(300000); // Wait for 5 min before checking again
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                itemInQueue=false;
                String buildUrl2 = jenkinsUrl + "/job/" + jobName + "/" + buildNumberLong + "/api/json";
                ResponseEntity<String> statusResponse = restTemplate.exchange(buildUrl2, HttpMethod.GET, new HttpEntity<>(headers), String.class);

                // Extract the build status from the response
                String buildStatus = extractBuildStatus(statusResponse.getBody());
                buildNumberLong++;
                String url = jenkinsUrl + "/job/" + jobName + "/" + buildNumberLong;
                String markdownLink = "[" + "CheckStatus" + "](" + url + ")";
                userBuildJobModel.setJobname(jobName);
                userBuildJobModel.setStatus(buildStatus);
                userBuildJobModel.setUrl(markdownLink);
                userBuildJobModel.setEmail(username);
                userBuildJobRepo.save(userBuildJobModel);
                alertCard.showAlert("🔔🔔Deploy Status", buildStatus + " : " + jobName, turnContext);
//                return buildStatus + " : " + jobName;
            }
        }
//        return "job not found";


    }

    private String extractBuildStatus(String statusResponse) {

        return statusResponse.contains("\"result\":\"SUCCESS\"") ? "SUCCESS" : "FAILURE";
    }

    private String extractBuildNumber(String statusResponse) {

        return statusResponse.contains("\"number\":") ? statusResponse.split("\"number\":")[1].split(",")[0].trim() : "";
    }

}
