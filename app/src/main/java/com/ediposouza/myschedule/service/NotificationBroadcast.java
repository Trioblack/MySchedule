package com.ediposouza.myschedule.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.ediposouza.myschedule.HomeActivity;
import com.ediposouza.myschedule.NewAppointmentActivity;
import com.ediposouza.myschedule.R;
import com.ediposouza.myschedule.model.Appointment;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by ufc134.souza on 16/09/2014.
 */
public class NotificationBroadcast extends BroadcastReceiver {

    public static final String APPOINTMENT_TO_NOTIFY = "appointmentToNotify";

    @Override
    public void onReceive(Context context, Intent intent) {
        // wake the device
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Schedule");
        wl.acquire();
        // show Notification
        if(intent.hasExtra(APPOINTMENT_TO_NOTIFY)){
            Appointment appointment = (Appointment) intent.getExtras().
                    getSerializable(APPOINTMENT_TO_NOTIFY);
            createPendentNotification(appointment, context);
        }
        // Release the lock
        wl.release();
    }

    private void createPendentNotification(final Appointment appointment, final Context context) {
        final NotificationCompat.BigTextStyle bigTextStyle =
                new NotificationCompat.BigTextStyle();
        bigTextStyle.setSummaryText(appointment.getTitle());
        bigTextStyle.bigText(appointment.getDesc());

        new Thread(new Runnable() {
            @Override
            public void run() {
                NotificationCompat.Builder b = new NotificationCompat.Builder(context);
                Notification notification = b.setContentTitle(appointment.getTitle())
                        .setContentText(appointment.getDesc())
                        .setContentIntent(getPendentIntent(appointment, context))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(getContactImage(appointment.getContactUri(), context))
                        .setStyle(bigTextStyle)
                        .setVibrate(new long[]{200, 800, 200, 800})
                        .build();

                NotificationManager notificationMgr = (NotificationManager) context.
                        getSystemService(context.NOTIFICATION_SERVICE);
                notificationMgr.notify(appointment.getId(), notification);
            }
        }).start();
    }

    private PendingIntent getPendentIntent(Appointment appointment, Context context) {
        Intent resultIntent = new Intent(context, NewAppointmentActivity.class);
        resultIntent.putExtra(NewAppointmentActivity.NOTIFICATION_ID, appointment.getId());
        resultIntent.putExtra(NewAppointmentActivity.EDIT_APPOINTMENT, appointment);
        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(HomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

    private Bitmap getContactImage(Uri contactUri, Context context) {
        Bitmap contactImage = null;
        Cursor c = context.getContentResolver().query(
                contactUri,
                new String[] {ContactsContract.Contacts.PHOTO_THUMBNAIL_URI},
                null,
                null,
                null);
        if(c != null) {
            c.moveToFirst();
            String uriString = c.getString(0);
            if(uriString == null)
                uriString = "";
            Uri contactPhotoUri = Uri.parse(uriString);
            try {
                contactImage = Picasso.with(context).load(contactPhotoUri).get();
            } catch (IOException e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
        if(contactImage == null) {
            contactImage = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_contact_picture_holo_light);
        }
        return contactImage;
    }

}
