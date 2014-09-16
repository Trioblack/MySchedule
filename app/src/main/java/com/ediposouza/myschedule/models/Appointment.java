package com.ediposouza.myschedule.models;

import android.net.Uri;

import java.sql.Time;
import java.util.Date;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class Appointment {

    private String title;
    private String desc;
    private Date date;
    private Time time;
    private Uri contactUri;

    public Appointment(){

    }

    public Appointment(String title, String desc, Date date, Time time, Uri contactUri) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.contactUri = contactUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Uri getContactUri() {
        return contactUri;
    }

    public void setContactUri(Uri contactUri) {
        this.contactUri = contactUri;
    }
}
