package com.mycompany.echo.AllTasksAndServices;
import org.springframework.stereotype.Component;

@Component
public class AllCommands {

    public String getCommnds(){


       String messageToUser=" 1) To add/remove resources by admin : add/remove resource resourcename";
        messageToUser+="\n\n";
        messageToUser+=" 2) To lock resource : lock resource resourcename timeperiod";
        messageToUser+="\n\n";
        messageToUser+=" 3) To unlock resource : unlock resource resourcename";
        messageToUser+="\n\n";
        messageToUser+=" 4) To show all resources : all resources";
        messageToUser+="\n\n";
        messageToUser+=" 5) To inc/dec time on lock : inc/dec lock resourcename time";
        messageToUser+="\n\n";
        messageToUser+=" 6) To grant lock to user : grant resource resourcename username";
        messageToUser+="\n\n";
        messageToUser+=" 7) To trigger jenkins build : build job";
        messageToUser+="\n\n";
        messageToUser+=" 8) To get status of User's build : mybuild jobs";
        messageToUser+=" 9) To get status of any build : get status jobname buildnumber";
        return messageToUser;
    }
}
