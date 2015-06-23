package app.indiana;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import app.indiana.adapters.ViewPagerAdapter;
import app.indiana.helpers.ViewHelper;
import app.indiana.services.BackgroundDataService;
import app.indiana.services.NotificationService;
import app.indiana.services.PostService;
import app.indiana.views.HotPostsView;
import app.indiana.views.MyPostsView;
import app.indiana.views.NewPostsView;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Indiana appState;
    GoogleApiClient mGoogleApiClient;
    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    boolean mIsInForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appState = (Indiana) getApplicationContext();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        CharSequence tabTitles[] = {
                getString(R.string.tab_hot_title),
                getString(R.string.tab_new_title),
                getString(R.string.tab_my_title)
        };
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabTitles, tabTitles.length);
        mViewPager.setAdapter(mViewPagerAdapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.color_tabs_bar_active);
            }
        });
        tabs.setViewPager(mViewPager);

        checkLocationServices();
        buildGoogleApiClient();
        runKarmaHandler();
        com.facebook.FacebookSdk.sdkInitialize(this);

        mIsInForeground = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.facebook.appevents.AppEventsLogger.activateApp(this, "408085569375556");
        mIsInForeground = true;
        appState.getUserLocation().refreshLocation();
        refreshViews();
        new BackgroundDataService(this).stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsInForeground = false;
        BackgroundDataService backgroundDataService = new BackgroundDataService(this);
        backgroundDataService.setIntent(NotificationService.class);
        backgroundDataService.run(android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void createMessageDialog(View v) {
        appState.getUserLocation().refreshLocation();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_create_title));
        builder.setIcon(R.drawable.ic_indiana);

        builder.setView(View.inflate(this, R.layout.dialog_create, null))
                .setPositiveButton(getString(R.string.dialog_create_send), null)
                .setNegativeButton(getString(R.string.dialog_create_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow (DialogInterface dialog) {
                final AlertDialog d = (AlertDialog) dialog;
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText messageInput = (EditText) d.findViewById(R.id.message_input);
                        messageInput.setEnabled(false);
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        d.findViewById(R.id.post_spinner).setVisibility(View.VISIBLE);
                        Location lastLocation = appState.getUserLocation().getLastLocation();
                        PostService.post(messageInput.getText().toString(),
                                lastLocation.getLongitude(), lastLocation.getLatitude(),
                                appState.getUserHash(), createPostResponseHandler(d));
                    }
                });
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        EditText messageInput = (EditText) dialog.getWindow().findViewById(R.id.message_input);
        TextView charCounter = (TextView) dialog.getWindow().findViewById(R.id.char_counter);
        messageInput.addTextChangedListener(ViewHelper.createTextWatcher(charCounter,
                buttonPositive, getString(R.string.divider_max_chars), getString(R.string.max_chars)));
        buttonPositive.setEnabled(false);
    }

    private JsonHttpResponseHandler createPostResponseHandler(final DialogInterface dialog) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                String message = response.optString("message");
                if (message.equals("OK")) {
                    dialog.dismiss();
                    mViewPager.setCurrentItem(mViewPagerAdapter.getPosition("my"));
                    MyPostsView mpv = (MyPostsView) mViewPagerAdapter.getView("my");
                    mpv.refresh();
                } else {
                    AlertDialog d = (AlertDialog) dialog;
                    d.findViewById(R.id.message_input).setEnabled(true);
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    d.findViewById(R.id.post_spinner).setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers,
                                  String responseString,
                                  Throwable throwable) {
                String msg = getString(R.string.error_post_failed);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                AlertDialog d = (AlertDialog) dialog;
                d.findViewById(R.id.message_input).setEnabled(true);
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                d.findViewById(R.id.post_spinner).setVisibility(View.GONE);
            }
        };
    }

    private void checkLocationServices() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.error_location_disabled_title));
            builder.setMessage(getString(R.string.error_location_disabled_message));

            builder.setPositiveButton(getString(R.string.error_location_disabled_okay), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        appState.getUserLocation().setGoogleApiClient(mGoogleApiClient);
    }

    private void refreshViews() {
        HotPostsView hpv = (HotPostsView) mViewPagerAdapter.getView("hot");
        if (hpv != null) hpv.refresh();

        NewPostsView npv = (NewPostsView) mViewPagerAdapter.getView("new");
        if (npv != null) npv.refresh();
    }

    @Override
    public void onConnected(Bundle bundle) {
        appState.getUserLocation().refreshLocation();
        appState.getUserLocation().setConnected(true);
        refreshViews();
    }

    @Override
    public void onConnectionSuspended(int i) {
        appState.getUserLocation().setConnected(false);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        appState.getUserLocation().setConnected(false);
        Toast.makeText(this, getString(R.string.error_location_connection_failed), Toast.LENGTH_SHORT).show();
    }

    private void runKarmaHandler() {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsInForeground) {
                    fetchKarma();
                }
                h.postDelayed(this, 10000);
            }
        }, 0);
    }

    private void fetchKarma() {
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.pop);
        PostService.karma(appState.getUserHash(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                TextView karmaTextView = (TextView) findViewById(R.id.toolbar_karma);
                int oldKarma = Integer.parseInt(karmaTextView.getText().toString());
                int karma = response.optInt("karma");
                karmaTextView.setText(String.valueOf(karma));
                if (karma != oldKarma) {
                    karmaTextView.startAnimation(anim);
                }
            }
        });
    }

}
