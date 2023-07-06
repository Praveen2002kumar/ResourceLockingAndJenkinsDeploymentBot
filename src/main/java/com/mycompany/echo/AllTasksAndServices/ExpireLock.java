package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.mycompany.echo.AllModels.AllContext;
import com.mycompany.echo.AllModels.LockedResourceModel;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class ExpireLock {


    @Autowired
    LockedResourceRepo lockedResourceRepo;

    @Autowired
    AlertCard alertCard;

    @Autowired
    AllContext allContext;

    @Scheduled(fixedDelay = 1000)
    public void deleteExpireLocks() {

        List<LockedResourceModel> list = lockedResourceRepo.findAll();
        for (LockedResourceModel lockedResourceModel : list) {

            LocalDateTime time = lockedResourceModel.getExpiretime();
            if (time == null) time = LocalDateTime.now();
            LocalDateTime currenttime = LocalDateTime.now();
            if (time.isBefore(currenttime)) {


                String text = "Your lock for resource : " + lockedResourceModel.getResource() + " has expired";
                System.out.println(lockedResourceModel.getResource());

                String ownerEmail = lockedResourceModel.getUseremail();
                List<TurnContext> contexts = allContext.getContext(ownerEmail);
                for (TurnContext context : contexts) {
                    alertCard.showAlert("🚨🚨🚨🚨Alert", text, context);
                }


                lockedResourceRepo.deleteById(lockedResourceModel.getId());
            }
        }
    }

}
