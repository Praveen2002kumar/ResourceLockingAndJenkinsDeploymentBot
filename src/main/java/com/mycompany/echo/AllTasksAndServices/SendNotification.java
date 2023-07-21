package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.mycompany.echo.AllModels.AllContext;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendNotification {

    @Autowired
    LockedResourceRepo lockedResourceRepo;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    NotificationCard notificationCard;


    @Autowired
    AllContext allContext;

    /**
     * used to send notification to user
     * @param senderemail senderemail
     * @param resource resource name
     * @param time time
     * @return return string
     */

    public String notify(String senderemail, String resource, Long time) {

        if (resourceRepository.findByResourcename(resource) == null) return "Resource does not exist";
        if (lockedResourceRepo.findByResource(resource) == null) return "Resource is already unlocked";
        String reciveremail = lockedResourceRepo.findByResource(resource).getUseremail();

        List<TurnContext> list = allContext.getContext(reciveremail);
        for (TurnContext turnContext : list) {
            notificationCard.getCard(time, senderemail, resource, turnContext);
        }

        return "successfully send the notificaiton";
    }
}
