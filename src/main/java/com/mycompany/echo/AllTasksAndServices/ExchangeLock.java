package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.AllContext;
import com.mycompany.echo.AllModels.ExpireLockNotificationModel;
import com.mycompany.echo.AllModels.LockedResourceModel;
import com.mycompany.echo.AllRepositories.ExpireLockNotificationRepo;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExchangeLock {


    @Autowired
    LockedResourceModel lockedResourceModel;


    @Autowired
    AlertCard alertCard;
    @Autowired
    AllContext allContext;

    @Autowired
    LockedResourceRepo lockedResourceRepo;

    @Autowired
    ExpireLockNotificationModel expireLockNotificationModel;

    @Autowired
    ExpireLockNotificationRepo expireLockNotificationRepo;

    @Autowired
    ConvertUTCToIST convertUTCToIST;

    public String getExchange(String senderemail, String resource, TurnContext turnContext, String hourString,String minuteString) {
        long time;
        long hour;
        try {
            hour=Long.parseLong(hourString);
            time = Long.parseLong(minuteString);
        } catch (NumberFormatException e) {
            return "please enter number";
        }
        time=time+hour*60;

        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String useremail = teamsAcc.getEmail();
        List<TurnContext> list = allContext.getContext(senderemail);

        if(lockedResourceRepo.findByResource(resource) == null)return "You have not lock of this resource";
        System.out.println(lockedResourceRepo.findByResource(resource).getUseremail()+useremail);


       if(lockedResourceRepo.findByResource(resource).getUseremail().equals(useremail)){
           lockedResourceRepo.deleteById(lockedResourceRepo.findByResource(resource).getId());
           lockedResourceModel.setUseremail(senderemail);
           lockedResourceModel.setResource(resource);
           lockedResourceModel.setExpiretime(LocalDateTime.now().plusMinutes(time));
           lockedResourceRepo.save(lockedResourceModel);
           if(expireLockNotificationRepo.findByResource(resource)!=null)expireLockNotificationRepo.deleteById(expireLockNotificationRepo.findByResource(resource).getId());
           expireLockNotificationModel.setExpiretime(LocalDateTime.now().plusMinutes(time));
           expireLockNotificationModel.setUseremail(senderemail);
           expireLockNotificationModel.setResource(resource);
           expireLockNotificationRepo.save(expireLockNotificationModel);
           for (TurnContext context : list) {
               alertCard.showAlert("🔔🔔🔔🔔Notification", "Lock for resource : " + lockedResourceModel.getResource() + " has been granted to you , Expire at : "+convertUTCToIST.getIST(LocalDateTime.now().plusMinutes(time)), context);
           }
           return "Successfully granted lock to : " + senderemail;
       }
       return "You have not lock of this resource";

    }
}
