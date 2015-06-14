package app.indiana.models;

import org.json.JSONObject;

/**
 * Created by chris on 11.06.2015.
 */
public class ReplyContainer {
    public String id;
    public String message;
    public String age;
    public String score;
    public int voted;

    public ReplyContainer(JSONObject json) {
        id = json.optString("id");
        message = json.optString("message");
        age = json.optString("age");
        score = json.optString("score");
        voted = json.optInt("voted");
    }
}
