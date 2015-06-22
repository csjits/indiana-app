package app.indiana.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chris on 22.06.2015.
 */
public class BackgroundDataService {

    Context mContext;
    AlarmManager mAlarmManager;
    PendingIntent mAlarmIntent;

    public BackgroundDataService(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setIntent(Class<?> receiver) {
        Intent intent = new Intent(mContext, receiver);
        mAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
    }

    public void run(long interval) {
        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, interval, interval,
                mAlarmIntent);
    }

    public void stop() {
        if (mAlarmManager != null) {
            mAlarmManager.cancel(mAlarmIntent);
        }
    }

}
