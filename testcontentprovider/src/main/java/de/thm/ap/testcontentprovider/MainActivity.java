package de.thm.ap.testcontentprovider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected  void onResume(){
        super.onResume();
        // Get data from content provider
        Uri uri = Uri.parse("content://de.thm.ap.records.cp/records");
        ContentResolver cr = getContentResolver();
        String[] projection = {"id", "moduleName"};
        Cursor c = cr.query(uri, projection, null, null, "moduleName");
        List<Record> records = new ArrayList<>();

        if (c != null) {
            while (c.moveToNext()) {
                Log.i(TAG, "id: " + c.getLong(0) + " module name: " + c.getString(1));
                records.add(new Record(c.getLong(0),c.getString(1)));
            }
        }
        ArrayAdapter<Record> adapter = null;
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,records);
        ListView recordsView = findViewById(R.id.recordslist);
        recordsView.setAdapter(adapter);

        // READ CSV-File----------------------------------------------------------------------------
        try {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(uri,"r");
            FileReader fileReader = new FileReader(pfd.getFileDescriptor());
            CSVReader csvReader = new CSVReader(fileReader);
            try {
                List<String[]> csvFile = csvReader.readAll();
                for(String[] s : csvFile){
                    Log.d(TAG,"CSV: " + Arrays.toString(s));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public class Record{
        long id;
        String name;
        Record(long id, String name){
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString(){
            return "ID: "+id+", name: " + name;
        }
    }
}
