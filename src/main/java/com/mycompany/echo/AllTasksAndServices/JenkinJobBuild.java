package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.TriggerJobModel;
import com.mycompany.echo.AllModels.UserBuildJobModel;
import com.mycompany.echo.AllRepositories.JenkinsTokenRepo;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import com.mycompany.echo.AllRepositories.TriggerJobRepo;
import com.mycompany.echo.AllRepositories.UserBuildJobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class JenkinJobBuild {

    @Autowired
    BuildNumberByQueueId buildNumberByQueueId;

    @Autowired
    UserBuildJobModel userBuildJobModel;

    @Autowired
    UserBuildJobRepo userBuildJobRepo;

    @Autowired
    LockedResourceRepo lockedResourceRepo;


    @Autowired
    TriggerJobRepo triggerJobRepo;

    @Autowired
    TriggerJobModel triggerJobModel;

    @Autowired
    JenkinsTokenRepo jenkinsTokenRepo;

    @Autowired
    SendNotification sendNotification;

    /**
     * this is used to trigger jenkins job
     * @param jobName job name
     * @param chart_name chart name
     * @param release_name release name
     * @param branch branch name
     * @param mode  mode
     * @param turnContext turncontext of user who trigger this job
     * @return return status code
     * @throws InterruptedException interrupted exception
     */

    public String triggerJob(String jobName, String chart_name, String release_name, String branch, String mode, TurnContext turnContext) throws InterruptedException {
       turnContext.getActivity().removeRecipientMention();
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail = teamsAcc.getEmail();

        String jenkinsUrl="https://qa4-build.sprinklr.com/jenkins";


        String username=userEmail;
        if(jenkinsTokenRepo.findByEmail(username)==null)return "Access token not found use command : add token tokenvalue";
        String password=jenkinsTokenRepo.findByEmail(username).getToken();


        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(release_name);

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the Basic Authentication header
        String authHeader = username + ":" + password;
        String base64AuthHeader = Base64.getEncoder().encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + base64AuthHeader);

        // Trigger the build
        try {

            StringBuilder apiUrl = new StringBuilder(jenkinsUrl + "/job/" + jobName + "/buildWithParameters");
            Map<String,String> parameters=new HashMap<>();
            if(chart_name!=null)parameters.put("CHART_NAME=",chart_name);
            if(release_name!=null)parameters.put("CHART_RELEASE_NAME=",release_name);
            if(branch!=null)parameters.put("CHART_REPO_BRANCH=",branch);
            if(mode!=null)parameters.put("JOB_MODE=",mode);

            List<String> keysList = new ArrayList<>(parameters.keySet());

            if(keysList.size()>0){
                apiUrl.append("?"+keysList.get(0)).append(parameters.get(keysList.get(0)));
            }
            for(int i=1;i<parameters.size();i++){
                apiUrl.append("&"+keysList.get(i)).append(parameters.get(keysList.get(i)));
            }


            String resource = chart_name + "-" + release_name;
            if (lockedResourceRepo.findByResource(resource) != null){

                return resource + " : is already lock by : " + lockedResourceRepo.findByResource(resource).getUseremail();
            }



            ResponseEntity<String> triggerResponse = restTemplate.exchange(apiUrl.toString(), HttpMethod.POST, new HttpEntity<>(headers), String.class);


            if (triggerResponse.getStatusCode().is2xxSuccessful()) {
                String locationHeader = triggerResponse.getHeaders().getFirst("Location");

                // Extract the queue item ID from the Location header
                String itemId = extractItemIdFromLocationHeader(locationHeader);


                Long buildNumberLong = buildNumberByQueueId.getBuildNumber(itemId, jobName,username,password) + 1;


                String url = jenkinsUrl + "/job/" + jobName + "/" + buildNumberLong;


                triggerJobModel.setEmail(userEmail);
                triggerJobModel.setUrl(url+"/api/json");
                triggerJobModel.setBuildnumber(String.valueOf(buildNumberLong));
                triggerJobModel.setJobname(jobName);
                triggerJobRepo.save(triggerJobModel);
                System.out.println("Queue Item ID: " + itemId);


                String responseMessage = "[" + "CheckStatus" + "](" + url + ")";

                userBuildJobModel.setJobname(jobName);
                userBuildJobModel.setUrl(responseMessage);
                userBuildJobModel.setEmail(username);
                userBuildJobModel.setTriggertime(LocalDateTime.now());
                userBuildJobRepo.save(userBuildJobModel);

                return responseMessage + " " + "Triggered Successfully";

            }else return triggerResponse.getStatusCode().toString();

        } catch (Exception e) {
            System.out.println(e);
        }
        return "invalid job name or parameters";
    }


    private String extractItemIdFromLocationHeader(String locationHeader) {

        String[] parts = locationHeader.split("/");
        return parts[parts.length - 1];
    }


}
