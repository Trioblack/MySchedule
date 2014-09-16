package com.ediposouza.myschedule.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ediposouza.myschedule.R;
import com.ediposouza.myschedule.db.AppointmentContract;
import com.ediposouza.myschedule.model.Appointment;
import com.ediposouza.myschedule.model.AppointmentViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

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
        Appointment appointment = Appointment.parseAppointmentData(
                cursor.getInt(cursor.getColumnIndex(AppointmentContract.AppointmentEntry._ID)),
                getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_TITLE),
                getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_DESC),
                getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_DATE),
                getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_TIME),
                getCursorString(cursor, AppointmentContract.AppointmentEntry.COLUMN_CONTACT_URI)
        );
        viewHolder.ID = appointment.getId();
        viewHolder.tvTitle.setText(appointment.getTitle());
        viewHolder.tvDesc.setText(appointment.getDesc());
        viewHolder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDate()));
        viewHolder.tvTime.setText(new SimpleDateFormat("hh:mm").format(appointment.getTime()));
        //load default image
        Picasso.with(context).load(R.drawable.ic_contact_picture_holo_light).into(viewHolder.ivContact);
        //load contact image
        Cursor c = context.getContentResolver().query(
                appointment.getContactUri(),
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
            Picasso.with(context).load(contactPhotoUri).into(viewHolder.ivContact);
            viewHolder.ivContact.setTag(appointment.getContactUri().toString());
        }
    }

    private String getCursorString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

}
