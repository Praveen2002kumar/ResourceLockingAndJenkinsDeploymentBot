package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllRepositories.JenkinsTokenRepo;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class Abortjob {
  @Autowired
    JenkinsTokenRepo jenkinsTokenRepo;

    StringBuilder responseStatus= new StringBuilder("job not found");
    String JENKINS_URL = "https://81fb-2400-80c0-3001-12fd-00-1.ngrok-free.app";
//    String JENKINS_URL="https://qa4-build.sprinklr.com/jenkins";

//    String USERNAME="praveen.kumar@sprinklr.com";
//            String PASSWORD = "11526c2640716f0683072286fe8c801ae5";
//    String PASSWORD="11cac87e679a977391343de33757fdf4ae";

    public String abortJob(String JOB_NAME, String buildNumber, TurnContext turnContext) {
        String messageToUser = "Invalid job name or buildnumber";

        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail = teamsAcc.getEmail();
        String USERNAME=userEmail;
        USERNAME="Praveen_Kumar";
        if(jenkinsTokenRepo.findByEmail(userEmail)==null)return "Access token not found";
        String PASSWORD=jenkinsTokenRepo.findByEmail(userEmail).getToken();
        RestTemplate restTemplate = new RestTemplate();

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the Basic Authentication header
        String authHeader = USERNAME + ":" + PASSWORD;
        String base64AuthHeader = Base64.getEncoder().encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + base64AuthHeader);

        // Trigger the build
        try {


            StringBuilder apiUrl = new StringBuilder(JENKINS_URL + "/job/" + JOB_NAME + "/"+buildNumber+"/stop");


            ResponseEntity<String> response = restTemplate.exchange(apiUrl.toString(), HttpMethod.POST, new HttpEntity<>(headers), String.class);
            System.out.println(response.getStatusCode());

                messageToUser = "Jenkins job aborted successfully! : "+JOB_NAME+" , "+buildNumber ;
        } catch (HttpClientErrorException e) {
            System.out.println(e);

        }
        return messageToUser;
    }

}


