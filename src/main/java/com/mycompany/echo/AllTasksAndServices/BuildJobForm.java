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
public class BuildJobForm {
    /**
    *Build job form to access job parameters using form
     */
    private String getAdaptiveCardJson() {
        String adaptiveCardJson = "{\n" +
                "  \"type\": \"AdaptiveCard\",\n" +
                "  \"body\": [\n" +
                "    {\n" +
                "      \"type\": \"TextBlock\",\n" +
                "      \"text\": \"Build Jenkins Job\",\n" +
                "      \"weight\": \"Bolder\",\n" +
                "      \"size\": \"Medium\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"Input.Text\",\n" +
                "      \"id\": \"job_name\",\n" +
                "      \"placeholder\": \"JOB_NAME\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"Input.Text\",\n" +
                "      \"id\": \"chart_name\",\n" +
                "      \"placeholder\": \"CHART_NAME\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"Input.Text\",\n" +
                "      \"id\": \"chart_release_name\",\n" +
                "      \"placeholder\": \"CHART_RELEASE_NAME\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"Input.Text\",\n" +
                "      \"id\": \"branch\",\n" +
                "      \"placeholder\": \"CHART_REPO_BRANCH\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"Input.Text\",\n" +
                "      \"id\": \"mode\",\n" +
                "      \"placeholder\": \"JOB_MODE\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"actions\": [\n" +
                "    {\n" +
                "      \"type\": \"Action.Submit\",\n" +
                "      \"title\": \"Build\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"$schema\": \"http://adaptivecards.io/schemas/adaptive-card.json\",\n" +
                "  \"version\": \"1.5\"\n" +
                "}";

        return adaptiveCardJson;

    }

    public void getForm(TurnContext turnContext) {
        Attachment cardAttachment = new Attachment();
        cardAttachment.setContentType("application/vnd.microsoft.card.adaptive");
        try {
            cardAttachment.setContent(new ObjectMapper().readTree(getAdaptiveCardJson()));
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
