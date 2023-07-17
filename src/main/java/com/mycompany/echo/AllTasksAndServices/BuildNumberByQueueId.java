package com.mycompany.echo.AllTasksAndServices;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BuildNumberByQueueId {

    public long getBuildNumber(String queueId, String jobName, String username, String password) throws Exception {
        String jenkinsUrl="https://qa4-build.sprinklr.com/jenkins/";


//        String jenkinsUrl = "https://81fb-2400-80c0-3001-12fd-00-1.ngrok-free.app/";


        RestTemplate restTemplate = new RestTemplate();

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the Basic Authentication header
        String authHeader = username + ":" + password;
        String base64AuthHeader = Base64.getEncoder().encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + base64AuthHeader);


        String buildUrl1 = jenkinsUrl + "job/" + jobName + "/lastBuild/buildNumber";
        ResponseEntity<String> buildNumberResponse = restTemplate.exchange(buildUrl1, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        String buildNumber = extractBuildNumber(buildNumberResponse.getBody());
        System.out.println(buildNumber);

        long buildNumberLong = 0;
        if (buildNumberResponse.getStatusCode().is2xxSuccessful()) {
            // Extract the build number from the response body
            buildNumberLong = Integer.parseInt(buildNumberResponse.getBody());

            System.out.println("Build Number: " + buildNumber);
        } else {
            System.out.println("Failed to retrieve build number. Response code: ");
        }
        return buildNumberLong;

    }

    private String extractBuildNumber(String statusResponse) {
        return statusResponse.contains("\"number\":") ? statusResponse.split("\"number\":")[1].split(",")[0].trim() : "";
    }
}
