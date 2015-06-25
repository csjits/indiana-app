package app.indiana.services;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chris on 26.06.2015.
 */
public class BootService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            BackgroundDataService backgroundDataService = new BackgroundDataService(context);
            backgroundDataService.setIntent(NotificationService.class);
            backgroundDataService.run(AlarmManager.INTERVAL_HOUR);
        }
    }
}
