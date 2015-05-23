package app.indiana;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chris on 23.05.2015.
 */
public abstract class PostsView extends Fragment {

    Indiana appState;
    JSONAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String mSortType;

    public PostsView(String sortType) {
        mSortType = sortType;
    }

    protected void setAdapter(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new JSONAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    protected void setRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPosts();
            }
        });
    }

    protected void fetchPosts() {
        appState.getUserLocation().refreshLocation();
        Location loc = appState.getUserLocation().getLastLocation();
        PostService.get(loc.getLongitude(), loc.getLatitude(), mSortType, createResponseHandler());
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


    public void refresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        fetchPosts();
    }
}
