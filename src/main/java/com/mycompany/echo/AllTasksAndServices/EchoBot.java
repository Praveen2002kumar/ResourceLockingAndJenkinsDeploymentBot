// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.mycompany.echo.AllTasksAndServices;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;

import com.microsoft.bot.schema.*;

import com.mycompany.echo.AllRepositories.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * This class implements the functionality of the Bot.
 *
 * <p>
 * This is where application specific logic for interacting with the users would be added. For this
 * sample, the {@link #onMessageActivity(TurnContext)} echos the text back to the user. The {@link
 * #onMembersAdded(List, TurnContext)} will send a greeting to new conversation participants.
 * </p>
 */
public class EchoBot extends ActivityHandler {



    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    AllResources allResources;

    @Autowired
    JenkinJobBuild jenkinJobBuild;

    @Autowired
    ResourceLocking resourceLocking;

    @Autowired
    JenkinJobInfoService jenkinJobInfoService;


    @Autowired
    AllLockedResources allLockedResources;

    @Autowired
    ExchangeLock exchangeLock;

    @Autowired
    AllCommands allCommands;

    @Autowired
    AdminAddedResources adminAddedResources;

    @Autowired
    IncreaseLock increaseLock;

    @Autowired
    RemoveResource removeResource;

    @Autowired
    ConsumeContext consumeContext;


    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {

        consumeContext.getConsume(turnContext);


        String input = turnContext.getActivity().getText();
        StringTokenizer tokenizer = new StringTokenizer(input);

        List<String> list = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(token);
        }


        String messageToUser = "command not found";

        try {
            if (list.get(0).equals("hi")) {
                messageToUser = "Hi! " + turnContext.getActivity().getFrom().getName();
            } else if (list.get(1).equals("status")) {
                messageToUser = jenkinJobInfoService.getJobInfo(list.get(0));

            } else if (list.get(0).equals("all") && list.get(1).equals("resources")) {
                messageToUser = allResources.getAllResources();
            } else if (list.get(1).equals("build")) {
                messageToUser = jenkinJobBuild.triggerJob(list, turnContext.getActivity().getFrom().getId());

            } else if (list.get(0).equals("add") && list.get(1).equals("resource")) {
                messageToUser = adminAddedResources.addResource(list.get(2), turnContext);
            } else if (list.get(0).equals("lock") && list.get(1).equals("resource")) {
                if (resourceRepository.findByResourcename(list.get(2)) == null) messageToUser = "Resource not found";

                messageToUser = resourceLocking.LockResource(list.get(2), list.get(3), turnContext);

            } else if (list.get(0).equals("unlock") && list.get(1).equals("resource")) {

                messageToUser = resourceLocking.UnlockResource(list.get(2), turnContext);

            } else if (list.get(0).equals("locked") && list.get(1).equals("resources")) {
                messageToUser = allLockedResources.getLockedResources();
            } else if (list.get(0).equals("grant") && list.get(1).equals("resource")) {
                messageToUser = exchangeLock.getExchange(list.get(2), list.get(3), turnContext, list.get(4));
            } else if (list.get(0).equals("remove") && list.get(1).equals("resource")) {
                messageToUser = removeResource.getRemove(list.get(2), turnContext);
            } else if ((list.get(0).equals("inc") || list.get(0).equals("dec")) && list.get(1).equals("lock")) {
                messageToUser = increaseLock.getIncrease(list.get(0), list.get(2), list.get(3), turnContext);

            } else if (list.get(0).equals("all") && list.get(1).equals("commands")) {
                messageToUser = allCommands.getCommnds();
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            System.out.println(e);
        } catch (JsonProcessingException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        return turnContext.sendActivity(
                MessageFactory.text(messageToUser)
        ).thenApply(sendResult -> null);
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(
            List<ChannelAccount> membersAdded,
            TurnContext turnContext
    ) {

        return membersAdded.stream()
                .filter(
                        member -> !StringUtils
                                .equals(member.getId(), turnContext.getActivity().getRecipient().getId())
                ).map(channel -> turnContext.sendActivity(MessageFactory.text("Hello and welcome!")))
                .collect(CompletableFutures.toFutureList()).thenApply(resourceResponses -> null);
    }

}
