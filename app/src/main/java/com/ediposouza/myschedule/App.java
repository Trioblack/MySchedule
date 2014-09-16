package com.ediposouza.myschedule;

import android.app.Application;

/**
 * Created by ufc134.souza on 15/09/2014.
 */
public class App extends Application {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
