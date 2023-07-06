package com.mycompany.echo.AllTasksAndServices;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

public class Abortjob {

    private static final String JENKINS_URL = "http://localhost:8080";
    private static final String USERNAME = "Praveen_Kumar";
    private static final String PASSWORD = "1135a00a99150ea1f778920d1f6fb54039";


    public String abortJob(String JOB_NAME) {
        String messageToUser = "job not found";
        String apiUrl = JENKINS_URL + "/job/" + JOB_NAME + "/1/stop";
        HttpHeaders headers = createBasicAuthHeaders(USERNAME, PASSWORD);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            System.out.println(response.getStatusCode());
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Jenkins job triggered successfully!");
                messageToUser = "Jenkins job triggered successfully!" + "\n\n" + "Status Code : " + response.getStatusCode();
            } else {
                System.out.println("Failed to trigger Jenkins job. Status code: " + response.getStatusCode());
                messageToUser = "Failed to trigger Jenkins job. Status code: " + response.getStatusCode();
            }
        } catch (HttpClientErrorException e) {
            System.out.println(e);
        }
        return messageToUser;
    }

    private HttpHeaders createBasicAuthHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        String authHeader = username + ":" + password;
        byte[] authHeaderBytes = authHeader.getBytes();
        byte[] base64AuthHeaderBytes = Base64.getEncoder().encode(authHeaderBytes);
        String base64AuthHeader = new String(base64AuthHeaderBytes);
        headers.set("Authorization", "Basic " + base64AuthHeader);
        return headers;

    }

}
