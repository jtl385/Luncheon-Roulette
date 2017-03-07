package com.example.jireh.wheretoeat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main2Activity extends AppCompatActivity {

    String filename = "prevPlaces.txt";
    ArrayList<String> placeHistory;
    Log log;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        displayHistory();
    }

    protected void onPause(Bundle savedInstanceState){
        finish();
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
}
