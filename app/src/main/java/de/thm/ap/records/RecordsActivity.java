package de.thm.ap.records;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import de.thm.ap.records.model.Record;

public class RecordsActivity extends AppCompatActivity {

    private ListView recordsListView;
    private List<Record> records = null;
    ArrayAdapter<Record> adapterRecord = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.records, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        recordsListView = findViewById(R.id.records_list);
        recordsListView
                .setEmptyView(findViewById(R.id.records_list_empty));

        records = new RecordDAO(this).findAll();
        adapterRecord = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, records);
        recordsListView.setAdapter(adapterRecord);
        recordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), RecordFormActivity.class);
                i.putExtra("selected_record",records.get(position).getId());
                startActivity(i);
            }
        });
        recordsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        recordsListView.setMultiChoiceModeListener(new RecordsChoiceModeListener(this));
    }

    @Override
    public void onResume(){
        super.onResume();
        adapterRecord.clear();
        records = new RecordDAO(this).findAll();
        adapterRecord.addAll(records);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Intent i = new Intent(this, RecordFormActivity.class);
                startActivity(i);
                return true;
            case R.id.action_stats:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.stats);
                Stats stats = new Stats(records);
                builder.setMessage(stats.toString());
                builder.setNeutralButton(R.string.close,null);
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
