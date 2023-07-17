package com.mycompany.echo.AllRepositories;

import com.mycompany.echo.AllModels.TriggerJobModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriggerJobRepo extends MongoRepository<TriggerJobModel,String> {
}
