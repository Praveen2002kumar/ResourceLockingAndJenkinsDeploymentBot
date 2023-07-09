package com.mycompany.echo.AllRepositories;

import com.mycompany.echo.AllModels.QueueJobModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueJobRepo extends MongoRepository<QueueJobModel,String> {
}
