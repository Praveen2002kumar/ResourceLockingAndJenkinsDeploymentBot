package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.ResourceModel;

import com.mycompany.echo.AllRepositories.ResourceAdminRepository;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminAddedResources {

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ResourceModel resourceModel;

    @Autowired
    ResourceAdminRepository resourceAdminRepository;
    

    public String addResource(String resource, TurnContext turnContext){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail=teamsAcc.getEmail();
        boolean admin=resourceAdminRepository.existsByEmail(userEmail);
        if(!admin)return "Only admin can add the resources";
        if (resourceRepository.findByResourcename(resource) != null) {
            resourceRepository.deleteById(resourceRepository.findByResourcename(resource).getId());

        }

        resourceModel.setEmail(userEmail);
        resourceModel.setResourcename(resource);
        resourceRepository.save(resourceModel);
        return  "Resource added successfully";
    }
}
