package com.ediposouza.schedule.db;

import android.provider.BaseColumns;

/**
 * Created by ufc134.souza on 15/09/2014.
 */
public final class AppointmentContract {

    private AppointmentContract() {
    }

    public static abstract class AppointmentEntry implements BaseColumns {

        public static final String TABLE_NAME = "appointment";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESC = "desc";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_CONTACT_URI = "contact_uri";

        public static final String[] PROJECTION_ALL_COLUMNS = {
                BaseColumns._ID,
                COLUMN_TITLE,
                COLUMN_DESC,
                COLUMN_DATE,
                COLUMN_TIME,
                COLUMN_CONTACT_URI
        };

    }

}
