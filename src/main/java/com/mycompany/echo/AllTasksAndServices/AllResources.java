package com.mycompany.echo.AllTasksAndServices;

import com.mycompany.echo.AllModels.ResourceModel;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllResources {
    @Autowired
    ResourceRepository resourceRepository;

    /**
     * to get all resources added by admin
     * @return return a string of all resoruces
     */
    public String getAllResources() {
        StringBuilder messageToUser = new StringBuilder("**All Available resources**" + "\n\n");
        List<ResourceModel> allresource = resourceRepository.findAll();
        for (ResourceModel resourceModel : allresource) {
            messageToUser.append(resourceModel.getResourcename());
            messageToUser.append("\n\n");
        }
        return messageToUser.toString();
    }
}
