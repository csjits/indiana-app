package app.indiana.services;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import app.indiana.helpers.JsonHelper;
import app.indiana.models.PostContainer;

/**
 * Created by chris on 21.06.2015.
 */
public class PostCacheService {

    public static int SCORE_DIFF_MULTI = 2;
    public static int REPLIES_DIFF_MULTI = 3;
    public String[] postTypes = {"hot", "new", "my"};

    private Context mContext;
    private HashMap<String, ArrayList<PostContainer>> mPostCache;

    public PostCacheService(Context context) {
        mContext = context;
        mPostCache = new HashMap<>();
    }

    public void load() {
        for (String postType : postTypes) {
            mPostCache.put(postType, read(mContext, postType));
        }
    }

    public void save() {
        for (String postType : postTypes) {
            write(mContext, mPostCache.get(postType), postType);
        }
    }

    public void cache(String postType, ArrayList<PostContainer> posts) {
        mPostCache.put(postType, posts);
    }

    public void cache(String postType, JSONArray posts) {
        ArrayList<PostContainer> postContainers = new ArrayList<>();
        for (int i = posts.length(); i < posts.length(); i++) {
            try {
                postContainers.add(JsonHelper.toPost(posts.getJSONObject(i)));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        cache(postType, postContainers);
    }

    public ArrayList<Integer> diff(String postType, ArrayList<PostContainer> postsNew) {
        ArrayList<PostContainer> postsCached = mPostCache.get(postType);
        ArrayList<Integer> diff = new ArrayList<>();

        for (PostContainer postCached : postsCached) {

            int diffVal = 1;
            for (PostContainer postNew : postsNew) {

                if (postCached.id.equals(postNew.id)) {
                    if (!postCached.score.equals(postNew.score)) diffVal *= SCORE_DIFF_MULTI;
                    if (postCached.replies != postNew.replies) diffVal *= REPLIES_DIFF_MULTI;
                    break;
                }
            }
            diff.add(diffVal);
        }

        return diff;
    }

    private static ArrayList<PostContainer> read(Context context, String postType) {
        Object myPosts = ObjectFileService.readObjectFromFile(context, postType+".cache");
        try {
            return (ArrayList<PostContainer>) myPosts;
        } catch (ClassCastException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static void write(Context context, ArrayList<PostContainer> posts, String postType) {
        ObjectFileService.witeObjectToFile(context, posts, postType+".cache");
    }

}
