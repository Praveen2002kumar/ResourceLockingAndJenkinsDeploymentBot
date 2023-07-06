package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.HeroCard;
import org.springframework.stereotype.Component;

@Component
public class NotificationCard {


  public void getCard(Long time,String senderemail,String resource,TurnContext turnContext){

      HeroCard card = new HeroCard();
      card.setTitle("🔔🔔🔔🔔Notification");
      String text="Send by : "+senderemail+" , Required resource : "+resource+" , For time : "+time;
      card.setText(text);


      Attachment cardAttachment = new Attachment();
      cardAttachment.setContentType("application/vnd.microsoft.card.hero");
      cardAttachment.setContent(card);

      Activity reply = MessageFactory.attachment(cardAttachment);

      turnContext.sendActivity(reply).join();

  }
}
