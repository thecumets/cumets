package com.dciets.cumets;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dciets.cumets.adapter.RoommateAdapter;
import com.dciets.cumets.listener.LogoutListener;
import com.dciets.cumets.model.Roommate;
import com.dciets.cumets.network.Server;
import com.dciets.cumets.utils.DatabaseHelper;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.dciets.cumets.service.QuickstartPreferences;
import com.dciets.cumets.service.RegistrationIntentService;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DrawerLayout.DrawerListener, AdapterView.OnItemClickListener, LogoutListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "CumETS";
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RoommateAdapter adapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isActivityStarted;

    public static void show(final Context context ) {
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isActivityStarted = false;


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

                Snackbar.make(findViewById(android.R.id.content), sentToken ? "Sent" : "Not sent", Snackbar.LENGTH_SHORT).show();
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.start_countdown).setOnClickListener(this);
        ((ListView) findViewById(R.id.listView)).setEmptyView(findViewById(R.id.empty_view));
        ((ListView) findViewById(R.id.listView)).setOnItemClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void refreshList() {
        GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                try {
                    ArrayList<Roommate> roommates = new ArrayList<Roommate>();
                    JSONArray ja = response.getJSONObject().getJSONArray("data");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        JSONObject joPicture = jo.getJSONObject("picture").getJSONObject("data");
                        Roommate roommate = new Roommate(jo.getString("name"), jo.getString("id"),
                                joPicture.getString("url"));
                        roommates.add(roommate);
                        DatabaseHelper.getInstance().addRoommate(roommate.getProfileId());
                    }
                    adapter = new RoommateAdapter(getApplicationContext(), roommates);
                    ((ListView) findViewById(R.id.listView)).setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle b = new Bundle();
        b.putString("fields", "name,id,picture");
        request.setParameters(b);
        request.executeAsync();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_roommates) {
            findViewById(R.id.countdown_main).setVisibility(View.GONE);
            findViewById(R.id.roommates_main).setVisibility(View.VISIBLE);
            toolbar.setTitle(R.string.roommates);
            refreshList();
        } else if (id == R.id.nav_countdown) {
            findViewById(R.id.roommates_main).setVisibility(View.GONE);
            findViewById(R.id.countdown_main).setVisibility(View.VISIBLE);
            toolbar.setTitle(R.string.countdown);
        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            Server.logout(this, this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_countdown) {
            if(!isActivityStarted) {
                Server.start(this);
                ((TextView) findViewById(R.id.start_countdown)).setText(R.string.stop_countdown);
            } else {
                Server.stop(this);
                ((TextView) findViewById(R.id.start_countdown)).setText(R.string.start_countdown);
            }
            isActivityStarted = !isActivityStarted;
        } else if (v.getId() == R.id.fab) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_invite_title);
            builder.setMessage(R.string.dialog_invite_desc);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendInvite();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    public void sendInvite(){
        String shareBody = "Hey! Install the Cumets application for Android to enjoy private personal pleasure!\nhttp://www.cumets.com";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Roommate roommate = adapter.getItem(position);
        boolean isMonitor = DatabaseHelper.getInstance().isMonitor(roommate.getProfileId());
        if(isMonitor) {
            DatabaseHelper.getInstance().updateMonitor(roommate.getProfileId(), false);
            Server.unmonitorUser(this, roommate.getProfileId());
        } else {
            DatabaseHelper.getInstance().updateMonitor(roommate.getProfileId(), true);
            Server.monitorUser(this, roommate.getProfileId());
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLogoutSuccessful() {
        LoginActivity.show(this);
        finish();
    }

    @Override
    public void onLogoutError() {
        Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_SHORT).show();
    }
}
