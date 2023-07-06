package com.mycompany.echo.AllModels;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AllContext {
    private Map<String, List<TurnContext>>usercontext=new HashMap<>();
    public void setContext(TurnContext turnContext){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String email=teamsAcc.getEmail();
        List<TurnContext> values = usercontext.getOrDefault(email, new ArrayList<>());
        values.add(turnContext);
        usercontext.put(email,values);
        System.out.println(usercontext.size());
    }
    public List<TurnContext> getContext(String email){
        return usercontext.get(email);
    }

}
