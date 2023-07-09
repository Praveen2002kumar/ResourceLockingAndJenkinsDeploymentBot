package com.mycompany.echo.AllRepositories;


import com.mycompany.echo.AllModels.ExpireLockNotificationModel;
import com.mycompany.echo.AllModels.ResourceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpireLockNotificationRepo extends MongoRepository<ExpireLockNotificationModel,String> {
    ExpireLockNotificationModel findByResource(String resource);
}
