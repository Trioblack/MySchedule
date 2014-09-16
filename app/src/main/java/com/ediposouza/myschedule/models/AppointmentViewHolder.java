package com.ediposouza.myschedule.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediposouza.myschedule.R;

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

    public AppointmentViewHolder(final Context context, View itemView) {
        super(itemView);
        this.context = context;
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        ivContact = (ImageView) itemView.findViewById(R.id.ivWith);
        ivContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriString = (String) ivContact.getTag();
                if(uriString != null) {
                    Uri contactUri = Uri.parse(uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, contactUri);
                    if (intent.resolveActivity(context.getPackageManager()) != null)
                        context.startActivity(intent);
                }else {
                    String errorMsg = context.getString(R.string.view_holder_error_no_contact);
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context, "Long click " + tvTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

}
