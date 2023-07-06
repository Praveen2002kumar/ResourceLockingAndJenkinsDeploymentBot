package com.mycompany.echo.AllRepositories;

import com.mycompany.echo.AllModels.ResourceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends MongoRepository<ResourceModel,String> {
    ResourceModel findByResourcename(String name);
}
