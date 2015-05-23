package app.indiana;

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


/**
 * Created by chris on 04.05.2015.
 */
public class HotPostsView extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hot, container, false);

        final Indiana appState = (Indiana) getActivity().getApplicationContext();
        final PostService mPostService = new PostService(v);

        RecyclerView hotRecyclerView = (RecyclerView) v.findViewById(R.id.hot_CardList);
        hotRecyclerView.setHasFixedSize(false);
        hotRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hotRecyclerView.setItemAnimator(new DefaultItemAnimator());
        hotRecyclerView.setAdapter(new JSONAdapter());

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_hot);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                appState.getUserLocation().refreshLocation();
                mPostService.fetchPosts(appState.getUserLocation().getLastLocation().getLongitude(),
                        appState.getUserLocation().getLastLocation().getLatitude());
            }
        });

        //mApiAdapter.fetchPosts(appState.getUserLocation().getLastLocation().getLongitude(),
        //        appState.getUserLocation().getLastLocation().getLatitude());

        return v;
    }


}
