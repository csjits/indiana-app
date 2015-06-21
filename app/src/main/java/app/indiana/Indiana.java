package app.indiana;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.ViewGroup;

import app.indiana.helpers.UserHelper;
import app.indiana.services.PostCacheService;
import app.indiana.services.UserLocationService;

/**
 * Created by chris on 18.05.2015.
 */
public class Indiana extends Application {

    private UserLocationService mUserLocationService = new UserLocationService();
    private String mUserHash = "";
    public ViewGroup lastExpandedPost;
    public PostCacheService postCacheService;

    public UserLocationService getUserLocation() {
        return mUserLocationService;
    }

    public String getUserHash() {
        if (mUserHash != "") return mUserHash;
        SharedPreferences userPrefs = getSharedPreferences("User", 0);
        mUserHash = userPrefs.getString("hash", UserHelper.createUserHash(getApplicationContext()));
        if (!userPrefs.contains("hash")) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putString("hash", mUserHash);
            editor.apply();
        }
        return mUserHash;
    }
}
