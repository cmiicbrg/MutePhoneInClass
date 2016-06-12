package at.ac.brgenns.android.mutePhoneInClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent mutePhoneService = new Intent(context, MutePhoneService.class);
            mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.BOOT);
            context.startService(mutePhoneService);
        }
    }
}
