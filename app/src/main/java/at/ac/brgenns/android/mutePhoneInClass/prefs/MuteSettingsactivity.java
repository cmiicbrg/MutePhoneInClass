package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

import at.ac.brgenns.android.mutePhoneInClass.R;

public class MuteSettingsActivity extends PreferenceActivity {
    List<ScanResult> wifisFoundList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mute_settings);

    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return WifiSettingsFragment.class.equals(fragmentName) ||
                EventsSettingsFragment.class.equals(fragmentName);
    }

}
