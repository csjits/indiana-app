package app.indiana;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
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

        post.id = jsonObject.optString("_id");
        post.message = jsonObject.optString("message");

        DateTimeFormatter dtfParser = ISODateTimeFormat.dateTime();
        DateTime dtPost = dtfParser.parseDateTime(jsonObject.optString("date"));
        DateTime dtCurrent = new DateTime();
        Duration duration = new Duration(dtPost, dtCurrent);
        String createdString = "";
        if (duration.getStandardDays() > 0) {
            createdString = duration.getStandardDays() + "d";
        } else if (duration.getStandardHours() > 0) {
            createdString = duration.getStandardHours() + "h";
        } else if (duration.getStandardMinutes() > 0) {
            createdString = duration.getStandardMinutes() + "m";
        } else if (duration.getStandardSeconds() > 0) {
            createdString = duration.getStandardSeconds() + "s";
        }
        post.created = createdString;

        post.score = jsonObject.optString("score");
        JSONObject loc = jsonObject.optJSONObject("loc");
        if (loc != null) post.distance = loc.optString("lat") + "," + loc.optString("long");

        postViewHolder.vMessage.setText(post.message);
        postViewHolder.vCreated.setText(post.created);
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
        public TextView vCreated;
        public TextView vScore;
        public TextView vDistance;
        public Button vUpvote;
        public Button vDownvote;

        public PostViewHolder(View v) {
            super(v);
            vMessage = (TextView) v.findViewById(R.id.post_message);
            vCreated = (TextView) v.findViewById(R.id.post_date);
            vScore = (TextView) v.findViewById(R.id.post_score);
            vDistance = (TextView) v.findViewById(R.id.post_distance);
            vUpvote = (Button) v.findViewById(R.id.post_upvote);
            vDownvote = (Button) v.findViewById(R.id.post_downvote);
        }
    }
}
