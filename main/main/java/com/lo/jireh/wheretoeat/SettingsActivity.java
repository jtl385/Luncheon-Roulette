package com.lo.jireh.wheretoeat;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.lo.jireh.wheretoeat.R;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    PrefsFragment pf;
    SharedPreferences sharedprefs;
    Log log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pf = new PrefsFragment();
        sharedprefs = PreferenceManager.getDefaultSharedPreferences(this);

        applyTheme();
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        colorToolbar();
        setSupportActionBar(toolbar);

        // Display the prefs frag in the FrameLayout
        getFragmentManager().beginTransaction()
                .replace(R.id.pref_content, pf)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return true;
    }

    public void applyTheme(){
        try {
            if (sharedprefs.getString("pref_theme", "").equals("Light")) {
                setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
            } else if (sharedprefs.getString("pref_theme", "").equals("Dark")) {
                setTheme(R.style.Theme_AppCompat_NoActionBar);
            }
            else
                log.i("theme", "something went wrong, theme setting is " +
                        sharedprefs.getString("pref_theme", ""));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void colorToolbar(){
        if (sharedprefs.getString("pref_theme", "").equals("Light"))
            toolbar.setBackgroundColor(Color.CYAN);
        else if (sharedprefs.getString("pref_theme", "").equals("Dark"))
            toolbar.setBackgroundColor(Color.BLUE);
    }
}
