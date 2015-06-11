package app.indiana.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import app.indiana.R;
import app.indiana.helpers.JsonHelper;
import app.indiana.models.ReplyContainer;

/**
 * Created by chris on 11.06.2015.
 */
public class ReplyAdapter extends ArrayAdapter<ReplyContainer>{

    public ReplyAdapter(Context context, ArrayList<ReplyContainer> replies) {
        super(context, R.layout.row_reply, replies);
    }

    public ReplyAdapter(Context context, JSONObject json) {
        this(context, JsonHelper.parseReplies(json));
        Log.d("JSON", json.toString());
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d("getView", "OK");
        ReplyContainer replyContainer = getItem(position);
        ReplyViewHolder replyViewHolder;

        if (view == null) {
            Log.d("view", "null");
            replyViewHolder = new ReplyViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.row_reply, parent, false);
            replyViewHolder.vReplyText = (TextView) view.findViewById(R.id.reply_text);
            view.setTag(replyViewHolder);
        } else {
            replyViewHolder = (ReplyViewHolder) view.getTag();
        }
        Log.d("message", replyContainer.message);
        replyViewHolder.vReplyText.setText(replyContainer.message);

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

