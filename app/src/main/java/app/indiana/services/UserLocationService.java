package app.indiana.services;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by chris on 18.05.2015.
 */
public class UserLocationService {
    private Location mLastLocation = new Location("foo");
    private GoogleApiClient mGoogleApiClient;
    private boolean isConnected = false;

    public void setGoogleApiClient(GoogleApiClient newClient) {
        mGoogleApiClient = newClient;
    }

    public void refreshLocation() {
        Location newLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (newLocation != null) {
            mLastLocation = newLocation;
        }
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
