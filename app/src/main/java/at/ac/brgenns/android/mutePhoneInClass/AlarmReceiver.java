package at.ac.brgenns.android.mutePhoneInClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by android on 14.04.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(AlarmReceiver.class.getSimpleName(), "Scheduled alarm received");
         Intent service = new Intent(context, MutePhoneService.class);
        service.putExtra(MutePhoneService.TASK,
                intent.getIntExtra(MutePhoneService.TASK, MutePhoneService.ALARM));
        context.startService(service);
    }
}
