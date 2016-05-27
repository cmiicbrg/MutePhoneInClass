package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeMap;

import at.ac.brgenns.android.mutePhoneInClass.R;
import at.ac.brgenns.android.mutePhoneInClass.prefs.db.PreferenceDataSource;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.SoundProfile;

public class MPICPrefs extends ListActivity {

    private PreferenceDataSource datasource;
    private TreeMap<Long, SoundProfile> soundprofiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpicprefs);

        final ListView prefsList = getListView();
//startPreferenceFragment(PreferenceFragment()  {
//},true);
//        WifiEventPreferenceFragment wifiEventPreferenceFragment = new WifiEventPreferenceFragment();
//        startPreferenceFragment(wifiEventPreferenceFragment,true);
        datasource = new PreferenceDataSource(this);
        datasource.open();

        soundprofiles = datasource.getAllSoundProfiles();
        PrefsListAdapter adapter =
                new PrefsListAdapter(this, new ArrayList(soundprofiles.values()));
        setListAdapter(adapter);

        prefsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Object item = parent.getItemAtPosition(position);
                if (item instanceof SoundProfile) {
                    Toast.makeText(getApplicationContext(), "SoundProfile " + position + " " + id,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

//    public static class WifiEventPreferenceFragment extends PreferenceFragment {
//
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            PreferenceScreen wifi = getPreferenceScreen();
//
//
//        }
//    }
}
