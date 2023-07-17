package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.mycompany.echo.AllModels.AllContext;
import com.mycompany.echo.AllModels.TriggerJobModel;
import com.mycompany.echo.AllRepositories.JenkinsTokenRepo;
import com.mycompany.echo.AllRepositories.TriggerJobRepo;
import com.mycompany.echo.AllRepositories.UserBuildJobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GetTriggerJobStatus {
    @Autowired
    TriggerJobRepo triggerJobRepo;

    @Autowired
    AllContext allContext;

    @Autowired
    AlertCard alertCard;

    @Autowired
    JenkinsTokenRepo jenkinsTokenRepo;

    @Autowired
    UserBuildJobRepo userBuildJobRepo;

    @Scheduled(fixedDelay = 5000)
    public void Status(){
        List<TriggerJobModel> triggerJobs=triggerJobRepo.findAll();
        for(TriggerJobModel triggerJob:triggerJobs){
            String username=triggerJob.getEmail();

            String password=jenkinsTokenRepo.findByEmail(username).getToken();


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(username, password);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);



             try{

                 ResponseEntity<String> response = restTemplate.exchange(
                         triggerJob.getUrl(),
                         HttpMethod.GET,
                         new HttpEntity<>(headers),
                         String.class
                 );

                 if (response.getStatusCode().is2xxSuccessful()) {
                     String responseBody = response.getBody();

                     Boolean inProgress = responseBody.contains("\"result\":null") ;

                     if(!inProgress){
                         List<TurnContext> contexts=allContext.getContext(triggerJob.getEmail());
                         String buildStatus=responseBody.contains("\"result\":\"SUCCESS\"") ? "SUCCESS" : "FAILURE";
                         for(TurnContext turnContext:contexts){
                             alertCard.showAlert("🔔🔔Build Status", buildStatus + " : " + triggerJob.getJobname() + " , " + triggerJob.getBuildnumber(), turnContext);

                         }

                         triggerJobRepo.deleteById(triggerJob.getId());
                     }

                 }
             }catch (HttpClientErrorException e){
                 System.out.println(e);
             }


        }
    }

}
