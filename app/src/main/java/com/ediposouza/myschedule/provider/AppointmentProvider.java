package com.ediposouza.myschedule.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.ediposouza.myschedule.db.AppointmentContract;
import com.ediposouza.myschedule.db.AppointmentDbHelper;

/**
 * Created by ufc134.souza on 16/09/2014.
 */
public class AppointmentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.ediposouza.myschedule.appointmentProvider";
    private static final String BASE_PATH = "appointments";

    // used for the UriMacher
    private static final int APPOINTMENTS = 1;
    private static final int APPOINTMENT_ID = 2;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/" + AppointmentContract.AppointmentEntry.TABLE_NAME;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, APPOINTMENTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", APPOINTMENT_ID);
    }

    private AppointmentDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new AppointmentDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(AppointmentContract.AppointmentEntry.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case APPOINTMENTS:
                break;
            case APPOINTMENT_ID:
                String id = uri.getLastPathSegment();
                queryBuilder.appendWhere(AppointmentContract.AppointmentEntry._ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        db = dbHelper.getReadableDatabase();
        Cursor c = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case APPOINTMENTS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(
                        AppointmentContract.AppointmentEntry.TABLE_NAME,
                        null,
                        values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        db = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case APPOINTMENTS:
                rowsDeleted = db.delete(
                        AppointmentContract.AppointmentEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case APPOINTMENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(
                            AppointmentContract.AppointmentEntry.TABLE_NAME,
                            AppointmentContract.AppointmentEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(
                            AppointmentContract.AppointmentEntry.TABLE_NAME,
                            AppointmentContract.AppointmentEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        db = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case APPOINTMENTS:
                rowsUpdated = db.update(
                        AppointmentContract.AppointmentEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case APPOINTMENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(
                            AppointmentContract.AppointmentEntry.TABLE_NAME,
                            values,
                            AppointmentContract.AppointmentEntry._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(
                            AppointmentContract.AppointmentEntry.TABLE_NAME,
                            values,
                            AppointmentContract.AppointmentEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
