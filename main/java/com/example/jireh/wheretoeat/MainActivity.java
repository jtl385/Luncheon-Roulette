package com.example.jireh.wheretoeat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ArrayList<EditText> ListOfPlaces;
    Log log;
    RelativeLayout layout;
    String filename = "prevPlaces.txt";
    File file;
    PopupWindow pw;
    Toolbar toolbar;
    Vibrator vibrator;
    SharedPreferences sharedprefs;
    Toast currToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //set default values of settings, should only do once ever
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        ListOfPlaces = new ArrayList<>();
        layout = (RelativeLayout) findViewById(R.id.layout_one);
        file = new File(getFilesDir(), filename);
        sharedprefs = PreferenceManager.getDefaultSharedPreferences(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //fill screen with text fields
        for (int i=0; i<10; i++)
            newEditText(layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        if (id == R.id.action_help){
            //show the help window
            createHelpPop();
            return true;

        }

        return false;
    }

    public void addToList(EditText e) {
        ListOfPlaces.add(e);
    }

    public void newEditText(View view) {
        //add a new EditText field under the most recent one, as long as
        //there is less than 10 EditText fields already on screen.
        if (ListOfPlaces.size() <= 10) {
            EditText e = new EditText(this);
            final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (!ListOfPlaces.isEmpty()) {
                lparams.addRule(RelativeLayout.BELOW, ListOfPlaces.get(ListOfPlaces.size() - 1).getId());
            }
            else {
                lparams.addRule(RelativeLayout.BELOW, R.id.app_bar);
            }
            e.setLayoutParams(lparams);
            e.setHint("Enter the name of a restaurant...");
            layout.addView(e);
            addToList(e);
            e.setId(ListOfPlaces.size());
        }
        //if 10 EditText fields exist, show an error message
        else{
            Toast toast = Toast.makeText(this, "Can't add more than 10 fields", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void clearPlaces(View view) {
        for(int n = 0; n < ListOfPlaces.size(); n++) {
            layout.removeView(ListOfPlaces.get(n));
        }
        ListOfPlaces.clear();

        //re-fill screen with text fields
        for (int i=0; i<10; i++)
            newEditText(layout);
        if(currToast != null){
            currToast.cancel();
        }
        currToast = Toast.makeText(this, "Cleared all fields", Toast.LENGTH_SHORT);
        currToast.show();
        vibrateIfSet();
    }

    //chooses random place based on inputted choices, then displays message
    public void choosePlace(View view) {
        boolean goodList = false;
        for (int i = 0; i < ListOfPlaces.size(); i++) {
            if (ListOfPlaces.get(i).getText().toString().trim().length() != 0) {
                goodList = true;
                break;
            }
        }
        if (!goodList)
            return;

        String place = "";
        while (place.trim().length() == 0) {
            Random rand = new Random();
            int choice = rand.nextInt(ListOfPlaces.size());
            place = ListOfPlaces.get(choice).getText().toString();
        }

        //set dialog alert that asks you if you want directions after choosing for you
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Okay guys, we're going to...\n" + place);
        builder.setCancelable(false);
        final String finalPlace = place;
        builder.setNeutralButton(
                "open maps",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openMap(finalPlace);
                        writeToFile(getTime() + " - " + finalPlace + " \n");
                    }
                });
        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Added to history", Toast.LENGTH_SHORT).show();
                        writeToFile(getTime() + " - " + finalPlace + " \n");
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(
                "Redo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Did not add to history", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
        );

        AlertDialog alert = builder.create();
        alert.show();
        vibrateIfSet();
    }

    //This closes the keyboard window when tapping outside of the textbox.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;
    }

    //opens google maps, searching for chosen restaurant. stole code, no idea how it works lel
    public void openMap(String place) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + place + " restaurant");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    //opens a popup windo displaying a help message
    public void createHelpPop(){
        LayoutInflater inflater = getLayoutInflater();
        View popLayout = inflater.inflate(R.layout.help_pop, (ViewGroup) findViewById(R.id.helpPopLayout));
        pw = new PopupWindow(popLayout, 600, 400, true);
        pw.showAtLocation(popLayout, Gravity.CENTER,0, 0);
        Button closeButton = (Button) popLayout.findViewById(R.id.helpPopButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
    }

    //used to write to history text file
    public void writeToFile(String data){
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(data);
            writer.flush();
            writer.close();
            log.i("io", "successful write");
        } catch (Exception e){
            e.printStackTrace();
            log.i("io", "couldn't open file");
        }

    }

    public void openHistory(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    //used to store date for history
    public String getTime(){
        String retString;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, h:mm a");
        retString = df.format(c.getTime());

        return retString;
    }

    //vibrates phone if preferences allows
    public void vibrateIfSet(){
        log.i("vibrate", "started vibrateIfSet");
        if (sharedprefs.getBoolean("pref_vibrate", true))
            try {
                vibrator.vibrate(50);
            } catch(Exception e){
                e.printStackTrace();
            }
    }

    //easter egg
    public void martinInUp(View view) {
        vibrateIfSet();
        clearPlaces(view);
        for (EditText e : ListOfPlaces){
            e.setText("Chicken Lovers");
        }

    }
}
