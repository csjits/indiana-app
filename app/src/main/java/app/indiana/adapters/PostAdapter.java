package app.indiana.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import app.indiana.Indiana;
import app.indiana.models.PostContainer;
import app.indiana.services.PostService;
import app.indiana.R;
import app.indiana.views.PostsView;


/**
 * Created by chris on 04.05.2015.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Indiana appState;
    JSONArray mPostArray = new JSONArray();
    View view;
    PostsView postsView;

    public PostAdapter(PostsView postsView) {
        this.postsView = postsView;
    }

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
    public void onBindViewHolder(final PostViewHolder postViewHolder, int position) {
        appState = (Indiana) postViewHolder.itemView.getContext().getApplicationContext();
        final PostContainer postContainer = new PostContainer();
        final int primaryColor = postViewHolder.itemView.getResources().getColor(R.color.color_primary);
        JSONObject jsonObject = mPostArray.optJSONObject(position);

        postContainer.id = jsonObject.optString("id");
        postContainer.message = jsonObject.optString("message");
        postContainer.age = jsonObject.optString("age");
        postContainer.score = jsonObject.optString("score");
        postContainer.distance = jsonObject.optString("distance");
        postContainer.voted = jsonObject.optInt("voted");
        postContainer.replies = jsonObject.optInt("replies");

        postViewHolder.vMessage.setText(postContainer.message);
        postViewHolder.vAge.setText(postContainer.age);
        postViewHolder.vScore.setText(postContainer.score);
        postViewHolder.vScore.setTextColor(Color.DKGRAY);
        String distanceUnit = postViewHolder.itemView.getResources().getString(R.string.distance_unit);
        postViewHolder.vDistance.setText(postContainer.distance + distanceUnit);
        int scoreColor = (postContainer.voted == 0) ? Color.DKGRAY : primaryColor;
        postViewHolder.vScore.setTextColor(scoreColor);

        int upvoteResource = (postContainer.voted == 1) ? R.drawable.upvote_active : R.drawable.upvote;
        postViewHolder.vUpvote.setBackgroundResource(upvoteResource);
        postViewHolder.vUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postContainer.voted != 0) return;
                postContainer.voted = 1;
                PostService.vote(postContainer.id, "up", appState.getUserHash(), new JsonHttpResponseHandler());
                ImageButton upvoteButton = (ImageButton) v;
                upvoteButton.setBackgroundResource(R.drawable.upvote_active);
                postContainer.score = String.valueOf(Integer.parseInt(postContainer.score) + 1);
                postViewHolder.vScore.setText(postContainer.score);
                postViewHolder.vScore.setTextColor(primaryColor);
            }
        });

        int downvoteResource = (postContainer.voted == -1) ? R.drawable.downvote_active : R.drawable.downvote;
        postViewHolder.vDownvote.setBackgroundResource(downvoteResource);
        postViewHolder.vDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postContainer.voted != 0) return;
                postContainer.voted = -1;
                PostService.vote(postContainer.id, "down", appState.getUserHash(), new JsonHttpResponseHandler());
                ImageButton downvoteButton = (ImageButton) v;
                downvoteButton.setBackgroundResource(R.drawable.downvote_active);
                postContainer.score = String.valueOf(Integer.parseInt(postContainer.score) - 1);
                postViewHolder.vScore.setText(postContainer.score);
                postViewHolder.vScore.setTextColor(primaryColor);
            }
        });

        if (postContainer.replies == 1) {
            postViewHolder.vReplyCount.setText(postContainer.replies + " "
                    + R.string.reply_num_replies_one);
        } else if (postContainer.replies > 1) {
            postViewHolder.vReplyCount.setText(postContainer.replies + " "
                    + R.string.reply_num_replies_multi);
        }
        else {
            postViewHolder.vReplyCount.setText(R.string.reply_zero_replies);
        }

        postViewHolder.vPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout replies = postViewHolder.vRepliesLayout;

                if (appState.lastExpandedPost != null && appState.lastExpandedPost != replies) {
                    appState.lastExpandedPost.setVisibility(View.GONE);
                }

                if (replies.getVisibility() == View.GONE) {
                    replies.setVisibility(View.VISIBLE);
                } else {
                    replies.setVisibility(View.GONE);
                }

                postsView.expandPost(postContainer.id, (View) v.getParent());
                appState.lastExpandedPost = replies;
            }
        });

        postViewHolder.vReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = postViewHolder.vReplyMessage.getText().toString();
                double longitude = appState.getUserLocation().getLastLocation().getLongitude();
                double latitude = appState.getUserLocation().getLastLocation().getLatitude();
                PostService.reply(message, longitude, latitude, appState.getUserHash(),
                        postContainer.id, new JsonHttpResponseHandler());
            }
        });
    }

    public void updateData(JSONArray jsonArray) {
        mPostArray = jsonArray;
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout vPostLayout;
        public LinearLayout vRepliesLayout;
        public TextView vMessage;
        public TextView vAge;
        public TextView vScore;
        public TextView vDistance;
        public TextView vReplyCount;
        public ImageButton vUpvote;
        public ImageButton vDownvote;
        public EditText vReplyMessage;
        public Button vReplyButton;

        public PostViewHolder(View v) {
            super(v);
            vPostLayout = (RelativeLayout) v.findViewById(R.id.layout_post);
            vRepliesLayout = (LinearLayout) v.findViewById(R.id.layout_replies);
            vMessage = (TextView) v.findViewById(R.id.post_message);
            vAge = (TextView) v.findViewById(R.id.post_date);
            vScore = (TextView) v.findViewById(R.id.post_score);
            vDistance = (TextView) v.findViewById(R.id.post_distance);
            vReplyCount = (TextView) v.findViewById(R.id.post_reply_count);
            vUpvote = (ImageButton) v.findViewById(R.id.post_upvote);
            vDownvote = (ImageButton) v.findViewById(R.id.post_downvote);
            vReplyMessage = (EditText) v.findViewById(R.id.reply_message);
            vReplyButton = (Button) v.findViewById(R.id.button_reply);
        }
    }

}
