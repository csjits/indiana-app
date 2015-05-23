package app.indiana;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by chris on 18.05.2015.
 */
public class UserLocationService {
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

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
}
