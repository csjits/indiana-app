package app.indiana;

import android.app.Application;

/**
 * Created by chris on 18.05.2015.
 */
public class Indiana extends Application {

    private UserLocation mUserLocation = new UserLocation();

    public UserLocation getUserLocation() {
        return mUserLocation;
    }

}
