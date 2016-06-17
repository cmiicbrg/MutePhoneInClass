package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Intent;
import android.os.Bundle;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 28.05.2016.
 */
public class SoundProfileSettingsActivity extends AppCompatPreferenceActivity {

    private String settingID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        settingID = intent.getStringExtra(MuteSettingsActivity.SETTING_ID);

        setContentView(R.layout.activity_sound_profile_settings);

    }

    public String getSettingID() {
        return settingID;
    }



}
