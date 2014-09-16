package com.ediposouza.myschedule.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ufc134.souza on 15/09/2014.
 */
public class AppointmentDbHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "Appointment.db";
    private static int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AppointmentContract.AppointmentEntry.TABLE_NAME + " (" +
            AppointmentContract.AppointmentEntry._ID + " INTEGER PRIMARY KEY, " +
            AppointmentContract.AppointmentEntry.COLUMN_TITLE + " TEXT, " +
            AppointmentContract.AppointmentEntry.COLUMN_DESC + " TEXT, " +
            AppointmentContract.AppointmentEntry.COLUMN_DATE + " TEXT, " +
            AppointmentContract.AppointmentEntry.COLUMN_TIME + " TEXT, " +
            AppointmentContract.AppointmentEntry.COLUMN_CONTACT_URI + " TEXT" +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AppointmentContract.AppointmentEntry.TABLE_NAME;

    public AppointmentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
    }
}
