package app.indiana.views;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import app.indiana.Indiana;
import app.indiana.R;
import app.indiana.adapters.PostAdapter;
import app.indiana.services.PostService;

/**
 * Created by chris on 23.05.2015.
 */
public abstract class PostsView extends Fragment {

    Indiana appState;
    PostAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String mSortType;
    String mToolbarDescription;

    public PostsView(String sortType) {
        mSortType = sortType;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        View view = getView();
        if (view != null && visible) {
            TextView toolbarDescription = (TextView) view.getRootView().findViewById(R.id.toolbar_description);
            toolbarDescription.setText(mToolbarDescription);
        }
    }

    protected void setAdapter(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PostAdapter();
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

    protected void setScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(dx == 0 && topRowVerticalPosition >= 0);
            }
        });

    }

    protected void fetchPosts() {
        appState.getUserLocation().refreshLocation();
        Location loc = appState.getUserLocation().getLastLocation();
        PostService.get(loc.getLongitude(), loc.getLatitude(), mSortType, appState.getUserHash(), createResponseHandler());
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
