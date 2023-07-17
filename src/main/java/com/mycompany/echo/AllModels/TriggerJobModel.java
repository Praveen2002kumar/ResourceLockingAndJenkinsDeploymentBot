package com.mycompany.echo.AllModels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document(collection = "TriggerJobs")
@Component
public class TriggerJobModel {
    @Id
    private String id;
    private String url;
    private String email;
    private String jobname;
    private String buildnumber;

    public String getJobname() {
        return jobname;
    }

    public String getBuildnumber() {
        return buildnumber;
    }


    public void setBuildnumber(String buildnumber) {
        this.buildnumber = buildnumber;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
