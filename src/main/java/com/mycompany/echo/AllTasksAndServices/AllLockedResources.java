package com.mycompany.echo.AllTasksAndServices;

import com.mycompany.echo.AllModels.LockedResourceModel;
import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllLockedResources {
    @Autowired
    LockedResourceRepo lockedResourceRepo;

    @Autowired
    ConvertUTCToIST convertUTCToIST;

    public String getLockedResources(){
        List<LockedResourceModel> list=lockedResourceRepo.findAll();
        StringBuilder messageToUser= new StringBuilder("Current Locked Resources " + "\n\n");
        for (LockedResourceModel lockedResourceModel : list) {

            messageToUser.append(lockedResourceModel.getResource()).append(" : ").append(convertUTCToIST.getIST(lockedResourceModel.getExpiretime())).append(" , Locked By : ").append(lockedResourceModel.getUseremail());
            messageToUser.append("\n\n");
        }
        return messageToUser.toString();
    }
}
