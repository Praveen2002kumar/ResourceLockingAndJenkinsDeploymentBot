package com.mycompany.echo.AllRepositories;

import com.mycompany.echo.AllModels.ResourceAdminModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceAdminRepository extends MongoRepository<ResourceAdminModel,String> {
    boolean existsByEmail(String email);
}
