package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.LockedResourceModel;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IncreaseLock {

    @Autowired
    LockedResourceRepo lockedResourceRepo;

    public String getIncrease(String operation,String resource, String timeString, TurnContext turnContext){


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
                    lockedResource.setExpiretime(finaltime);
                    lockedResourceRepo.deleteById(lockedResource.getId());
                    lockedResourceRepo.save(lockedResource);
                    return  "successfully increase time on lock";
                } else {
                    finaltime = oldExpireTime.minusMinutes(minutes);
                    lockedResource.setExpiretime(finaltime);
                    lockedResourceRepo.deleteById(lockedResource.getId());
                    lockedResourceRepo.save(lockedResource);
                    return "successfully decrease time on lock";
                }


            } catch (NumberFormatException e) {
                System.out.println(e);
                return "Number Format Exception";
            }

        } else return "this resource is not locked by you";

    }
}
