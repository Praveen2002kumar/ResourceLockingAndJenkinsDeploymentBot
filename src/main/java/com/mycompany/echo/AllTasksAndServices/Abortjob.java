package com.mycompany.echo.AllTasksAndServices;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class Abortjob {


    StringBuilder responseStatus= new StringBuilder("job not found");
    //        String jenkinsUrl = "http://localhost:8080";
    String JENKINS_URL="https://qa4-build.sprinklr.com/jenkins";
    //        String username = "Praveen_Kumar";
    String USERNAME="praveen.kumar@sprinklr.com";
    //        String password = "11526c2640716f0683072286fe8c801ae5";
    String PASSWORD="11cac87e679a977391343de33757fdf4ae";

    public String abortJob(String JOB_NAME,String builNumber) {
        String messageToUser = "job not found";
        String apiUrl = JENKINS_URL + "/job/" + JOB_NAME + "/"+builNumber+"/stop";
        HttpHeaders headers = createBasicAuthHeaders(USERNAME, PASSWORD);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            System.out.println(response.getStatusCode());

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();


            if (response.getStatusCode().is2xxSuccessful()) {
                messageToUser = "Jenkins job aborted successfully!" + "\n\n" + "Status Code : " + response.getStatusCode();
            } else {
                messageToUser = "Failed to abort Jenkins job. Status code: " + response.getStatusCode();
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
