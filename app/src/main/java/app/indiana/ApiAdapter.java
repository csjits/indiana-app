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
public class ApiAdapter {

    //private static final String API_URL = "http://indiana.mirfac.uberspace.de/api/";
    private static final String API_URL = "http://192.168.178.61:61017/";

    private View callbackView;

    public ApiAdapter() {}

    public ApiAdapter(View v) {
        callbackView = v;
    }

    public void fetchPosts() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) callbackView.findViewById(R.id.fragment_hot);
        final RecyclerView recyclerView = (RecyclerView) callbackView.findViewById(R.id.hot_CardList);
        final JSONAdapter jsonAdapter = (JSONAdapter) recyclerView.getAdapter();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL + "posts", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                jsonAdapter.updateData(jsonArray);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                //Toast.makeText(getActivity().getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void vote(String postId, String action) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(API_URL + "posts/" + postId + "/" + action, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                //Toast.makeText(getActivity().getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                //Toast.makeText(getActivity().getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void postMessage(String message) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("message", message);
        params.put("lat", "5");
        params.put("long", "6");
        client.post(API_URL + "posts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                //Toast.makeText(getActivity().getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                //Toast.makeText(getActivity().getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
