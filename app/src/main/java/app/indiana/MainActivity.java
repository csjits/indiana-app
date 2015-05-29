package app.indiana;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import app.indiana.adapters.ViewPagerAdapter;
import app.indiana.services.PostService;
import app.indiana.views.HotPostsView;
import app.indiana.views.NewPostsView;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Indiana appState;
    ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appState = (Indiana) getApplicationContext();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        CharSequence tabTitles[] = {"Hot","New", "My"};
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabTitles, tabTitles.length);
        pager.setAdapter(mViewPagerAdapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.color_tabs_bar_active);
            }
        });

        tabs.setViewPager(pager);

        buildGoogleApiClient();

        runKarmaHandler();
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
        builder.setTitle("Create new Indy");
        builder.setIcon(R.drawable.ic_indiana);

        builder.setView(v.inflate(this, R.layout.dialog_create, null))
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        EditText messageInput = (EditText) d.findViewById(R.id.message_input);
                        appState.getUserLocation().refreshLocation();
                        Location lastLocation = appState.getUserLocation().getLastLocation();
                        PostService.post(messageInput.getText().toString(),
                                lastLocation.getLongitude(), lastLocation.getLatitude(),
                                appState.getUserHash(), new JsonHttpResponseHandler());
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

        Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        EditText messageInput = (EditText) dialog.getWindow().findViewById(R.id.message_input);
        TextView charCounter = (TextView) dialog.getWindow().findViewById(R.id.char_counter);
        messageInput.addTextChangedListener(createTextWatcher(charCounter, buttonPositive));
        buttonPositive.setEnabled(false);
    }

    private TextWatcher createTextWatcher(final TextView textView, final Button button) {
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText(String.valueOf(s.length()+"/200"));
                if (s.length() > 0) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
        return textWatcher;
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
        appState.getUserLocation().setConnected(true);

        HotPostsView hpv = (HotPostsView) mViewPagerAdapter.getView("hot");
        if (hpv != null) hpv.refresh();

        NewPostsView npv = (NewPostsView) mViewPagerAdapter.getView("new");
        if (npv != null) npv.refresh();

        //MyPostsView mpv = (MyPostsView) mViewPagerAdapter.getView("my");
        //mpv.refresh();
    }

    @Override
    public void onConnectionSuspended(int i) {
        appState.getUserLocation().setConnected(false);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        appState.getUserLocation().setConnected(false);
        Log.d("Connection failed", connectionResult.toString());
    }

    private void runKarmaHandler() {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchKarma();
                h.postDelayed(this, 10000);
            }
        }, 0);
    }

    private void fetchKarma() {
        PostService.karma(appState.getUserHash(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                TextView karmaTextView = (TextView) findViewById(R.id.toolbar_karma);
                int karma = response.optInt("karma");
                karmaTextView.setText(String.valueOf(karma));
            }
        });
    }

}
