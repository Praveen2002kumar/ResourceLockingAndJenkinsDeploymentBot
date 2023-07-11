package com.mycompany.echo.AllTasksAndServices;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.UserBuildJobModel;
import com.mycompany.echo.AllRepositories.UserBuildJobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class UserSpecificBuildsStatus {


    @Autowired
    UserBuildJobRepo userBuildJobRepo;

    @Autowired
    ConvertUTCToIST convertUTCToIST;


    public String getStatus(TurnContext turnContext){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail=teamsAcc.getEmail();
        List<UserBuildJobModel> userBuilds=userBuildJobRepo.findByEmail(userEmail);
        String response="Your all builds job status";
        response+="\n\n";
        for(UserBuildJobModel userbuild:userBuilds){
            response+="JobName : "+userbuild.getJobname()+" , TriggeredAt : "+convertUTCToIST.getIST(userbuild.getTriggertime())+" , "+userbuild.getUrl();
            response+="\n\n";
        }
        return response;
      }

}
