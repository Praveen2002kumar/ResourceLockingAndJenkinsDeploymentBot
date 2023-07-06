package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.AllContext;
import com.mycompany.echo.AllModels.LockedResourceModel;
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

    public String getExchange(String senderemail, String resource, TurnContext turnContext, String timeString) {
        long time;
        try {
            time = Long.parseLong(timeString);
        } catch (NumberFormatException e) {
            return "number format exception";
        }

        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String useremail = teamsAcc.getEmail();
        List<TurnContext> list = allContext.getContext(senderemail);


        if (lockedResourceRepo.findByResource(resource) == null || !lockedResourceRepo.findByResource(resource).getUseremail().equals(useremail)) {
            return "You have not lock of this resource";
        }


        lockedResourceRepo.deleteById(lockedResourceRepo.findByResource(resource).getId());
        lockedResourceModel.setUseremail(senderemail);
        lockedResourceModel.setResource(resource);
        lockedResourceModel.setExpiretime(LocalDateTime.now().plusMinutes(time));
        lockedResourceRepo.save(lockedResourceModel);
        for (TurnContext context : list) {
            alertCard.showAlert("🔔🔔🔔🔔Notification", "Lock for resource : " + lockedResourceModel.getResource() + " has been granted to you : ", context);
        }
        return "Successfully granted lock to : " + senderemail;

    }
}
