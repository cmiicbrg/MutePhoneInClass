package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class WifiSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_event_settings);
    }
}