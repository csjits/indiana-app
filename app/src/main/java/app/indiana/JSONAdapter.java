package app.indiana;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        final Post post = new Post();
        final ApiAdapter apiAdapter = new ApiAdapter(view);
        JSONObject jsonObject = mPostArray.optJSONObject(position);

        post.id = jsonObject.optString("id");
        post.message = jsonObject.optString("message");
        post.age = jsonObject.optString("age");
        post.score = jsonObject.optString("score");
        post.distance = jsonObject.optString("distance");

        postViewHolder.vMessage.setText(post.message);
        postViewHolder.vAge.setText(post.age);
        postViewHolder.vScore.setText(post.score);
        postViewHolder.vDistance.setText(post.distance + "km");

        postViewHolder.vUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiAdapter.vote(post.id, "up");
            }
        });
        postViewHolder.vDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiAdapter.vote(post.id, "down");
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
