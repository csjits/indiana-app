package app.indiana.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import app.indiana.Indiana;
import app.indiana.helpers.JsonHelper;
import app.indiana.helpers.ViewHelper;
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
        final int primaryColor = postViewHolder.itemView.getResources().getColor(R.color.color_primary);
        JSONObject jsonObject = mPostArray.optJSONObject(position);

        final PostContainer postContainer = JsonHelper.toPost(jsonObject);

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

        postViewHolder.vReplyCount.setText(ViewHelper.getReplyText(postContainer.replies, view));

        postViewHolder.vPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup replies = postViewHolder.vRepliesLayout;

                if (appState.lastExpandedPost != null && appState.lastExpandedPost != replies) {
                    appState.lastExpandedPost.setVisibility(View.GONE);
                }

                if (replies.getVisibility() == View.GONE) {
                    replies.setVisibility(View.VISIBLE);
                    postsView.expandPost(postContainer.id, (View) v.getParent());
                    appState.lastExpandedPost = replies;
                } else {
                    replies.setVisibility(View.GONE);
                }
            }
        });

        postViewHolder.vReplyMessage.addTextChangedListener(
                ViewHelper.createTextWatcher(postViewHolder.vCharCounterReply,
                        postViewHolder.vReplyButton,
                        view.getResources().getString(R.string.divider_max_chars),
                        view.getResources().getString(R.string.max_chars)));
        postViewHolder.vReplyButton.setEnabled(false);

        postViewHolder.vReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentView = (View) v.getParent();
                String message = postViewHolder.vReplyMessage.getText().toString();
                double longitude = appState.getUserLocation().getLastLocation().getLongitude();
                double latitude = appState.getUserLocation().getLastLocation().getLatitude();
                parentView.findViewById(R.id.reply_spinner).setVisibility(View.VISIBLE);
                parentView.findViewById(R.id.reply_message).setEnabled(false);
                parentView.findViewById(R.id.button_reply).setEnabled(false);
                PostService.reply(message, longitude, latitude, appState.getUserHash(),
                        postContainer.id, createReplyResponseHandler(postContainer.id, parentView));
            }
        });
    }

    private JsonHttpResponseHandler createReplyResponseHandler(final String postId, final View view) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                view.findViewById(R.id.reply_spinner).setVisibility(View.GONE);
                TextView messageView = (TextView) view.findViewById(R.id.reply_message);
                messageView.setEnabled(true);
                view.findViewById(R.id.button_reply).setEnabled(true);
                String message = response.optString("message");
                if (message.equals("OK")) {
                    postsView.expandPost(postId, (View) view.getParent());
                    messageView.setText("");
                    messageView.clearFocus();
                } else {
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  org.apache.http.Header[] headers,
                                  String responseString,
                                  Throwable throwable) {
                String msg = view.getResources().getString(R.string.error_reply_failed);
                Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
                view.findViewById(R.id.reply_spinner).setVisibility(View.GONE);
                view.findViewById(R.id.reply_message).setEnabled(true);
                view.findViewById(R.id.button_reply).setEnabled(true);
            }
        };
    }

    public void updateData(JSONArray jsonArray) {
        mPostArray = jsonArray;
        notifyDataSetChanged();
        try {
            appState.postCacheService.cache(postsView.getType(), jsonArray);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup vPostLayout;
        public ViewGroup vRepliesLayout;
        public TextView vMessage;
        public TextView vAge;
        public TextView vScore;
        public TextView vDistance;
        public TextView vReplyCount;
        public ImageButton vUpvote;
        public ImageButton vDownvote;
        public EditText vReplyMessage;
        public Button vReplyButton;
        public TextView vCharCounterReply;

        public PostViewHolder(View v) {
            super(v);
            vPostLayout = (ViewGroup) v.findViewById(R.id.layout_post);
            vRepliesLayout = (ViewGroup) v.findViewById(R.id.layout_replies);
            vMessage = (TextView) v.findViewById(R.id.post_message);
            vAge = (TextView) v.findViewById(R.id.post_date);
            vScore = (TextView) v.findViewById(R.id.post_score);
            vDistance = (TextView) v.findViewById(R.id.post_distance);
            vReplyCount = (TextView) v.findViewById(R.id.post_reply_count);
            vUpvote = (ImageButton) v.findViewById(R.id.post_upvote);
            vDownvote = (ImageButton) v.findViewById(R.id.post_downvote);
            vReplyMessage = (EditText) v.findViewById(R.id.reply_message);
            vReplyButton = (Button) v.findViewById(R.id.button_reply);
            vCharCounterReply = (TextView) v.findViewById(R.id.char_counter_reply);
        }
    }

}
