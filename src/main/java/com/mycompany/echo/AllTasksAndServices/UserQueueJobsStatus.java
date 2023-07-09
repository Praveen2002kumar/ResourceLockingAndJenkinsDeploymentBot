package com.mycompany.echo.AllTasksAndServices;

import com.mycompany.echo.AllModels.QueueJobModel;
import com.mycompany.echo.AllRepositories.QueueJobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UserQueueJobsStatus {
    @Autowired
    QueueJobRepo queueJobRepo;
//    public String getStatus(){
//        String response="";
//        List<QueueJobModel> AllQueueJob=queueJobRepo.findAll();
//        for(QueueJobModel queueJob:AllQueueJob){
//            String itemId=queueJob.getQueuid();
//            String queueItemUrl = jenkinsUrl + "/queue/item/" + itemId + "/api/json";
//            ResponseEntity<String> queueItemResponse = restTemplate.exchange(queueItemUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
//            String queueItemStatus = queueItemResponse.getBody();
//
//            response+="Jobname : "+queueJob.getJobname()+" , "
//
//        }
//    }
}
