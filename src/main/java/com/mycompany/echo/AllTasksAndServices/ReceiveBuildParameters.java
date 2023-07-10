package com.mycompany.echo.AllTasksAndServices;


import com.microsoft.bot.builder.TurnContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;
import java.util.Map;

@Component
public class ReceiveBuildParameters {

    @Autowired
    JenkinJobBuild jenkinJobBuild;

    public String getReceive(TurnContext turnContext) {
        String message= "failed to trigger";
        if (turnContext.getActivity().getValue() != null) {
            Object value = turnContext.getActivity().getValue();
            System.out.println(turnContext.getActivity().getName());
            if (value instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) value;

                // Extract the values of input1 and input2
                String job_name = (String) mapData.get("job_name");
                String chart_name = (String) mapData.get("chart_name");
                String chart_release_name = (String) mapData.get("chart_release_name");
                String branch = (String) mapData.get("branch");
                String mode = (String) mapData.get("mode");
                try {
                    message = jenkinJobBuild.triggerJob(job_name, chart_name, chart_release_name, branch, mode, turnContext);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return message;
    }
}
