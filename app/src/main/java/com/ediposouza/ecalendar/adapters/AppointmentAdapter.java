package com.ediposouza.ecalendar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ediposouza.ecalendar.R;
import com.ediposouza.ecalendar.models.Appointment;
import com.ediposouza.ecalendar.models.AppointmentViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentViewHolder> {

    private Context context;
    private ArrayList<Appointment> appointments;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_appointment, viewGroup, false);
        AppointmentViewHolder viewHolder = new AppointmentViewHolder(context, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder viewHolder, int position) {
        Appointment appointment = appointments.get(position);
        viewHolder.tvTitle.setText(appointment.getTitle());
        viewHolder.tvDesc.setText(appointment.getDesc());
        viewHolder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(appointment.getDate()));
        viewHolder.tvTime.setText(new SimpleDateFormat("hh:mm").format(appointment.getTime()));
        viewHolder.qcbContact.assignContactUri(appointment.getContactUri());
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
