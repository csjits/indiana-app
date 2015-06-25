package app.indiana.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
                postCacheService.cache("my", response);
            }
        });
    }

    private void showMyPostActivityNotification(Context context, PostContainer post, int change) {

        Resources res = context.getResources();
        String title = res.getString(R.string.notification_my_title_new) + " ";
        if (change % (PostCacheService.SCORE_DIFF_MULTI * PostCacheService.REPLIES_DIFF_MULTI) == 0) {
            title += res.getString(R.string.notification_my_title_ratings_replies);
        } else if (change % PostCacheService.SCORE_DIFF_MULTI == 0) {
            title += res.getString(R.string.notification_my_title_ratings);
        } else if (change % PostCacheService.REPLIES_DIFF_MULTI == 0) {
            title += res.getString(R.string.notification_my_title_replies);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.notification_icon_large);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title);
        style.bigText(post.message);
        style.setSummaryText(
                res.getString(R.string.notification_my_summary_score, post.score)
                        + " \t "
                        + res.getString(R.string.notification_my_summary_replies, post.replies));

        NotificationCompat.Builder notifictationBuilder = new NotificationCompat.Builder(context)
            .setStyle(style)
            .setContentTitle(title)
            .setContentText(post.message)
            .setSmallIcon(R.mipmap.notification_icon_small)
            .setLargeIcon(largeIcon)
            .setAutoCancel(true)
            .setVibrate(new long[]{0, 100, 100, 100})
            .setColor(res.getColor(R.color.color_primary))
            .setLights(Color.CYAN, 400, 2000)
            .setContentIntent(buildIntent(context, MainActivity.class));

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
