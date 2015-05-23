package app.indiana;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * Created by chris on 04.05.2015.
 */
public class JSONAdapter extends RecyclerView.Adapter<JSONAdapter.PostViewHolder> {

    JSONArray mPostArray = new JSONArray();
    View view;

    @Override
    public int getItemCount() {
        return mPostArray.length();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.row_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder postViewHolder, int position) {
        final PostContainer postContainer = new PostContainer();
        JSONObject jsonObject = mPostArray.optJSONObject(position);

        postContainer.id = jsonObject.optString("id");
        postContainer.message = jsonObject.optString("message");
        postContainer.age = jsonObject.optString("age");
        postContainer.score = jsonObject.optString("score");
        postContainer.distance = jsonObject.optString("distance");

        postViewHolder.vMessage.setText(postContainer.message);
        postViewHolder.vAge.setText(postContainer.age);
        postViewHolder.vScore.setText(postContainer.score);
        postViewHolder.vDistance.setText(postContainer.distance + "km");

        postViewHolder.vUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostService.vote(postContainer.id, "up", new JsonHttpResponseHandler());
            }
        });
        postViewHolder.vDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostService.vote(postContainer.id, "down", new JsonHttpResponseHandler());
            }
        });
    }

    public void updateData(JSONArray jsonArray) {
        mPostArray = jsonArray;
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView vMessage;
        public TextView vAge;
        public TextView vScore;
        public TextView vDistance;
        public Button vUpvote;
        public Button vDownvote;

        public PostViewHolder(View v) {
            super(v);
            vMessage = (TextView) v.findViewById(R.id.post_message);
            vAge = (TextView) v.findViewById(R.id.post_date);
            vScore = (TextView) v.findViewById(R.id.post_score);
            vDistance = (TextView) v.findViewById(R.id.post_distance);
            vUpvote = (Button) v.findViewById(R.id.post_upvote);
            vDownvote = (Button) v.findViewById(R.id.post_downvote);
        }
    }
}
