package com.mycompany.echo.AllModels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Document(collection = "Userbuildjob")
@Component
public class UserBuildJobModel {
    @Id
    private String id;
    private String jobname;
    private LocalDateTime triggertime;
    private String url;
    private String email;

    public LocalDateTime getTriggertime() {
        return triggertime;
    }

    public void setTriggertime(LocalDateTime triggertime) {
        this.triggertime = triggertime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
