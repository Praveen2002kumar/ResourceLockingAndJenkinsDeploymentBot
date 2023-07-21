package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.HeroCard;
import com.mycompany.echo.AllModels.AllContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationCard {

    @Autowired
    AllContext allContext;

    /**
     * used to send notificaiton
     * @param time time
     * @param senderemail senderemail
     * @param resource resource
     * @param turnContext turncontext
     */

  public void getCard(Long time,String senderemail,String resource,TurnContext turnContext){

      HeroCard card = new HeroCard();
      card.setTitle("🔔🔔🔔🔔Request Notification");
      String text="Send by "+senderemail+" require resource "+resource+" for "+time+" minutes";

      card.setText(text);


      Attachment cardAttachment = new Attachment();
      cardAttachment.setContentType("application/vnd.microsoft.card.hero");
      cardAttachment.setContent(card);


      Activity reply = MessageFactory.attachment(cardAttachment);


      turnContext.sendActivity(reply).join();

  }
}
