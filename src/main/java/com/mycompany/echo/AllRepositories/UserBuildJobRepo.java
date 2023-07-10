package com.mycompany.echo.AllRepositories;

import com.mycompany.echo.AllModels.UserBuildJobModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBuildJobRepo extends MongoRepository<UserBuildJobModel,String> {
    List<UserBuildJobModel> findByEmail(String email);
}
