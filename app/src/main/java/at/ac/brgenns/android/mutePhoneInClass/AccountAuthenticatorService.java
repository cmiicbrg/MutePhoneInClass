package at.ac.brgenns.android.mutePhoneInClass;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Christoph on 22.06.2016.
 */
public class AccountAuthenticatorService extends Service {
    public static final String AUTH_TYPE = "at.ac.brgenns.android.mutePhoneInClass";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
