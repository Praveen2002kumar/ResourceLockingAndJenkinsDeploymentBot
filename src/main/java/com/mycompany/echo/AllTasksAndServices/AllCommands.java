package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllCommands {

    public String getCommnds(){

        String messageToUser="\n\n"+" 1) To login admin : login admin email pass" ;
        messageToUser+="\n\n";
        messageToUser+=" 2) To add resources by admin : add resource resourcename";
        messageToUser+="\n\n";
        messageToUser+=" 3) To lock resource : lock resource resourcename timeperiod";
        messageToUser+="\n\n";
        messageToUser+=" 4) To unlock resource : unlock resource resourcename";
        messageToUser+="\n\n";
        messageToUser+=" 5) To show all resources : all resources";
        messageToUser+="\n\n";
        messageToUser+=" 6) To inc/dec time on lock : inc/dec lock resourcename time";
        messageToUser+="\n\n";
        messageToUser+=" 7) To Send notification : send notify resourcename time";
        messageToUser+="\n\n";
        messageToUser+=" 8) To grant lock to user : grant resource resourcename username";
        messageToUser+="\n\n";
        messageToUser+=" 9) To get notificaiton : get notify";
        messageToUser+="\n\n";
        messageToUser+=" 10) To get Expire lock alert : expire locks";
        messageToUser+="\n\n";
        messageToUser+=" 11) To trigger jenkins build : jobname build parameters value";
        messageToUser+="\n\n";
        messageToUser+=" 12) To get status of a build : jobname status";
        messageToUser+="\n\n";
        messageToUser+=" 13) To logout admin : logout admin ";
//        alertCard.showAlert("All Commands",messageToUser,turnContext,uniqueid);
        return messageToUser;
    }
}
