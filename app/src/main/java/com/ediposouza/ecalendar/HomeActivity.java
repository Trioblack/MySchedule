package com.ediposouza.ecalendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ediposouza.ecalendar.adapters.AppointmentCursorAdapter;
import com.ediposouza.ecalendar.db.AppointmentContract;
import com.ediposouza.ecalendar.db.AppointmentDbHelper;

public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

        private TextView tvUserName;
        private RecyclerView rvAppointment;

        private AppointmentCursorAdapter appointmentAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            rvAppointment = (RecyclerView) view.findViewById(R.id.rvAppointment);
            appointmentAdapter = new AppointmentCursorAdapter(getActivity(), null);
            //Show UserName
            App app = (App) getActivity().getApplication();
            tvUserName.setText(app.getUserName());
            //Config RecyclerView
            rvAppointment.setAdapter(appointmentAdapter);
            rvAppointment.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvAppointment.setItemAnimator(new DefaultItemAnimator());
            // Initializes the loader
            getLoaderManager().initLoader(0, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            AppointmentDbHelper dbHelper = new AppointmentDbHelper(getActivity());
            final SQLiteDatabase database = dbHelper.getReadableDatabase();
            final Loader<Cursor> cursorLoader = new CursorLoader(getActivity(), null,
                    AppointmentContract.AppointmentEntry.PROJECTION_ALL_COLUMNS,
                    null,   //selection
                    null,   //args
                    null){  //sort
                @Override
                public Cursor loadInBackground() {
                    Cursor cursor = database.query(
                            AppointmentContract.AppointmentEntry.TABLE_NAME,
                            getProjection(),
                            getSelection(),
                            getSelectionArgs(),
                            null,   //group
                            null,   //having
                            getSortOrder());
                    return cursor;
                }
            };
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            appointmentAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            appointmentAdapter.swapCursor(null);
        }
    }
}
