package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.Mention;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SendMention {
    public  void sendMentionMessage(TurnContext turnContext, String messageToUser){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();

        Mention mention = new Mention();
        mention.setMentioned(teamsAcc);
        mention.setText(
                "<at>" + "praveen kumar" + "</at>"
        );
        Activity replyActivity = MessageFactory.text(messageToUser);
        replyActivity.setMentions(Collections.singletonList(mention));
        turnContext.sendActivity(replyActivity).thenApply(resourceResponse -> null);
    }
}
