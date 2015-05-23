package app.indiana;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Indiana appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);

        appState = (Indiana) getApplicationContext();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        CharSequence tabTitles[] = {"Hot","New", "My"};
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabTitles, tabTitles.length);
        pager.setAdapter(adapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(pager);

        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void createMessageDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = v;
        builder.setTitle("Say something!");

        builder.setView(v.inflate(this, R.layout.dialog_create, null))
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PostService postService = new PostService(view);
                        Dialog d = (Dialog) dialog;
                        EditText messageInput = (EditText) d.findViewById(R.id.message_input);
                        appState.getUserLocation().refreshLocation();
                        Location lastLocation = appState.getUserLocation().getLastLocation();
                        postService.postMessage(messageInput.getText().toString(),
                                lastLocation.getLongitude(), lastLocation.getLatitude());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        appState.getUserLocation().setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnected(Bundle bundle) {
        appState.getUserLocation().refreshLocation();
        if (appState.getUserLocation() != null) {
            Log.d("long", String.valueOf(appState.getUserLocation().getLastLocation().getLatitude()));
            Log.d("lat", String.valueOf(appState.getUserLocation().getLastLocation().getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection failed", connectionResult.toString());
    }
}
