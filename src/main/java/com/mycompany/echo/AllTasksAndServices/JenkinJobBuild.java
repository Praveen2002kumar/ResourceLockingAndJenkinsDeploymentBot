package com.mycompany.echo.AllTasksAndServices;

import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class JenkinJobBuild {

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    LockedResourceRepo lockedResourceRepo;

    public String triggerJob(List<String> list, String uniqueid) throws InterruptedException {
//        String jenkinsUrl = "http://localhost:8080";
        String jenkinsUrl="https://qa4-build.sprinklr.com/jenkins";
//        String username = "Praveen_Kumar";
        String username="praveen.kumar@sprinklr.com";
//        String password = "11526c2640716f0683072286fe8c801ae5";
        String password="11cac87e679a977391343de33757fdf4ae";
        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the Basic Authentication header
        String authHeader = username + ":" + password;
        String base64AuthHeader = Base64.getEncoder().encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + base64AuthHeader);

        // Trigger the build
        try {
            String jobName = list.get(0);
            StringBuilder apiUrl = new StringBuilder(jenkinsUrl + "/job/" + list.get(0) + "/buildWithParameters");
            for (int i = 2; i < list.size(); i += 2) {
                if (i == 2) apiUrl.append('?');
                apiUrl.append(list.get(i));
                apiUrl.append('=');
//                System.out.println(uniqueid);
//                System.out.println(lockedResourceRepo.findByResource(list.get(i+1)).getUniqueid());

//                if (resourceRepository.findByResourcename(list.get(i + 1)) == null)
//                    return list.get(i + 1) + "resource not found";
//                if (lockedResourceRepo.findByResource(list.get(i + 1)) != null && !lockedResourceRepo.findByResource(list.get(i + 1)).getUniqueid().equals(uniqueid))
//                    return list.get(i + 1) + " resource is already locked";
//                if (lockedResourceRepo.findByResource(list.get(i + 1)) == null || !lockedResourceRepo.findByResource(list.get(i + 1)).getUniqueid().equals(uniqueid))
//                    return list.get(i + 1) + " this resource is not locked by you";
                apiUrl.append(list.get(i + 1));

                apiUrl.append('&');
            }


            ResponseEntity<String> triggerResponse = restTemplate.exchange(apiUrl.toString(), HttpMethod.POST, new HttpEntity<>(headers), String.class);

            // Extract the Location header from the response
            String locationHeader = triggerResponse.getHeaders().getFirst("Location");

            // Extract the queue item ID from the Location header
            String itemId = extractItemIdFromLocationHeader(locationHeader);

            System.out.println("Queue Item ID: " + itemId);

            boolean itemInQueue = true;
            while (true) {
                // Get the queue item status
                String queueItemUrl = jenkinsUrl + "/queue/item/" + itemId+ "/api/json";
                ResponseEntity<String> queueItemResponse = restTemplate.exchange(queueItemUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
                String queueItemStatus = queueItemResponse.getBody();

                // Check if the item is still in the queue
                if (queueItemStatus.contains("\"why\":null")) {
                    Thread.sleep(300000); // Wait for 5 min before checking again
                } else {

                    String buildUrl1 = jenkinsUrl + "/job/" + jobName + "/api/json";
                    ResponseEntity<String> buildNumberResponse = restTemplate.exchange(buildUrl1, HttpMethod.GET, new HttpEntity<>(headers), String.class);
                    String buildNumber = extractBuildNumber(buildNumberResponse.getBody()) ;
                    long buildNumberLong=Long.parseLong(buildNumber);


                    String buildUrl2 = jenkinsUrl + "/job/" + jobName + "/" + buildNumberLong + "/api/json";
                    ResponseEntity<String> statusResponse = restTemplate.exchange(buildUrl2, HttpMethod.GET, new HttpEntity<>(headers), String.class);

                    // Extract the build status from the response
                    String buildStatus = extractBuildStatus(statusResponse.getBody());

//                    System.out.println("Build Status: " + buildStatus);

                    itemInQueue = false;
                    String linkText = "CheckStatus";
                    buildNumberLong++;
//                    String url = "http://localhost:8080/job/" + jobName + "/" +buildNumberLong;
                     String url="https://qa4-build.sprinklr.com/jenkins/job/"+jobName+"/"+buildNumberLong;
                    String markdownLink = "[" + linkText + "](" + url + ")";

                    String responseMessage = markdownLink + " " + buildStatus;

                    return responseMessage;
                }
            }
        } catch (HttpClientErrorException | IndexOutOfBoundsException e) {
            System.out.println(e);
        }
        return "Job not found";
    }

    private String extractBuildStatus(String statusResponse) {

        return statusResponse.contains("\"result\":\"SUCCESS\"") ? "SUCCESS" : "FAILURE";
    }

    private String extractBuildNumber(String statusResponse) {
        // Parse the build response JSON and extract the build number field
        // You can use a JSON parsing library like Jackson or Gson for this task
        // Here's a simple example assuming the build response is in a known format
        // Adapt the code based on the actual structure of the build response
        // For example, if the build response contains a field named "number", you can directly extract it
        return statusResponse.contains("\"number\":") ? statusResponse.split("\"number\":")[1].split(",")[0].trim() : "";
    }

    private String extractItemIdFromLocationHeader(String locationHeader) {

        String[] parts = locationHeader.split("/");
        return parts[parts.length - 1];
    }

}
