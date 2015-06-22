package app.indiana.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import app.indiana.MainActivity;
import app.indiana.R;

/**
 * Created by chris on 22.06.2015.
 */
public class NotificationService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder notifictationBuilder = new NotificationCompat.Builder(context);
        notifictationBuilder.setContentTitle("Hello World");
        notifictationBuilder.setContentText("Yo");
        notifictationBuilder.setSmallIcon(R.drawable.ic_indiana);

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notifictationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notifictationBuilder.build());
    }
}
