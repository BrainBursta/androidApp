package com.example.joonas.harjoitustyo;

/**
 * Created by Joonas on 30/01/2016.
 */
public class JobDataProvider { //Tätä luokkaa käytetään jos Työt haetaan asset/Jobs.xml muute tämä on turha
    private String jobId, desc, deadline, status;

    public JobDataProvider(String jobId, String desc, String deadline, String status) {
        this.jobId = jobId;
        this.desc = desc;
        this.deadline = deadline;
        this.status = status;
    }

    public String getjobId() {
        return jobId;
    }

    public String getDesc() {
        return desc;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
