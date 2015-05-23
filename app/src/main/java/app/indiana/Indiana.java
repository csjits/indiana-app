package app.indiana;

import android.app.Application;

/**
 * Created by chris on 18.05.2015.
 */
public class Indiana extends Application {

    private UserLocationService mUserLocationService = new UserLocationService();

    public UserLocationService getUserLocation() {
        return mUserLocationService;
    }

}
