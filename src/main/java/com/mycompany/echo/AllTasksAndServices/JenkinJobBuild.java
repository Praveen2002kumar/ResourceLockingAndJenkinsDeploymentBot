package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.mycompany.echo.AllModels.QueueJobModel;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import com.mycompany.echo.AllRepositories.QueueJobRepo;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JenkinJobBuild {

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    LockedResourceRepo lockedResourceRepo;

    @Autowired
    BuildNumberByQueueId buildNumberByQueueId;

    @Autowired
    TriggerJobStatus triggerJobStatus;

    @Autowired
    QueueJobRepo queueJobRepo;

    @Autowired
    QueueJobModel queueJobModel;

    public String triggerJob(String jobName,String chart_name,String release_name,String branch,String mode, TurnContext turnContext) throws InterruptedException {
        String jenkinsUrl = "http://localhost:8080";
//        String jenkinsUrl="https://qa4-build.sprinklr.com/jenkins";
        String username = "Praveen_Kumar";
//        String username="praveen.kumar@sprinklr.com";
        String password = "11526c2640716f0683072286fe8c801ae5";
//        String password="11cac87e679a977391343de33757fdf4ae";
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


            StringBuilder apiUrl = new StringBuilder(jenkinsUrl + "/job/" + jobName + "/buildWithParameters");
            apiUrl.append("?chart_name=").append(chart_name);
            apiUrl.append("&release_name=").append(release_name);
            apiUrl.append("&branch=").append(branch);
            apiUrl.append("&mode=").append(mode);
            String resource = chart_name + " " + release_name;

            ResponseEntity<String> triggerResponse = restTemplate.exchange(apiUrl.toString(), HttpMethod.POST, new HttpEntity<>(headers), String.class);

            // Extract the Location header from the response

            if (triggerResponse.getStatusCode().is2xxSuccessful()) {
                String locationHeader = triggerResponse.getHeaders().getFirst("Location");

                // Extract the queue item ID from the Location header
                String itemId = extractItemIdFromLocationHeader(locationHeader);

                queueJobModel.setJobname(jobName);
                queueJobModel.setQueuid(itemId);
                queueJobModel.setTriggertime(LocalDateTime.now());
                queueJobRepo.save(queueJobModel);

                Long buildNumberLong = buildNumberByQueueId.getBuildNumber(itemId, jobName);

                triggerJobStatus.getStatus(jobName, itemId, buildNumberLong, turnContext);
                System.out.println("Queue Item ID: " + itemId);
                buildNumberLong++;
                String url = jenkinsUrl + "/job/" + jobName + "/" + buildNumberLong;
                String markdownLink = "[" + "CheckStatus" + "](" + url + ")";

                String responseMessage = markdownLink;

                return responseMessage + " " + "Triggered Successfully";

            }

        } catch (HttpClientErrorException | IndexOutOfBoundsException e) {
            System.out.println(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Job not found";
    }


    private String extractItemIdFromLocationHeader(String locationHeader) {

        String[] parts = locationHeader.split("/");
        return parts[parts.length - 1];
    }

}
