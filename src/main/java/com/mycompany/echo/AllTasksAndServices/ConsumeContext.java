package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import com.mycompany.echo.AllModels.AllContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConsumeContext {
    @Autowired
    AllContext allContext;

    List<String> recipientIds=new ArrayList<>();
    public  void getConsume(TurnContext turnContext){
        if(!recipientIds.contains(turnContext.getActivity().getFrom().getId())){
            allContext.setContext(turnContext);
            recipientIds.add(turnContext.getActivity().getFrom().getId());
        }

    }
}
