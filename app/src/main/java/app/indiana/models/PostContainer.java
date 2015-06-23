package app.indiana.models;

import java.io.Serializable;

/**
 * Created by chris on 07.05.2015.
 */
public class PostContainer implements Serializable {
    public String id;
    public String message;
    public String age;
    public String score;
    public String distance;
    public int voted;
    public int replies;
}
