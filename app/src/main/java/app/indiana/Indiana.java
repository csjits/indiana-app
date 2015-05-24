package app.indiana;

import android.app.Application;
import android.content.SharedPreferences;

import app.indiana.helpers.UserHelper;
import app.indiana.services.UserLocationService;

/**
 * Created by chris on 18.05.2015.
 */
public class Indiana extends Application {

    private UserLocationService mUserLocationService = new UserLocationService();
    private String mUserHash = "";

    public UserLocationService getUserLocation() {
        return mUserLocationService;
    }

    public String getUserHash() {
        if (mUserHash != "") return mUserHash;
        SharedPreferences userPrefs = getSharedPreferences("User", 0);
        mUserHash = userPrefs.getString("hash", UserHelper.createUserHash());
        if (!userPrefs.contains("hash")) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putString("hash", mUserHash);
            editor.commit();
        }
        return mUserHash;
    }

}
