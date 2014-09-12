package com.ediposouza.ecalendar.models;

import android.net.Uri;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class Appointment implements Serializable{

    private Date date;
    private Time time;
    private String title;
    private String desc;
    private Uri contactUri;

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

    public Uri getContactUri() {
        return contactUri;
    }

    public void setContactUri(Uri contactUri) {
        this.contactUri = contactUri;
    }
}
