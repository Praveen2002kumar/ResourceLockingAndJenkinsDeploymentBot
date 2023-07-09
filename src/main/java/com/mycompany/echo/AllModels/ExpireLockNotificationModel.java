package com.mycompany.echo.AllModels;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Document(collection = "ExpireNotificationRepo")
@Component
public class ExpireLockNotificationModel {
    @Id
    private String id;
    private String resource;
    private LocalDateTime expiretime;

    private String useremail;



    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public LocalDateTime getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(LocalDateTime expiretime) {
        this.expiretime = expiretime;
    }

}
