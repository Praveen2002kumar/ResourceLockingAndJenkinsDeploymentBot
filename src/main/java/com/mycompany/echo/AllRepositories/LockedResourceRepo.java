package com.mycompany.echo.AllRepositories;

import com.mycompany.echo.AllModels.LockedResourceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface  LockedResourceRepo extends MongoRepository<LockedResourceModel,String> {
    LockedResourceModel findByResource(String resource);

}
