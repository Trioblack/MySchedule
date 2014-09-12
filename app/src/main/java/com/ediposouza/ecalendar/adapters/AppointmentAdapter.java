package com.ediposouza.ecalendar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ediposouza.ecalendar.R;
import com.ediposouza.ecalendar.models.Appointment;
import com.ediposouza.ecalendar.models.AppointmentViewHolder;

import java.util.ArrayList;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class AppointmentAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Appointment> appointments;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_appointment, viewGroup, false);
        AppointmentViewHolder viewHolder = new AppointmentViewHolder(context, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Appointment appointment = appointments.get(position);
        AppointmentViewHolder appointmentViewHolder = (AppointmentViewHolder) viewHolder;
        appointmentViewHolder.fillWith(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
