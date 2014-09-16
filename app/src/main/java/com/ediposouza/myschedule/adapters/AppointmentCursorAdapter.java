package com.ediposouza.myschedule.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ediposouza.myschedule.R;
import com.ediposouza.myschedule.db.AppointmentContract;
import com.ediposouza.myschedule.models.Appointment;
import com.ediposouza.myschedule.models.AppointmentViewHolder;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class AppointmentCursorAdapter extends RecyclerViewCursorAdapter<AppointmentViewHolder> {

    public AppointmentCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public AppointmentViewHolder newView(Context context, ViewGroup parent, Cursor cursor) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_appointment, parent, false);
        AppointmentViewHolder viewHolder = new AppointmentViewHolder(context, v);
        return viewHolder;
    }

    @Override
    public void bindView(AppointmentViewHolder viewHolder, Context context, Cursor cursor) {
        Appointment appointment = new Appointment(
                getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_TITLE),
                getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_DESC),
                parseDate(getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_DATE)),
                parseTime(getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_TIME)),
                Uri.parse(getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_CONTACT_URI))
        );
        viewHolder.tvTitle.setText(appointment.getTitle());
        viewHolder.tvDesc.setText(appointment.getDesc());
        viewHolder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDate()));
        viewHolder.tvTime.setText(new SimpleDateFormat("hh:mm").format(appointment.getTime()));
        Cursor c = context.getContentResolver().query(
                appointment.getContactUri(),
                new String[] {ContactsContract.Contacts.PHOTO_THUMBNAIL_URI},
                null,
                null,
                null);
        if(c != null) {
            c.moveToFirst();
            Uri contactPhotoUri = Uri.parse(c.getString(0));
            Picasso.with(context).load(contactPhotoUri).into(viewHolder.ivContact);
            viewHolder.ivContact.setTag(appointment.getContactUri().toString());
        }
    }

    private String getCursorString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }catch (Exception e){
            Log.e(getClass().getName(), e.getMessage());
        }
        return null;
    }

    private Time parseTime(String date) {
        try {
            Date d = new SimpleDateFormat("hh:mm").parse(date);
            return new Time(d.getTime());
        }catch (Exception e){
            Log.e(getClass().getName(), e.getMessage());
        }
        return null;
    }

}
