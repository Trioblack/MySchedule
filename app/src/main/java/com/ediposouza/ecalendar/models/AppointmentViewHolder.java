package com.ediposouza.ecalendar.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.ediposouza.ecalendar.R;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

    private Context context;
    public TextView tvTitle;
    public TextView tvDesc;
    public TextView tvDate;
    public TextView tvTime;
    public QuickContactBadge qcbContact;

    public AppointmentViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        tvTitle = (TextView) itemView.findViewById(R.id.appoint_title);
        tvDesc = (TextView) itemView.findViewById(R.id.appoint_desc);
        tvDate = (TextView) itemView.findViewById(R.id.appoint_date);
        tvTime = (TextView) itemView.findViewById(R.id.appoint_time);
        qcbContact = (QuickContactBadge) itemView.findViewById(R.id.appoint_contact);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context, "Long click " + tvTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

}
