package app.indiana.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import app.indiana.Indiana;
import app.indiana.MainActivity;
import app.indiana.R;
import app.indiana.helpers.JsonHelper;
import app.indiana.models.PostContainer;

/**
 * Created by chris on 22.06.2015.
 */
public class NotificationService extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Indiana appState = (Indiana) context.getApplicationContext();
        Location loc = appState.getUserLocation().getLastLocation();
        PostService.get(loc.getLongitude(), loc.getLatitude(), "my", appState.getUserHash(),
                new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONArray response) {
                PostCacheService postCacheService = new PostCacheService(context);
                postCacheService.load("my");
                ArrayList<PostContainer> posts = JsonHelper.toPostList(response);
                ArrayList<Integer> diffs = postCacheService.diff("my", posts);
                for (int i = 0; i < diffs.size(); i++) {
                    if (diffs.get(i) > 1) {
                        showMyPostActivityNotification(context, posts.get(i), diffs.get(i));
                    }
                }
            }
        });
    }

    private void showMyPostActivityNotification(Context context, PostContainer post, int change) {
        NotificationCompat.Builder notifictationBuilder = new NotificationCompat.Builder(context);

        String title = "New ";
        if (change % (PostCacheService.SCORE_DIFF_MULTI * PostCacheService.REPLIES_DIFF_MULTI) == 0) {
            title += "replies and ratings";
        } else if (change % PostCacheService.SCORE_DIFF_MULTI == 0) {
            title += "ratings";
        } else if (change % PostCacheService.REPLIES_DIFF_MULTI == 0) {
            title += "replies";
        }

        String message = (post.message.length() > 50) ? post.message.substring(0, 50)+"…" : post.message;

        notifictationBuilder.setContentTitle(title);
        notifictationBuilder.setContentText("\"" + message + "\"\r\nScore: " + post.score + "\r\nReplies: " + post.replies);
        notifictationBuilder.setSmallIcon(R.drawable.ic_indiana);
        notifictationBuilder.setContentIntent(buildIntent(context, MainActivity.class));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(post.id, 1, notifictationBuilder.build());
    }

    private static PendingIntent buildIntent(Context context, Class<?> activity) {
        Intent resultIntent = new Intent(context, activity);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(activity);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
