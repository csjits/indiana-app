package app.indiana.views;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import app.indiana.Indiana;
import app.indiana.R;
import app.indiana.adapters.PostAdapter;
import app.indiana.adapters.ReplyAdapter;
import app.indiana.helpers.JsonHelper;
import app.indiana.helpers.ViewHelper;
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
        mAdapter = new PostAdapter(this);
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
                mSwipeRefreshLayout.setEnabled(dy == 0 && topRowVerticalPosition >= 0);
            }
        });

    }

    protected void fetchPosts() {
        appState.getUserLocation().refreshLocation();
        Location loc = appState.getUserLocation().getLastLocation();
        PostService.get(loc.getLongitude(), loc.getLatitude(), mSortType, appState.getUserHash(),
                createPostResponseHandler());
    }

    protected void fetchReplies(String postId, View view) {
        view.findViewById(R.id.replies_spinner).setVisibility(View.VISIBLE);
        appState.getUserLocation().refreshLocation();
        Location loc = appState.getUserLocation().getLastLocation();
        PostService.replies(loc.getLongitude(), loc.getLatitude(), appState.getUserHash(), postId,
                createRepliesResponseHandler(view));
    }

    private JsonHttpResponseHandler createPostResponseHandler() {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                mSwipeRefreshLayout.setRefreshing(false);
                String msg = response.optString("message");
                Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, JSONArray posts) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (posts.length() == 0) {
                    return;
                }
                mAdapter.updateData(posts);
                try {
                    int emptyViewId = getResources().getIdentifier("empty_view_"+mSortType, "id", getActivity().getPackageName());
                    TextView emptyView = (TextView) getActivity().findViewById(emptyViewId);
                    emptyView.setVisibility(View.GONE);
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                 org.apache.http.Header[] headers,
                                 String responseString,
                                 Throwable throwable) {
                mSwipeRefreshLayout.setRefreshing(false);
                String msg = getString(R.string.error_connection_failed);
                Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        };
        return handler;
    }

    private JsonHttpResponseHandler createRepliesResponseHandler(final View view) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                view.findViewById(R.id.replies_spinner).setVisibility(View.GONE);
                ListView replyList = (ListView) view.findViewById(R.id.post_replies);

                int replies = JsonHelper.countReplies(response);
                String replyText = ViewHelper.getReplyText(replies, view);
                ((TextView) view.findViewById(R.id.post_reply_count)).setText(replyText);

                ReplyAdapter replyAdapter = new ReplyAdapter(getActivity().getApplicationContext(), response);
                replyList.setAdapter(replyAdapter);

                if (replyList.getHeaderViewsCount() < 1) {
                    replyList.addHeaderView(new View(getActivity().getApplicationContext()));
                }

                if (replyList.getFooterViewsCount() < 1) {
                    replyList.addFooterView(new View(getActivity().getApplicationContext()));
                }

                ViewHelper.setListViewHeightBasedOnItems(replyList);
            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers,
                                  String responseString,
                                  Throwable throwable) {
                view.findViewById(R.id.replies_spinner).setVisibility(View.GONE);
                String msg = getString(R.string.error_connection_failed);
                Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        };
        return handler;
    }

    public void refresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        fetchPosts();
    }

    public void expandPost(String postId, View view) {
        fetchReplies(postId, view);
    }
}
