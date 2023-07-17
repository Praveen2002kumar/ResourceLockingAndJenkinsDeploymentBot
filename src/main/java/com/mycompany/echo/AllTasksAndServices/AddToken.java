package com.mycompany.echo.AllTasksAndServices;


import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.JenkinsTokenModel;
import com.mycompany.echo.AllRepositories.JenkinsTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddToken {

    @Autowired
    JenkinsTokenRepo jenkinsTokenRepo;

    @Autowired
    JenkinsTokenModel jenkinsTokenModel;

    public String add(TurnContext turnContext,String tokenvalue){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail = teamsAcc.getEmail();
        if(jenkinsTokenRepo.findByEmail(userEmail)!=null)jenkinsTokenRepo.deleteById(jenkinsTokenRepo.findByEmail(userEmail).getId());
        jenkinsTokenModel.setEmail(userEmail);
        jenkinsTokenModel.setToken(tokenvalue);
        jenkinsTokenRepo.save(jenkinsTokenModel);
        return "Successfully added token";
    }
}
