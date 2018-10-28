package de.thm.ap.records;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
                android.R.layout.simple_list_item_activated_1, records);
        recordsListView.setAdapter(adapter);
        recordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), RecordFormActivity.class);
                i.putExtra("selected_record",records.get(position).getId());
                startActivity(i);
            }
        });
        RecordDAO recordDAO = new RecordDAO(this);
        Context context = this;
        recordsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        recordsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int itemsChecked = 0;
            List<Record> records_selected = new LinkedList<>();
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                if(checked) itemsChecked++;
                else itemsChecked--;
                actionMode.setTitle(itemsChecked + " ausgewählt");
                if(checked) records_selected.add(records.get(position));
                else records_selected.remove(records.get(position));
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.contextual_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_delete:
                        //deleteactions + Alert do you want do delete?
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Willst du die Leistungen wirklich löschen?");
                        builder.setNeutralButton(R.string.cancel,null);
                        builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(!recordDAO.deleteRecords(records_selected)) throw new RuntimeException("Löschen war nicht erfolgreich");
                                records.removeAll(records_selected);
                                actionMode.finish();
                            }
                        });

                        builder.show();
                        return true;
                    case R.id.action_email:
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Meine Leistungen " + records_selected.size());
                        StringBuilder emailText = new StringBuilder();
                        emailText.append("hier meine Leistungen:\n\n");
                        for(Record record : records_selected){
                            emailText.append(record.getModuleName()).append(" ")
                            .append(record.getModuleNum()).append(" ")
                            .append("(").append(record.getMark()).append("% ")
                            .append(record.getCrp()).append(" crp)").append("\n");
                        }
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailText.toString());
                        startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                //updates nachdem modus beendet wird
            }
        });
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
