package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.lang.annotation.Target;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 28.05.2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class KusssSettingsActivity extends AppCompatPreferenceActivity {

    private String settingID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        settingID = intent.getStringExtra(MuteSettingsActivity.SETTING_ID);

        setContentView(R.layout.activity_kusss_settings);

    }

    public String getSettingID() {
        return settingID;
    }



}
