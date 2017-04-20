package com.lo.jireh.wheretoeat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.lo.jireh.wheretoeat.R;


public class PrefsFragment extends PreferenceFragment {

    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences sharedprefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedprefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //create listener
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //change summary of theme preference when user changes it
                if (key.equals("pref_theme")) {
                    Preference pref = findPreference(key);
                    pref.setSummary(sharedPreferences.getString(key, ""));
                }
            }
        };
        sharedprefs.registerOnSharedPreferenceChangeListener(listener);

        //set summary of background theme preference
        Preference pref_theme = findPreference("pref_theme");
        pref_theme.setSummary(sharedprefs.getString("pref_theme", ""));
    }

}
