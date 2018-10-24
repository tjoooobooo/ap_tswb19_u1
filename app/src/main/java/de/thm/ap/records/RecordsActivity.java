package de.thm.ap.records;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import de.thm.ap.records.model.Record;

public class RecordsActivity extends AppCompatActivity {

    private ListView recordsListView;
    private List<Record> records = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        recordsListView = findViewById(R.id.records_list);
        recordsListView
                .setEmptyView(findViewById(R.id.records_list_empty));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.records, menu);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        records = new RecordDAO(this).findAll();
        ArrayAdapter<Record> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, records);
        recordsListView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Intent i = new Intent(this, RecordFormActivity.class);
                startActivity(i);
                return true;
            case R.id.action_stats:
                // TODO Statistik
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                Stats stats = new Stats(records);
                builder.setTitle(R.string.stats);
                builder.setMessage(
                        "Leistungen " + records.size()+"\n"+
                        "50% Leistungen " + stats.getSumHalfWeighted()+"\n"+
                        "Summe Crp " + stats.getSumCrps()+"\n"+
                        "Crp bis Ziel " + stats.getCrpToEnd()+"\n"+
                        "Durchschnitt " + stats.getAverageMark() + "%");
                builder.setNeutralButton(R.string.close,null);
                builder.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
