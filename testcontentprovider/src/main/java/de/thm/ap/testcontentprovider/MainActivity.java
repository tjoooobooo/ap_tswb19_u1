package de.thm.ap.testcontentprovider;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private String[] projection = {"id","moduleName"};
    private ArrayAdapter<Record> adapter = null;
    private List<Record> records = new ArrayList<>();
    private ContentResolver cr = null;
    private Uri uri = Uri.parse("content://de.thm.ap.records.cp/records");
    private ListView recordsView = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cr = getContentResolver();
        recordsView = findViewById(R.id.recordslist);
        records.clear();
    }
    @Override
    protected  void onResume(){
        super.onResume();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,records);
        ListView recordsView = findViewById(R.id.recordslist);
        recordsView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("record/*");
                startActivityForResult(intent,1);
                return true;
            case R.id.get_all_records:
                getAllRecords(uri);
                return true;
            case R.id.file_download:
                readCSVFile(uri);
                return true;
            case R.id.action_delete:
                records.clear();
                recordsView.setAdapter(adapter);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void getAllRecords(Uri uri){
        Cursor c = cr.query(uri, projection, null, null, "moduleName");
        if (c != null) {
            while (c.moveToNext()) {
                Log.i(TAG, "id: " + c.getLong(0) + " module name: " + c.getString(1));
                records.add(new Record(c.getLong(0),c.getString(1)));
            }
            recordsView.setAdapter(adapter);
        }
    }

    void readCSVFile(Uri uri){
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case -1:
                Uri contactData = data.getData();
                getRecordFromUri(contactData);
                break;
            case 0:
                break;
        }
    }

    protected void getRecordFromUri(Uri uri){
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(uri, projection, null, null, "moduleName");
        if (c != null) {
            while (c.moveToNext()) {
                Log.i(TAG, "id: " + c.getLong(0) + " module name: " + c.getString(1));
                records.add(new Record(c.getLong(0),c.getString(1)));
            }
            recordsView.setAdapter(adapter);
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
