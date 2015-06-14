package app.indiana.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import app.indiana.Indiana;
import app.indiana.R;
import app.indiana.helpers.JsonHelper;
import app.indiana.models.ReplyContainer;
import app.indiana.services.PostService;

/**
 * Created by chris on 11.06.2015.
 */
public class ReplyAdapter extends ArrayAdapter<ReplyContainer>{

    Indiana appState;

    public ReplyAdapter(Context context, ArrayList<ReplyContainer> replies) {
        super(context, R.layout.row_reply, replies);
        appState = (Indiana) context.getApplicationContext();
    }

    public ReplyAdapter(Context context, JSONObject json) {
        this(context, JsonHelper.parseReplies(json));
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ReplyContainer replyContainer = getItem(position);
        final ReplyViewHolder replyViewHolder;

        if (view == null) {
            replyViewHolder = new ReplyViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.row_reply, parent, false);
            replyViewHolder.vReplyText = (TextView) view.findViewById(R.id.reply_text);
            replyViewHolder.vAge = (TextView) view.findViewById(R.id.reply_date);
            replyViewHolder.vScore = (TextView) view.findViewById(R.id.reply_score);
            replyViewHolder.vUpvote = (ImageButton) view.findViewById(R.id.reply_upvote);
            replyViewHolder.vDownvote = (ImageButton) view.findViewById(R.id.reply_downvote);
            view.setTag(replyViewHolder);
        } else {
            replyViewHolder = (ReplyViewHolder) view.getTag();
        }
        replyViewHolder.vReplyText.setText(replyContainer.message);
        replyViewHolder.vAge.setText(replyContainer.age);
        replyViewHolder.vScore.setText(replyContainer.score);

        final int primaryColor = view.getResources().getColor(R.color.color_primary);
        int scoreColor = (replyContainer.voted == 0) ? Color.DKGRAY : primaryColor;
        replyViewHolder.vScore.setTextColor(scoreColor);

        int upvoteResource = (replyContainer.voted == 1) ? R.drawable.upvote_active : R.drawable.upvote;
        replyViewHolder.vUpvote.setBackgroundResource(upvoteResource);
        replyViewHolder.vUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replyContainer.voted != 0) return;
                replyContainer.voted = 1;
                PostService.vote(replyContainer.id, "up", appState.getUserHash(), new JsonHttpResponseHandler());
                ImageButton upvoteButton = (ImageButton) v;
                upvoteButton.setBackgroundResource(R.drawable.upvote_active);
                replyContainer.score = String.valueOf(Integer.parseInt(replyContainer.score) + 1);
                replyViewHolder.vScore.setText(replyContainer.score);
                replyViewHolder.vScore.setTextColor(primaryColor);
            }
        });

        int downvoteResource = (replyContainer.voted == -1) ? R.drawable.downvote_active : R.drawable.downvote;
        replyViewHolder.vDownvote.setBackgroundResource(downvoteResource);
        replyViewHolder.vDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replyContainer.voted != 0) return;
                replyContainer.voted = -1;
                PostService.vote(replyContainer.id, "down", appState.getUserHash(), new JsonHttpResponseHandler());
                ImageButton downvoteButton = (ImageButton) v;
                downvoteButton.setBackgroundResource(R.drawable.downvote_active);
                replyContainer.score = String.valueOf(Integer.parseInt(replyContainer.score) - 1);
                replyViewHolder.vScore.setText(replyContainer.score);
                replyViewHolder.vScore.setTextColor(primaryColor);
            }
        });

        return view;
    }

    public void updateData() {
        notifyDataSetChanged();
    }

    public static class ReplyViewHolder {
        public TextView vReplyText;
        public TextView vAge;
        public TextView vScore;
        public ImageButton vUpvote;
        public ImageButton vDownvote;
    }
}

