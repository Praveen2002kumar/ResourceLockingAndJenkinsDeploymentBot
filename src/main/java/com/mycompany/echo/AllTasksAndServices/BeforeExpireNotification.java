package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.mycompany.echo.AllModels.AllContext;
import com.mycompany.echo.AllModels.ExpireLockNotificationModel;
import com.mycompany.echo.AllRepositories.ExpireLockNotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BeforeExpireNotification {

    @Autowired
    ExpireLockNotificationRepo expireLockNotificationRepo;

    @Autowired
    AlertCard alertCard;

    @Autowired
    AllContext allContext;

    @Autowired
    ConvertUTCToIST convertUTCToIST;

    @Scheduled(fixedDelay = 5000)
      public void expireLockNotify(){
            try {
                List<ExpireLockNotificationModel> lockedResources = expireLockNotificationRepo.findAll();
                for (ExpireLockNotificationModel lockedResourceModel : lockedResources) {

                    LocalDateTime time = lockedResourceModel.getExpiretime();
                    if (time == null) time = LocalDateTime.now();
                    LocalDateTime fiveMinuteAfterCurrentTime = LocalDateTime.now().plusMinutes(5);
                    if (time.isBefore(fiveMinuteAfterCurrentTime)) {


                        String text = "Your lock for resource : " + lockedResourceModel.getResource() + " will expired at : " + convertUTCToIST.getIST(lockedResourceModel.getExpiretime());

                        String ownerEmail = lockedResourceModel.getUseremail();
                        List<TurnContext> contexts = allContext.getContext(ownerEmail);
                        for (TurnContext context : contexts) {
                            alertCard.showAlert("🚨🚨🚨🚨Expire Alert", text, context);
                        }
                        expireLockNotificationRepo.deleteById(lockedResourceModel.getId());

                    }
                }
            } catch (NullPointerException e) {
                System.out.println(e);
            }

       }
}
