package com.mycompany.echo.AllModels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Document(collection = "QueueJobs")
@Component
public class QueueJobModel {
    @Id
    private String id;
    private String queuid;
    private String jobname;
    private LocalDateTime triggertime=LocalDateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueuid() {
        return queuid;
    }

    public void setQueuid(String queuid) {
        this.queuid = queuid;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public LocalDateTime getTriggertime() {
        return triggertime;
    }

    public void setTriggertime(LocalDateTime triggertime) {
        this.triggertime = triggertime;
    }
}
