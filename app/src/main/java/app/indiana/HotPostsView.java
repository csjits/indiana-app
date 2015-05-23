package app.indiana;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by chris on 04.05.2015.
 */
public class HotPostsView extends Fragment {

    Indiana appState;
    JSONAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hot, container, false);
        appState = (Indiana) getActivity().getApplicationContext();

        RecyclerView hotRecyclerView = (RecyclerView) v.findViewById(R.id.hot_CardList);
        hotRecyclerView.setHasFixedSize(false);
        hotRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hotRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new JSONAdapter();
        hotRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_hot);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPosts();
            }
        });

        //mApiAdapter.fetchPosts(appState.getUserLocation().getLastLocation().getLongitude(),
        //        appState.getUserLocation().getLastLocation().getLatitude());

        return v;
    }

    private void fetchPosts() {
        appState.getUserLocation().refreshLocation();
        Location loc = appState.getUserLocation().getLastLocation();
        PostService.get(loc.getLongitude(), loc.getLatitude(), "hot", createResponseHandler());
    }

    private JsonHttpResponseHandler createResponseHandler() {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // Toast error
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, JSONArray posts) {
                mAdapter.updateData(posts);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        return handler;
    }
}
