package app.indiana.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.indiana.Indiana;
import app.indiana.R;

/**
 * Created by chris on 04.05.2015.
 */
public class NewPostsView extends PostsView {

    public NewPostsView() {
        super("new");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appState = (Indiana) getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fragment_new, container, false);

        RecyclerView hotRecyclerView = (RecyclerView) v.findViewById(R.id.new_CardList);
        setAdapter(hotRecyclerView);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_new);
        setRefreshLayout(swipeRefreshLayout);

        if (appState.getUserLocation().isConnected()) {
            fetchPosts();
        }

        return v;
    }

}
