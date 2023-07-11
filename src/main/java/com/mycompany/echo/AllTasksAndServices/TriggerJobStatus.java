package com.mycompany.echo.AllTasksAndServices;


import com.microsoft.bot.builder.TurnContext;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TriggerJobStatus implements Runnable{


    private String jobName;
    private String itemId;
    private Long buildNumberLong;
    private TurnContext turnContext;

    public void setArguments(String job,String item,Long build,TurnContext tc){
        jobName=job;
        itemId=item;
        buildNumberLong=build;
        turnContext=tc;
    }


    public void run()  {
                String jenkinsUrl = "http://localhost:8080";

        String username = "Praveen_Kumar";
       
        String password = "11526c2640716f0683072286fe8c801ae5";
     
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
                    Thread.sleep(60000); // Wait for 1 min before checking again
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                itemInQueue=false;
                String buildUrl2 = jenkinsUrl + "/job/" + jobName + "/" + buildNumberLong + "/api/json";
                ResponseEntity<String> statusResponse = restTemplate.exchange(buildUrl2, HttpMethod.GET, new HttpEntity<>(headers), String.class);

                // Extract the build status from the response
                String buildStatus = extractBuildStatus(statusResponse.getBody());
                AlertCard alertCard=new AlertCard();
                alertCard.showAlert("🔔🔔Deploy Status", buildStatus + " : " + jobName, turnContext);

            }
        }



    }

    private String extractBuildStatus(String statusResponse) {

        return statusResponse.contains("\"result\":\"SUCCESS\"") ? "SUCCESS" : "FAILURE";
    }

    private String extractBuildNumber(String statusResponse) {

        return statusResponse.contains("\"number\":") ? statusResponse.split("\"number\":")[1].split(",")[0].trim() : "";
    }

}
