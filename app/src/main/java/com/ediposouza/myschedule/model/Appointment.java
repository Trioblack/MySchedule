package com.ediposouza.myschedule.model;

import android.net.Uri;
import android.util.Log;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class Appointment implements Serializable{

    private int id;
    private String title;
    private String desc;
    private Date date;
    private Time time;
    private String contactUri;

    public Appointment(){
        this.id = 0;
    }

    public Appointment(int id, String title, String desc, Date date, Time time, Uri contactUri) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.time = time;
        if(contactUri == null)
            this.contactUri = "";
        else
            this.contactUri = contactUri.toString();
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
        if(contactUri == null)
            contactUri = "";
        return Uri.parse(contactUri);
    }

    public void setContactUri(Uri contactUri) {
        if(contactUri == null)
            this.contactUri = "";
        else
            this.contactUri = contactUri.toString();
    }

    public static Appointment parseAppointmentData(int id, String title, String desc,
                                                   String date, String time, String contactUri) {
        if (contactUri == null)
            contactUri = "";
        return new Appointment(id, title, desc,
                parseDate(date), parseTime(time), Uri.parse(contactUri));
    }

    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }catch (Exception e){
            Log.e(Appointment.class.getName(), e.getMessage());
        }
        return null;
    }

    private static Time parseTime(String date) {
        try {
            Date d = new SimpleDateFormat("hh:mm").parse(date);
            return new Time(d.getTime());
        }catch (Exception e){
            Log.e(Appointment.class.getName(), e.getMessage());
        }
        return null;
    }

}
