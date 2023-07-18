package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.QueueJobModel;
import com.mycompany.echo.AllModels.TriggerJobModel;
import com.mycompany.echo.AllModels.UserBuildJobModel;
import com.mycompany.echo.AllRepositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

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
    ResourceRepository resourceRepository;

    @Autowired
    TriggerJobRepo triggerJobRepo;

    @Autowired
    TriggerJobModel triggerJobModel;

    @Autowired
    JenkinsTokenRepo jenkinsTokenRepo;

    public String triggerJob(String jobName, String chart_name, String release_name, String branch, String mode, TurnContext turnContext) throws InterruptedException {
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail = teamsAcc.getEmail();

       String jenkinsUrl="https://qa4-build.sprinklr.com/jenkins";


        String username=userEmail;
        if(jenkinsTokenRepo.findByEmail(username)==null)return "Access token not found use command : add token tokenvalue";
        String password=jenkinsTokenRepo.findByEmail(username).getToken();
        username="Praveen_Kumar";

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
            System.out.println(chart_name);

            StringBuilder apiUrl = new StringBuilder(jenkinsUrl + "/job/" + jobName + "/buildWithParameters");

            apiUrl.append("?CHART_NAME=").append(chart_name);

            apiUrl.append("&CHART_RELEASE_NAME=").append(release_name);

            apiUrl.append("&CHART_REPO_BRANCH=").append(branch);

            apiUrl.append("&JOB_MODE=").append(mode);
            String resource = chart_name + "-" + release_name;
            if (lockedResourceRepo.findByResource(resource) != null)
                return resource + " : is already lock by : " + lockedResourceRepo.findByResource(resource).getUseremail();

            ResponseEntity<String> triggerResponse = restTemplate.exchange(apiUrl.toString(), HttpMethod.POST, new HttpEntity<>(headers), String.class);
//            System.out.println(apiUrl);
            // Extract the Location header from the response

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

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return "Job not found";
    }


    private String extractItemIdFromLocationHeader(String locationHeader) {

        String[] parts = locationHeader.split("/");
        return parts[parts.length - 1];
    }

    private String extractUrl(String statusResponse) {

        return statusResponse.contains("\"url\":") ? statusResponse.split("\"url\":")[1].split(",")[0].trim() : "";
    }

}
