package com.ediposouza.schedule.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediposouza.schedule.R;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

    private Context context;
    public TextView tvTitle;
    public TextView tvDesc;
    public TextView tvDate;
    public TextView tvTime;
    public ImageView ivContact;

    public AppointmentViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        ivContact = (ImageView) itemView.findViewById(R.id.ivWith);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context, "Long click " + tvTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

}
