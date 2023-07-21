package com.mycompany.echo.AllTasksAndServices;


import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.mycompany.echo.AllModels.JenkinsTokenModel;
import com.mycompany.echo.AllRepositories.JenkinsTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddToken {

    @Autowired
    JenkinsTokenRepo jenkinsTokenRepo;

    @Autowired
    JenkinsTokenModel jenkinsTokenModel;
    public static String encrypt(String plaintext, int key) {
        StringBuilder encryptedText = new StringBuilder();
        for (char ch : plaintext.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                char encryptedChar = (char) (((ch - base + key) % 26) + base);
                encryptedText.append(encryptedChar);
            } else {
                encryptedText.append(ch);
            }
        }
        return encryptedText.toString();
    }

    /**
     * this is used to add token for jenkins users
     * @param turnContext turncontext of user
     * @param tokenvalue token value of user
     * @return this will return a string if token added successfully or not
     */
    public String add(TurnContext turnContext,String tokenvalue){
        ChannelAccount sentBy = turnContext.getActivity().getFrom();
        TeamsChannelAccount teamsAcc = TeamsInfo.getMember(turnContext, sentBy.getId()).join();
        String userEmail = teamsAcc.getEmail();
        if(jenkinsTokenRepo.findByEmail(userEmail)!=null)jenkinsTokenRepo.deleteById(jenkinsTokenRepo.findByEmail(userEmail).getId());
        jenkinsTokenModel.setEmail(userEmail);
        jenkinsTokenModel.setToken(encrypt(tokenvalue,3));
        jenkinsTokenRepo.save(jenkinsTokenModel);
        return "Successfully added token";
    }
}
