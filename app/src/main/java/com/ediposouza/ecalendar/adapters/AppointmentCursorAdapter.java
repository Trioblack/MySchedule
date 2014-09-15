package com.ediposouza.ecalendar.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ediposouza.ecalendar.R;
import com.ediposouza.ecalendar.db.AppointmentContract;
import com.ediposouza.ecalendar.models.Appointment;
import com.ediposouza.ecalendar.models.AppointmentViewHolder;

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
    public AppointmentViewHolder newView(Context context, Cursor cursor) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_appointment, null, false);
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
        viewHolder.qcbContact.assignContactUri(appointment.getContactUri());
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
