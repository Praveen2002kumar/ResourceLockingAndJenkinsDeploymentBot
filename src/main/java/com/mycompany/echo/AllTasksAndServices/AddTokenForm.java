package com.mycompany.echo.AllTasksAndServices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AddTokenForm {
    String adaptiveCardJson = "{\n" +
            "  \"type\": \"AdaptiveCard\",\n" +
            "  \"version\": \"1.3\",\n" +
            "  \"body\": [\n" +
            "    {\n" +
            "      \"type\": \"TextBlock\",\n" +
            "      \"text\": \"Add your jenkins token\",\n" +
            "      \"weight\": \"Bolder\",\n" +
            "      \"size\": \"Medium\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"Input.Text\",\n" +
            "      \"id\": \"token\",\n" +
            "      \"placeholder\": \"Enter your token\",\n" +
            "      \"isMultiline\": false,\n" +
            "      \"style\": \"password\",\n" +
            "      \"maxLength\": 50\n" +
            "    }\n" +
            "  ],\n" +
            "  \"actions\": [\n" +
            "    {\n" +
            "      \"type\": \"Action.Submit\",\n" +
            "      \"title\": \"Add\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    public void getForm(TurnContext turnContext){
        Attachment cardAttachment = new Attachment();
        cardAttachment.setContentType("application/vnd.microsoft.card.adaptive");
        try {
            cardAttachment.setContent(new ObjectMapper().readTree(adaptiveCardJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//            cardAttachment.setContent(JsonNodeFactory.instance.textNode(getAdaptiveCardJson()));

        Activity reply = MessageFactory.attachment(cardAttachment);
        Map<String, Object> channelData = new HashMap<>();
        channelData.put("isAdaptiveCard", true);
        reply.setChannelData(channelData);

        // Send the reply
        turnContext.sendActivity(reply).thenApply(resourceResponse -> null);
    }
}
