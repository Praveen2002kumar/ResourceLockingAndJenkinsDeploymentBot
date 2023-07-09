package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.ExpireLockNotificationModel;
import com.mycompany.echo.AllModels.LockedResourceModel;
import com.mycompany.echo.AllRepositories.ExpireLockNotificationRepo;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IncreaseLock {

    @Autowired
    LockedResourceRepo lockedResourceRepo;

   @Autowired
    ExpireLockNotificationModel expireLockNotificationModel;

   @Autowired
    ExpireLockNotificationRepo expireLockNotificationRepo;

    public String getIncrease(String operation,String resource, String timeString, TurnContext turnContext){

      String response="response";
        LockedResourceModel lockedResource = lockedResourceRepo.findByResource(resource);

        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail=teamsAcc.getEmail();

        if (lockedResource == null) {
            return "resource not Locked";
        } else if (lockedResource.getUseremail().equals(userEmail)) {
            LocalDateTime oldExpireTime = lockedResource.getExpiretime();
            try {
                Long minutes = Long.parseLong(timeString);
                LocalDateTime finaltime = LocalDateTime.now();
                if (operation.equals("inc")) {
                    finaltime = oldExpireTime.plusMinutes(minutes);
                    response= "successfully increase time on lock";
                } else {
                    finaltime = oldExpireTime.minusMinutes(minutes);
                   response= "successfully decrease time on lock";
                }
                lockedResource.setExpiretime(finaltime);
                lockedResourceRepo.deleteById(lockedResource.getId());
                lockedResourceRepo.save(lockedResource);

                if(expireLockNotificationRepo.findByResource(resource)!=null)expireLockNotificationRepo.deleteById(expireLockNotificationRepo.findByResource(resource).getId());
                expireLockNotificationModel.setResource(resource);
                expireLockNotificationModel.setUseremail(lockedResource.getUseremail());
                expireLockNotificationModel.setExpiretime(finaltime);
                expireLockNotificationRepo.save(expireLockNotificationModel);
           return response;

            } catch (NumberFormatException e) {
                System.out.println(e);
                return "Number Format Exception";
            }

        } else return "this resource is not locked by you";

    }
}
