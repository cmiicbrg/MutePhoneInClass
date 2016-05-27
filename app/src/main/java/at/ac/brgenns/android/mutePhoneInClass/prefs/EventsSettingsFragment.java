package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class EventsSettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mute_settings);

    }

    @Override
    public void onResume() {
        super.onResume();
        buildUI();
    }

    private void buildUI() {
        final PreferenceScreen root = getPreferenceScreen();
        root.removeAll();
        final Preference p = new Preference(getActivity());
        p.setIcon(R.drawable.ic_add_black_24dp);
        p.setTitle(R.string.add_rule);
        p.setPersistent(false);
        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                MetricsLogger.action(mContext, MetricsLogger.ACTION_ZEN_ADD_RULE);
//                showAddRuleDialog();
                return true;
            }
        });
        root.addPreference(p);
    }
}
