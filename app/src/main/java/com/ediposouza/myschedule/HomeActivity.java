package com.ediposouza.myschedule;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediposouza.myschedule.adapters.AppointmentCursorAdapter;
import com.ediposouza.myschedule.db.AppointmentContract;
import com.ediposouza.myschedule.db.AppointmentDbHelper;
import com.ediposouza.myschedule.service.NotificationService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melnykov.fab.FloatingActionButton;

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {

    private static PlaceholderFragment homeFragment;

    public enum sort {BY_NAME, BY_DATE};

    private Boolean exitApp = false;
    private String[] mNavDrawerItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public static void reloadList() {
        homeFragment.reloadList(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mNavDrawerItems = getResources().getStringArray(R.array.home_nav_drawner_items);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // config the navegation drawer list
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name  /* "close drawer" description */
            ) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
        };
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavDrawerItems));
        mDrawerList.setOnItemClickListener(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        //Check UserNane
        App app = (App) getApplication();
        if(app.getUserName() == null){
            showLoginScreen();
        }
        //check permission
        String homePermission = "com.ediposouza.myschedule.permission.HOME_ACTIVITY";
        if(checkCallingOrSelfPermission(homePermission) != PackageManager.PERMISSION_GRANTED)
            throw new SecurityException();
        if (savedInstanceState == null) {
            homeFragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, homeFragment)
                    .commit();
        }
    }

    private void showLoginScreen() {
        App app = (App) getApplication();
        app.setUserName(null);
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_sort_date).setVisible(!drawerOpen);
        menu.findItem(R.id.action_sort_name).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (exitApp){
            //reset login
            HomeActivity homeActivity = HomeActivity.this;
            if(homeActivity != null)
                homeActivity.finish();
            App app = (App) getApplication();
            app.setUserName(null);
        }else {
            String exitConfirmationMsg = getString(R.string.home_exit_confirmation);
            Toast.makeText(this, exitConfirmationMsg, Toast.LENGTH_SHORT).show();
            exitApp = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitApp = false;
                }
            }, 3000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sort_name) {
            homeFragment.reloadList(sort.BY_NAME);
            return true;
        }
        if (id == R.id.action_sort_date) {
            homeFragment.reloadList(sort.BY_DATE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String mNavDrawerClickedItem = mNavDrawerItems[position];
        if(mNavDrawerClickedItem.equals(getString(R.string.home_nav_drawner_logout))){
            showLoginScreen();
        }
        if(mNavDrawerClickedItem.equals(getString(R.string.home_nav_drawner_config))){
            Toast.makeText(this, "Config", Toast.LENGTH_SHORT).show();
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

        private static final int hideFabDelay = 3000;

        private TextView tvUserName;
        private RecyclerView rvAppointment;
        private FloatingActionButton fabAdd;
        private AdView mAdView;

        private Handler handler;
        private boolean hideFab = true;
        private boolean notificationServiceBound = false;
        private NotificationService notificationService;
        private AppointmentCursorAdapter appointmentAdapter;
        private String sortOrder = AppointmentContract.AppointmentEntry.COLUMN_DATE + " DESC";

        /** Defines callbacks for service binding, passed to bindService() */
        private ServiceConnection notificationServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                NotificationService.LocalBinder binder = (NotificationService.LocalBinder) service;
                notificationService = binder.getService();
                notificationServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                notificationServiceBound = false;
            }
        };

        private Runnable hideFabRunnable = new Runnable() {
            @Override
            public void run() {
                if(hideFab)
                    fabAdd.hide(true);
                handler.postDelayed(this, hideFabDelay);
            }
        };

        public PlaceholderFragment() {
            handler = new Handler(Looper.getMainLooper());
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
            fabAdd = (FloatingActionButton) view.findViewById(R.id.fabAdd);
            mAdView = (AdView) view.findViewById(R.id.adView);
            appointmentAdapter = new AppointmentCursorAdapter(getActivity(), null);
            //Init Ads
            mAdView.loadAd(new AdRequest.Builder().build());
            //Config FAB
            fabAdd.setOnClickListener(this);
            //Show UserName
            App app = (App) getActivity().getApplication();
            tvUserName.setText(app.getUserName());
            //Config RecyclerView
            rvAppointment.setAdapter(appointmentAdapter);
            rvAppointment.setItemAnimator(new DefaultItemAnimator());
            rvAppointment.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvAppointment.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideFab = false;
                    fabAdd.show(true);
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideFab = true;
                            }
                        }, hideFabDelay);
                    }
                    return false;
                }
            });
            // Bind to LocalService
            Intent intent = new Intent(getActivity(), NotificationService.class);
            getActivity().bindService(intent, notificationServiceConnection, Context.BIND_AUTO_CREATE);
            // Initializes the loader
            getLoaderManager().initLoader(0, null, this);
            handler.postDelayed(hideFabRunnable, hideFabDelay);
        }

        @Override
        public void onDestroyView() {
            // Unbind from the service
            if (notificationServiceBound) {
                getActivity().unbindService(notificationServiceConnection);
                notificationServiceBound = false;
            }
            super.onDestroyView();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onResume() {
            reloadList(sort.BY_NAME);
            super.onResume();
        }

        public void reloadList(sort by) {
            if(by != null)
                if (by == sort.BY_DATE)
                    sortOrder = AppointmentContract.AppointmentEntry.COLUMN_DATE + " DESC";
                else
                    sortOrder = AppointmentContract.AppointmentEntry.COLUMN_TITLE + " ASC";
            getLoaderManager().restartLoader(0, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            App app = (App) getActivity().getApplication();
            //Check UserNane
            if(app.getUserName() == null){
                return null;
            }
            AppointmentDbHelper dbHelper = new AppointmentDbHelper(getActivity());
            final SQLiteDatabase database = dbHelper.getReadableDatabase();
            String selection = AppointmentContract.AppointmentEntry.COLUMN_USERNAME_HASH +
                    "=" + String.valueOf(app.getUserName().hashCode());
            final Loader<Cursor> cursorLoader = new CursorLoader(getActivity(), null,
                    AppointmentContract.AppointmentEntry.PROJECTION_ALL_COLUMNS,
                    selection,   //selection
                    null,   //args
                    sortOrder){  //sort
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
            if(notificationService != null)
                notificationService.updateAppointmentsNotification();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            appointmentAdapter.swapCursor(null);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), NewAppointmentActivity.class);
            startActivity(intent);
        }

    }
}
