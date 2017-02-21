package com.example.jireh.wheretoeat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ArrayList<EditText> ListOfPlaces;
    Log log;
    RelativeLayout layout;
    FileOutputStream fstream;
    String filename = "prevPlaces";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListOfPlaces = new ArrayList<EditText>();
        layout = (RelativeLayout) findViewById(R.id.layout_one);

    }
    public void addToList(EditText e) {
        ListOfPlaces.add(e);
    }
    public void removeFromList(EditText e){
        ListOfPlaces.remove(e);
        log.i("list", "Removed from list");
    }
    public void newEditText(View view) {
        //add a new EditText field under the most recent one, as long as
        //the there is less than 10 EditText fields already on screen.
        if (ListOfPlaces.size() <= 10) {
            EditText e = new EditText(this);
            final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (!ListOfPlaces.isEmpty()) {
                lparams.addRule(RelativeLayout.BELOW, ListOfPlaces.get(ListOfPlaces.size() - 1).getId());
            }
            e.setLayoutParams(lparams);
            e.setHint("Enter the name of a restaurant...");
            layout.addView(e);
            addToList(e);
            e.setId(ListOfPlaces.size());
        }
        //if 10 EditText fields exist, show an error message
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Can't add more than 10 places");
            builder.setCancelable(true);
            builder.setNeutralButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    public void clearPlaces(View view) {
        for(int n = 0; n < ListOfPlaces.size(); n++) {
            layout.removeView(ListOfPlaces.get(n));
        }
        ListOfPlaces.clear();
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

        writeToFile(place + "\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Okay guys, we're going to...\n" + place);
        builder.setCancelable(true);
        final String finalPlace = place;
        builder.setNeutralButton(
                "Get directions",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openMap(finalPlace);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //This closes the keyboard window when tapping outside of the textbox.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    //opens google maps, searching for chosen restaurant. stole code, no idea how it works lel
    public void openMap(String place) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + place + " restaurant");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void writeToFile(String data){
        try {
            fstream = openFileOutput(filename, Context.MODE_PRIVATE);
            fstream.write(data.getBytes());
            fstream.close();
            log.i("io", "successful write");
        } catch (Exception e){
            e.printStackTrace();
            log.i("io", "couldn't open file");
        }

    }
    public ArrayList<String> readFile() {
        File file = new File(getFilesDir(), filename);
        ArrayList<String> retlist = new ArrayList<>();
        try {
            Scanner s = new Scanner(file);
            while (s.hasNext()) {
                retlist.add(s.next());
            }
            s.close();
        } catch (Exception e){
            e.printStackTrace();
            log.i("io", "failed to read");
        }
        return retlist;
    }

    public void openHistory(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
