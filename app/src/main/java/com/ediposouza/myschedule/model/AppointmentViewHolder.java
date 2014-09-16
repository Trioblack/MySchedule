package com.ediposouza.myschedule.model;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediposouza.myschedule.HomeActivity;
import com.ediposouza.myschedule.NewAppointmentActivity;
import com.ediposouza.myschedule.R;
import com.ediposouza.myschedule.provider.AppointmentProvider;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ufc134.souza on 12/09/2014.
 */
public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

    private Context context;

    public int ID;
    public TextView tvTitle;
    public TextView tvDesc;
    public TextView tvDate;
    public TextView tvTime;
    public ImageView ivContact;

    private ArrayAdapter<String> longClickAdapter;

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
        longClickAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, new ArrayList<String>());
        longClickAdapter.add(context.getString(R.string.view_holder_option_edit));
        longClickAdapter.add(context.getString(R.string.view_holder_option_delete));
        longClickAdapter.add(context.getString(R.string.view_holder_option_export));
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(tvTitle.getText().toString())
                .setAdapter(longClickAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Appointment appointment = Appointment.parseAppointmentData(ID,
                                        tvTitle.getText().toString(),
                                        tvDesc.getText().toString(),
                                        tvDate.getText().toString(),
                                        tvTime.getText().toString(),
                                        (String) ivContact.getTag());
                                Intent intent = new Intent(context, NewAppointmentActivity.class);
                                intent.putExtra(NewAppointmentActivity.EDIT_APPOINTMENT, appointment);
                                context.startActivity(intent);
                                break;
                            case 1:
                                deleteAppointment();
                                break;
                            case 2:
                                exportAppointment();
                                break;
                        }
                    }

                })
                .setCancelable(true)
                .create()
                .show();
        return true;
    }

    private void deleteAppointment() {
        Uri deleteUri = ContentUris.withAppendedId(AppointmentProvider.CONTENT_URI, ID);
        int res = context.getContentResolver().delete(
                deleteUri,
                null,
                null
        );
        String deleteMsg = (res > 0) ? context.getString(R.string.new_appointment_delete_success)
                : context.getString(R.string.new_appointment_delete_error);
        HomeActivity.reloadList();
        Toast.makeText(context, deleteMsg, Toast.LENGTH_SHORT).show();
    }

    private void exportAppointment() {
        Appointment appointment = Appointment.parseAppointmentData(ID,
                tvTitle.getText().toString(),
                tvDesc.getText().toString(),
                tvDate.getText().toString(),
                tvTime.getText().toString(),
                (String) ivContact.getTag());
        File myScheduleDir = new File(Environment.getExternalStorageDirectory(), "My Schedule");
        if(!myScheduleDir.exists())
            myScheduleDir.mkdir();
        File exportedFile = new File(myScheduleDir, appointment.getTitle().trim() + ".apt");
        String exportMsg = context.getString(R.string.view_holder_success_export);
        try {
            FileWriter fileWriter = new FileWriter(exportedFile);
            new Gson().toJson(appointment, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            exportMsg = context.getString(R.string.view_holder_error_export) + " " + e.getMessage();
        }
        Toast.makeText(context, exportMsg, Toast.LENGTH_SHORT).show();
    }

}
