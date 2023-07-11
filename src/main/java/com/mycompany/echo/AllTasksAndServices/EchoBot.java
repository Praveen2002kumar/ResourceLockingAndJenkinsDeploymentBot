// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.mycompany.echo.AllTasksAndServices;

import com.codepoetics.protonpack.collectors.CompletableFutures;
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
    ResourceLocking resourceLocking;


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

    @Autowired
    StatusWithBuildNumber statusWithBuildNumber;

    @Autowired
    BuildJobForm buildJobForm;

    @Autowired
    ReceiveBuildParameters receiveBuildParameters;


    @Autowired
    UserSpecificBuildsStatus userSpecificBuildsStatus;

    @Autowired
    Abortjob abortjob;

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        String messageToUser = "help : all commands";
        if (turnContext.getActivity().isType(ActivityTypes.MESSAGE)&&turnContext.getActivity().getText() == null) {
            messageToUser = receiveBuildParameters.getReceive(turnContext);
            Activity reply = MessageFactory.text(messageToUser);
            return turnContext.sendActivity(reply).thenApply(resourceResponse -> null);
        }


        consumeContext.getConsume(turnContext);
        String input = turnContext.getActivity().getText();
        StringTokenizer tokenizer = new StringTokenizer(input);

        List<String> userInput = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            userInput.add(token);
        }


        try {
            if (userInput.get(0).equals("hi")) {
                messageToUser = "Hi! " + turnContext.getActivity().getFrom().getName();
            } else if (userInput.get(0).equals("all") && userInput.get(1).equals("resources")) {
                messageToUser = allResources.getAllResources();
            } else if (userInput.get(0).equals("add") && userInput.get(1).equals("resource")) {
                messageToUser = adminAddedResources.addResource(userInput.get(2), turnContext);
            } else if (userInput.get(0).equals("lock") && userInput.get(1).equals("resource")) {
                if (resourceRepository.findByResourcename(userInput.get(2)) == null)
                    messageToUser = "Resource not found";

                messageToUser = resourceLocking.LockResource(userInput.get(2), userInput.get(3), turnContext);

            } else if (userInput.get(0).equals("unlock") && userInput.get(1).equals("resource")) {

                messageToUser = resourceLocking.UnlockResource(userInput.get(2), turnContext);

            } else if (userInput.get(0).equals("locked") && userInput.get(1).equals("resources")) {
                messageToUser = allLockedResources.getLockedResources();
            } else if (userInput.get(0).equals("grant") && userInput.get(1).equals("resource")) {
                messageToUser = exchangeLock.getExchange(userInput.get(2), userInput.get(3), turnContext, userInput.get(4));
            } else if (userInput.get(0).equals("remove") && userInput.get(1).equals("resource")) {
                messageToUser = removeResource.getRemove(userInput.get(2), turnContext);
            } else if ((userInput.get(0).equals("inc") || userInput.get(0).equals("dec")) && userInput.get(1).equals("lock")) {
                messageToUser = increaseLock.getIncrease(userInput.get(0), userInput.get(2), userInput.get(3), turnContext);

            } else if (userInput.get(0).equals("all") && userInput.get(1).equals("commands")) {
                messageToUser = allCommands.getCommnds();
            } else if (userInput.get(0).equals("get") && userInput.get(1).equals("status")) {
                messageToUser = statusWithBuildNumber.getStatus(userInput.get(2), userInput.get(3), turnContext);
            } else if (userInput.get(0).equals("myjob") && userInput.get(1).equals("status")) {

            } else if (userInput.get(0).equals("build") && userInput.get(1).equals("job")) {
                buildJobForm.getForm(turnContext);
                messageToUser = "fill this form";
            }else if(userInput.get(0).equals("mybuild")&& userInput.get(1).equals("jobs")){
                messageToUser=userSpecificBuildsStatus.getStatus(turnContext);
            }else if(userInput.get(0).equals("abort")&& userInput.get(1).equals("job")){
                messageToUser=abortjob.abortJob(userInput.get(2),userInput.get(3));
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            System.out.println(e);
        } catch (Exception e) {
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
