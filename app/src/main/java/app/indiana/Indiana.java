package app.indiana;

import android.app.Application;
import android.content.SharedPreferences;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import app.indiana.helpers.UserHelper;
import app.indiana.services.PostService;
import app.indiana.services.UserLocationService;

/**
 * Created by chris on 18.05.2015.
 */
public class Indiana extends Application {

    private UserLocationService mUserLocationService = new UserLocationService();
    private String mUserHash = "";
    private String mToken = "";

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
            editor.apply();
        }
        return mUserHash;
    }

    public String getToken() {
        if (mToken != "") return mToken;
        SharedPreferences userPrefs = getSharedPreferences("User", 0);
        mToken = userPrefs.getString("token", "");
        return mToken;
    }

    public void fetchToken(String userHash) {
        PostService.token(userHash, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                String token = response.optString("token");
                if (token != null) {
                    mToken = token;
                    SharedPreferences userPrefs = getSharedPreferences("User", 0);
                    SharedPreferences.Editor editor = userPrefs.edit();
                    editor.putString("token", mToken);
                    editor.apply();
                }
            }
        });
    }

}
