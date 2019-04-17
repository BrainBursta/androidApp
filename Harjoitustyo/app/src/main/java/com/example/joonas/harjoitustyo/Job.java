package com.example.joonas.harjoitustyo;

/**
 * Created by Joonas on 07/02/2016.
 */
public class Job {
    protected String status, desc, deadline, suoritteet, selite;
    protected String user_id, job_id, tunnit;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getSuoritteet() {
        return suoritteet;
    }

    public void setSuoritteet(String suoritteet) {
        this.suoritteet = suoritteet;
    }

    public String getSelite() {
        return selite;
    }

    public void setSelite(String selite) {
        this.selite = selite;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getTunnit() {
        return tunnit;
    }

    public void setTunnit(String tunnit) {
        this.tunnit = tunnit;
    }
}
