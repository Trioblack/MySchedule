package com.ediposouza.ecalendar.models;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.ediposouza.ecalendar.R;

import java.text.SimpleDateFormat;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

    private Context context;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvDate;
    private TextView tvTime;
    private QuickContactBadge qcbContact;

    public AppointmentViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        tvTitle = (TextView) itemView.findViewById(R.id.appoint_title);
        tvDesc = (TextView) itemView.findViewById(R.id.appoint_desc);
        tvDate = (TextView) itemView.findViewById(R.id.appoint_date);
        tvTime = (TextView) itemView.findViewById(R.id.appoint_time);
        qcbContact = (QuickContactBadge) itemView.findViewById(R.id.appoint_contact);
        qcbContact.setMode(ContactsContract.QuickContact.MODE_LARGE);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context, "Long click " + tvTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

    public void fillWith(Appointment appointment){
        tvTitle.setText(appointment.getTitle());
        tvDesc.setText(appointment.getDesc());
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDate()));
        tvTime.setText(new SimpleDateFormat("hh:mm").format(appointment.getTime()));
        qcbContact.assignContactUri(appointment.getContactUri());
    }

}
