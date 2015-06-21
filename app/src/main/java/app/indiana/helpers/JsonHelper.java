package app.indiana.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.indiana.models.PostContainer;
import app.indiana.models.ReplyContainer;

/**
 * Created by chris on 11.06.2015.
 */
public class JsonHelper {

    public static ArrayList<ReplyContainer> parseReplies(JSONObject json) {
        JSONArray replies = json.optJSONArray("replies");
        ArrayList<ReplyContainer> replyArray = new ArrayList<>();
        for (int i = 0; i < replies.length(); i++) {
            JSONObject reply = new JSONObject();
            try {
                reply = (JSONObject) replies.get(i);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            replyArray.add(new ReplyContainer(reply));
        }
        return replyArray;
    }

    public static int countReplies(JSONObject json) {
        JSONArray replies = json.optJSONArray("replies");
        return replies.length();
    }

    public static PostContainer toPost(JSONObject jsonObject) {
        PostContainer postContainer = new PostContainer();
        postContainer.id = jsonObject.optString("id");
        postContainer.message = jsonObject.optString("message");
        postContainer.age = jsonObject.optString("age");
        postContainer.score = jsonObject.optString("score");
        postContainer.distance = jsonObject.optString("distance");
        postContainer.voted = jsonObject.optInt("voted");
        postContainer.replies = jsonObject.optInt("replies");
        return postContainer;
    }

}
