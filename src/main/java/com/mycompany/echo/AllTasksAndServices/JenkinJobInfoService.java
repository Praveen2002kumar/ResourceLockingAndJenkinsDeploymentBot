package com.mycompany.echo.AllTasksAndServices;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.echo.AllModels.JenkinsJobInfoModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.Base64;


@Controller
public class JenkinJobInfoService {
    //        String jenkinsUrl = "http://localhost:8080";
    String jenkinsUrl="https://qa4-build.sprinklr.com/jenkins";
    //        String username = "Praveen_Kumar";
    String username="praveen.kumar@sprinklr.com";
    //        String password = "11526c2640716f0683072286fe8c801ae5";
    String password="11cac87e679a977391343de33757fdf4ae";

    public String getJobInfo(String JOB_NAME) throws JsonProcessingException {
        String messageToUser = "Job Not Found";
        String apiUrl = jenkinsUrl + "/job/" + JOB_NAME + "/api/json";
        HttpHeaders headers = createBasicAuthHeaders(username,password);
        HttpEntity<String> entity = new HttpEntity<>(headers);

       try{
           RestTemplate restTemplate = new RestTemplate();
           ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

           if (response.getStatusCode().is2xxSuccessful()) {
               String jobInfoJson = response.getBody();
               ObjectMapper objectMapper = new ObjectMapper();
               JenkinsJobInfoModel jobInfo = objectMapper.readValue(jobInfoJson, JenkinsJobInfoModel.class);


               messageToUser = "JobName : " + jobInfo.getDisplayName()+"\n\n" + "Description : " + jobInfo.getDescription()+"\n\n" + "NextBuildNumber : " + jobInfo.getNextBuildNumber()+"\n\n"+"Color :"+jobInfo.getColor();
//            System.out.println("Job information JSON:\n" + jobInfoJson);
           } else {
               messageToUser = "Failed to retrieve job information. Status code: " + response.getStatusCode();
               System.out.println("Failed to retrieve job information. Status code: " + response.getStatusCode());
           }
       }catch (HttpClientErrorException e){
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
