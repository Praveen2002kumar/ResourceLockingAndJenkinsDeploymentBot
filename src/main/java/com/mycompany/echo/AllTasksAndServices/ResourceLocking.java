package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.ExpireLockNotificationModel;
import com.mycompany.echo.AllModels.LockedResourceModel;
import com.mycompany.echo.AllRepositories.ExpireLockNotificationRepo;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ResourceLocking {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private LockedResourceRepo lockedResourceRepo;

    @Autowired
    SendNotification sendNotification;

    @Autowired
    ConvertUTCToIST convertUTCToIST;

    @Autowired
    LockedResourceModel lockedResourceModel;

    @Autowired
    ExpireLockNotificationRepo expireLockNotificationRepo;

    @Autowired
    ExpireLockNotificationModel expireLockNotificationModel;

    @Autowired
    SendMention sendMention;


    /**
     * used to lock resource by user
     * @param resource  resource name
     * @param hourString time in hour
     * @param minuteString time in minutes
     * @param turnContext tunrcontext
     * @return return a string is locked resources
     */

    public synchronized String LockResource(String resource,String hourString,String minuteString, TurnContext turnContext) {
        if(resourceRepository.findByResourcename(resource)==null)return "Resource not found";
          //acquire lock
        Long minutes;
        Long hour;
        try {
            hour=Long.parseLong(hourString);
           minutes = Long.parseLong(minuteString);
        }catch (NumberFormatException e){
            return "Number format exception";
        }
        minutes=minutes+hour*60;

        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String useremail=teamsAcc.getEmail();
        if(useremail==null)useremail="test@sprinklr.com";



        try {

        LockedResourceModel lockedResource=lockedResourceRepo.findByResource(resource);
          if(lockedResource!=null) {
             sendNotification.notify(useremail,resource,minutes);

              return "resource is already locked by : "+lockedResource.getUseremail()+" Till : "+convertUTCToIST.getIST(lockedResourceRepo.findByResource(resource).getExpiretime());
          }

            LocalDateTime currenttime=LocalDateTime.now();
         lockedResourceModel.setUseremail(useremail);
          lockedResourceModel.setResource(resource);
          lockedResourceModel.setExpiretime(currenttime.plusMinutes(minutes));
          lockedResourceRepo.save(lockedResourceModel);

          expireLockNotificationModel.setExpiretime(currenttime.plusMinutes(minutes));
          expireLockNotificationModel.setUseremail(useremail);
          expireLockNotificationModel.setResource(resource);
          expireLockNotificationRepo.save(expireLockNotificationModel);

         return "successfully locked the resource";
        } catch (NullPointerException e){
            System.out.println(e);
        }
        return "command not found";
    }

    /**
     *this function is used to unlock the user lock resourcr
     */
    public synchronized String UnlockResource(String resource,TurnContext turnContext) {
        // Acquire the lock
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String useremail=teamsAcc.getEmail();
        if(useremail==null)useremail="test@sprinklr.com";
         if(resourceRepository.findByResourcename(resource)==null)return "Resource not found";
        LockedResourceModel lockedResource=lockedResourceRepo.findByResource(resource);
        if(lockedResource==null)return "Resource is already unlock";
        if( lockedResource.getUseremail().equals(useremail)){
            lockedResourceRepo.deleteById(lockedResource.getId());
            return "resource unlocked successfully";
        }else return "You cannot unlocked this resource";


    }

}
