package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 28.05.2016.
 */
public class WifiSettingsActivity extends AppCompatPreferenceActivity {

    private String settingID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        settingID = intent.getStringExtra(MuteSettingsActivity.SETTING_ID);

        setContentView(R.layout.activity_wifi_settings);

    }

    public String getSettingID() {
        return settingID;
    }



}
