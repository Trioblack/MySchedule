package com.ediposouza.myschedule.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;

import com.ediposouza.myschedule.db.AppointmentContract;
import com.ediposouza.myschedule.model.Appointment;
import com.ediposouza.myschedule.provider.AppointmentProvider;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ufc134.souza on 16/09/2014.
 */
public class NotificationService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        updateAppointmentsNotification();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * update the pendentIntent notifications for new Appointments
     */
    public void updateAppointmentsNotification() {
        Cursor cursor = getContentResolver().query(AppointmentProvider.CONTENT_URI,
                AppointmentContract.AppointmentEntry.PROJECTION_ALL_COLUMNS,
                null,
                null,
                null);
        if(cursor != null){
            cursor.moveToFirst();
            do{
                Appointment appointment = Appointment.parseAppointmentData(
                        cursor.getInt(cursor.getColumnIndex(AppointmentContract.AppointmentEntry._ID)),
                        getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_TITLE),
                        getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_DESC),
                        getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_DATE),
                        getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_TIME),
                        getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_CONTACT_URI)
                );
                registerPendentNotification(appointment);
            }while(cursor.moveToNext());
        }
    }

    private void registerPendentNotification(Appointment appointment) {
        Date date = appointment.getDate();
        date.setHours(appointment.getTime().getHours());
        date.setMinutes(appointment.getTime().getMinutes());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        Date now = new Date();
        if(now.getTime() > c.getTimeInMillis())
            return;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationBroadcast.class);
        intent.putExtra(NotificationBroadcast.APPOINTMENT_TO_NOTIFY, appointment);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                appointment.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
    }

    private String getCursorString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        // Return this instance of LocalService so clients can call public methods
        public NotificationService getService() {
            return NotificationService.this;
        }

    }

}
