package com.lo.jireh.wheretoeat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lo.jireh.wheretoeat.R;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main2Activity extends AppCompatActivity {

    String filename = "prevPlaces.txt";
    ArrayList<String> placeHistory;
    Log log;
    SharedPreferences sharedprefs;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedprefs = PreferenceManager.getDefaultSharedPreferences(this);

        applyTheme();
        setContentView(R.layout.activity_main2);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        colorToolbar();
        setSupportActionBar(toolbar);

        displayHistory();
    }

    public ArrayList<String> readFile() {
        File file = new File(getFilesDir(), filename);
        ArrayList<String> retlist = new ArrayList<>();
        try {
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                retlist.add(s.nextLine());
            }
            s.close();
        } catch (Exception e){
            e.printStackTrace();
            log.i("io", "failed to read");
        }
        return retlist;
    }

    public void displayHistory(){
        try {
            placeHistory = readFile();
        } catch(Exception e) {
            e.printStackTrace();
            log.i("io", "failed to assign placeHistory");
        }
        for (String word:placeHistory){
            log.i("placeHistory", word);
        }
        ListView listView = (ListView) findViewById(R.id.list_history);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.text_view, placeHistory);
        listView.setAdapter(adapter);
    }

    public void clearHistory(View view){
        try {
            File file = new File(getFilesDir(), filename);
            FileWriter writer = new FileWriter(file);
            writer.append("");
            writer.flush();
            writer.close();
            log.i("io", "successful write");
        } catch (Exception e){
            e.printStackTrace();
            log.i("io", "couldn't open file");
        }
        Toast.makeText(this, "History was cleared", Toast.LENGTH_SHORT).show();
        displayHistory();
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
            toolbar.setBackgroundColor(Color.rgb(87,193,234));
        else if (sharedprefs.getString("pref_theme", "").equals("Dark"))
            toolbar.setBackgroundColor(Color.BLUE);
    }
}
