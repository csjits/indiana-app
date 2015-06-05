package app.indiana.services;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by chris on 09.05.2015.
 */
public class PostService {

    private static final String API_URL = "http://indiana.mirfac.uberspace.de/api/";
    //private static final String API_URL = "http://192.168.178.61:61017/";

    private static AsyncHttpClient mClient = new AsyncHttpClient();

    public static void get(double longitude, double latitude, String sort, String userHash,
                           AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("long", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        params.put("sort", sort);
        params.put("user", userHash);
        mClient.get(API_URL + "posts", params, responseHandler);
    }

    public static void vote(String postId, String action, String userHash,
                            AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("user", userHash);
        mClient.post(API_URL + "posts/" + postId + "/" + action, params, responseHandler);
    }

    public static void post(String message, double longitude, double latitude, String userHash,
                            AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("message", message);
        params.put("long", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        params.put("user", userHash);
        mClient.post(API_URL + "posts", params, responseHandler);
    }

    public static void karma(String userHash, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("user", userHash);
        mClient.get(API_URL + "karma", params, responseHandler);
    }
}
