package com.mycompany.echo.AllTasksAndServices;

import com.mycompany.echo.AllRepositories.LockedResourceRepo;
import com.mycompany.echo.AllRepositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceAvailibility {

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    LockedResourceRepo lockedResourceRepo;

    @Autowired
    ConvertUTCToIST convertUTCToIST;

    /**
     * to check availibility of a resource
     * @param resource resource name
     * @return return string
     */
    public String getCheck(String resource){
        if(resourceRepository.findByResourcename(resource)==null)return "Resource not found";
        if(lockedResourceRepo.findByResource(resource)!=null)return "Resource is locked by "+lockedResourceRepo.findByResource(resource).getUseremail()+" :Till "+convertUTCToIST.getIST(lockedResourceRepo.findByResource(resource).getExpiretime());
         return "Resource is Available";
    }
}
