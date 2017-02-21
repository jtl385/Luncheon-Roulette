package com.example.jireh.wheretoeat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Main2Activity extends AppCompatActivity {

    FileOutputStream fstream;
    String filename = "prevPlaces";
    Log log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        log.i("io", readFile());
        displayHistory();
    }

    public void writeToFile(String data){
        try {
            fstream = openFileOutput(filename, Context.MODE_PRIVATE);
            fstream.write(data.getBytes());
            fstream.close();
        } catch (Exception e){
            e.printStackTrace();
            log.i("io", "couldn't open file");
        }

    }
    public String readFile(){
        File file = new File(getFilesDir(), filename);
        int length = (int) file.length();

        byte[] bytes = new byte[length];
        try {
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.i("io", "failed to read");
        }

        String contents = new String(bytes);
        return contents;
    }

    public void displayHistory(){

    }
}
