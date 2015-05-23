package app.indiana;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chris on 09.05.2015.
 */
public class PostService {

    private static final String API_URL = "http://indiana.mirfac.uberspace.de/api/";
    //private static final String API_URL = "http://192.168.178.61:61017/";

    private View callbackView;

    public PostService() {}

    public PostService(View v) {
        callbackView = v;
    }

    public void fetchPosts(double longitude, double latitude) {
        Log.d("indiana", "fetchPosts");
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) callbackView.findViewById(R.id.fragment_hot);
        final RecyclerView recyclerView = (RecyclerView) callbackView.findViewById(R.id.hot_CardList);
        final JSONAdapter jsonAdapter = (JSONAdapter) recyclerView.getAdapter();

        RequestParams params = new RequestParams();
        params.put("long", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "posts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                jsonAdapter.updateData(jsonArray);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("failure", "Error: " + statusCode + " " + throwable.getMessage());
            }
        });
    }

    public void vote(String postId, String action) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(API_URL + "posts/" + postId + "/" + action, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                // Vote success
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                Log.d("failure", "Error: " + statusCode + " " + throwable.getMessage());
            }
        });
    }

    public void postMessage(String message, double longitude, double latitude) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("message", message);
        params.put("long", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        client.post(API_URL + "posts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                Log.d("post", "success");
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                Log.d("failure", "Error: " + statusCode + " " + throwable.getMessage());
            }
        });
    }
}
