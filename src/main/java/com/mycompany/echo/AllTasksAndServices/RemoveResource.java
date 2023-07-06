package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllRepositories.ResourceAdminRepository;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoveResource {
    @Autowired
    ResourceAdminRepository resourceAdminRepository;
    @Autowired
    ResourceRepository resourceRepository;
    public String getRemove(String resource, TurnContext turnContext){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail=teamsAcc.getEmail();
       boolean admin= resourceAdminRepository.existsByEmail(userEmail);
       if(admin){
           if(resourceRepository.findByResourcename(resource)==null)return "resource not found";
           resourceRepository.deleteById(resourceRepository.findByResourcename(resource).getId());
           return "Successfully removed the resource";
       }
       return "Only admin can remove";
    }
}
