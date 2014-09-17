package com.ediposouza.myschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ediposouza.myschedule.db.AppointmentContract;
import com.ediposouza.myschedule.model.Appointment;
import com.ediposouza.myschedule.provider.AppointmentProvider;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewAppointmentActivity extends Activity {

    private static final int PICK_CONTACT_CODE = 1;
    public static final String EDIT_APPOINTMENT = "edit_appointment";

    private int editingID;
    private EditText etName;
    private EditText etDesc;
    private EditText etTime;
    private DatePicker dpDate;
    private ImageView ivWith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        etName = (EditText) findViewById(R.id.etAppoitmentName);
        etDesc = (EditText) findViewById(R.id.etAppoitmentDesc);
        etTime = (EditText) findViewById(R.id.etAppointmentTime);
        dpDate = (DatePicker) findViewById(R.id.dpAppointmentDate);
        ivWith = (ImageView) findViewById(R.id.ivWith);
        configComponents();
        editingID = 0;
        //Import file with apt extension
        importData(getIntent().getData());
        //load Appointment to edit
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(EDIT_APPOINTMENT)) {
            Appointment appointment = (Appointment) extras.getSerializable(EDIT_APPOINTMENT);
            editingID = appointment.getId();
            fillFields(appointment);
        }
    }

    private void configComponents() {
        Date now = new Date(System.currentTimeMillis());
        etTime.setText(new SimpleDateFormat("hh:mm").format(now));
        etTime.setFocusable(false);
        etTime.setFocusableInTouchMode(false);
        etTime.setClickable(true);
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(NewAppointmentActivity.this);
                final TimePicker timePicker = new TimePicker(NewAppointmentActivity.this);
                b.setView(timePicker)
                        .setTitle(getString(R.string.new_appointment_time))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar c = Calendar.getInstance();
                                c.set(2000, Calendar.JANUARY, 1, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                                Date date = new Date();
                                etTime.setText(new SimpleDateFormat("HH:mm").format(c.getTime()));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }
        });
        ivWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, PICK_CONTACT_CODE);
            }
        });
    }

    private void importData(Uri data) {
        if(data == null)
            return;
        String scheme = data.getScheme();

        if(ContentResolver.SCHEME_FILE.equals(scheme)) {
            try {
                InputStream is = getContentResolver().openInputStream(data);
                if (is == null)
                    return;
                InputStreamReader streamReader = new InputStreamReader(is);
                Appointment appointment = new Gson().fromJson(streamReader, Appointment.class);
                fillFields(appointment);
                is.close();
            } catch (Exception e) {
                String readErrorMsg = getString(R.string.new_appointment_read_error);
                Toast.makeText(this, readErrorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fillFields(Appointment appointment) {
        etName.setText(appointment.getTitle());
        etDesc.setText(appointment.getDesc());
        etTime.setText(new SimpleDateFormat("HH:mm").format(appointment.getTime()));
        dpDate.getCalendarView().setDate(appointment.getDate().getTime());
        loadContactImage(appointment.getContactUri());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveUpdateNewAppointment();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_CONTACT_CODE)
            if(data != null){
                Uri contactUri = data.getData();
                //showImage
                loadContactImage(contactUri);
            }
    }

    private void loadContactImage(Uri contactUri) {
        ivWith.setTag(contactUri.toString());
        Cursor c = getContentResolver().query(
                contactUri,
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
            Picasso.with(this).load(contactPhotoUri).into(ivWith);
        }
    }

    private void saveUpdateNewAppointment() {
        ContentValues values = new ContentValues();
        values.put(AppointmentContract.AppointmentEntry.COLUMN_TITLE, etName.getText().toString());
        values.put(AppointmentContract.AppointmentEntry.COLUMN_DESC, etDesc.getText().toString());
        values.put(AppointmentContract.AppointmentEntry.COLUMN_DATE, getFormattedDate());
        values.put(AppointmentContract.AppointmentEntry.COLUMN_TIME, etTime.getText().toString());
        values.put(AppointmentContract.AppointmentEntry.COLUMN_CONTACT_URI, getContactUri());
        if(editingID > 0) {
            Uri editingUri = ContentUris.withAppendedId(AppointmentProvider.CONTENT_URI, editingID);
            getContentResolver().update(editingUri, values, null, null);
            Toast.makeText(this, getString(R.string.new_appointment_save_success), Toast.LENGTH_SHORT).show();
        }
        else {
            Uri resUri = getContentResolver().insert(AppointmentProvider.CONTENT_URI, values);
            String msg = (resUri == null) ? getString(R.string.new_appointment_save_error)
                    : getString(R.string.new_appointment_save_success);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        int month = Calendar.JANUARY;
        switch (dpDate.getMonth()){
            case 1: month = Calendar.FEBRUARY; break;
            case 2: month = Calendar.MARCH; break;
            case 3: month = Calendar.APRIL; break;
            case 4: month = Calendar.MAY; break;
            case 5: month = Calendar.JUNE; break;
            case 6: month = Calendar.JULY; break;
            case 7: month = Calendar.AUGUST; break;
            case 8: month = Calendar.SEPTEMBER; break;
            case 9: month = Calendar.OCTOBER; break;
            case 10: month = Calendar.NOVEMBER; break;
            case 11: month = Calendar.DECEMBER; break;
        }
        c.set(dpDate.getYear(), month, dpDate.getDayOfMonth());
        return new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
    }

    public String getContactUri() {
        String uri = (String) ivWith.getTag();
        if(uri == null) {
            uri = "";
            ivWith.setTag("");
        }
        return uri;
    }
}
