package com.mycompany.echo.AllTasksAndServices;


import com.microsoft.bot.builder.TurnContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReceiveAllFormParameters {

    @Autowired
    JenkinJobBuild jenkinJobBuild;

    @Autowired
    ExchangeLock exchangeLock;

    @Autowired
    AddToken addToken;

    @Autowired
    ResourceLocking resourceLocking;


    public String getReceive(TurnContext turnContext) {
        String message= "failed to trigger";
        if (turnContext.getActivity().getValue() != null) {
            Object value = turnContext.getActivity().getValue();
            if (value instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) value;

                // Extract the values of input1 and input2
               if(mapData.size()==5 && mapData.containsKey("job_name")){
                   String job_name = (String) mapData.get("job_name");
                   String chart_name = (String) mapData.get("chart_name");
                   String chart_release_name = (String) mapData.get("chart_release_name");
                   String branch = (String) mapData.get("branch");
                   String mode = (String) mapData.get("mode");
                   try {

                       message = jenkinJobBuild.triggerJob(job_name, chart_name, chart_release_name, branch, mode, turnContext);

                   } catch (InterruptedException e) {
                      return "Job not found or invalid parameters";
                   }
               }else if(mapData.size()==4 && mapData.containsKey("email")){
                   String useremail=(String)mapData.get("email");
                   String resource=(String)mapData.get("resource");
                   String hour=(String)mapData.get("hour");
                   String minute=(String)mapData.get("minute");
                  message=exchangeLock.getExchange(useremail,resource,turnContext,hour,minute);
                  

               }else if(mapData.size()==1 && mapData.containsKey("token")){
                   String token= (String) mapData.get("token");
                   message=addToken.add(turnContext,token);

               }else{
                   String resource=(String)mapData.get("resource");
                   String hour=(String)mapData.get("hour");
                   String minute=(String)mapData.get("minute");
                   message=resourceLocking.LockResource(resource,hour,minute,turnContext);
               }
            }
        }

        return message;
    }
}
