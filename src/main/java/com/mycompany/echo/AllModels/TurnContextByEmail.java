package com.mycompany.echo.AllModels;

import com.microsoft.bot.builder.TurnContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TurnContextByEmail {
    private Map<String, TurnContext> usercontext;

    public TurnContextByEmail(){
        usercontext=new HashMap<>();
    }
    public void setContext(TurnContext turnContext,String userid){
        usercontext.put(userid,turnContext);
    }
    public TurnContext getContext(String email){
        return usercontext.get(email);
    }
}
