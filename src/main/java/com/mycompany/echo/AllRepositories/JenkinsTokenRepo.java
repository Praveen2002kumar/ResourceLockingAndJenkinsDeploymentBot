package com.mycompany.echo.AllRepositories;

import com.mycompany.echo.AllModels.JenkinsTokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JenkinsTokenRepo extends MongoRepository<JenkinsTokenModel,String> {
    JenkinsTokenModel findByEmail(String email);
}
