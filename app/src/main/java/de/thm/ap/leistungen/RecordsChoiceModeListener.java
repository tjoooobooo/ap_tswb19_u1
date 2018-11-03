package de.thm.ap.leistungen;

import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import de.thm.ap.leistungen.data.AppDatabase;
import de.thm.ap.leistungen.model.Record;

public class RecordsChoiceModeListener implements AbsListView.MultiChoiceModeListener {
    private Context context = null;
    private List<Record> records = null;
    private int itemsChecked = 0;
    private List<Record> records_selected = new LinkedList<>();
    private boolean delete_finished = false;
    RecordsChoiceModeListener(Context context) {
        this.context = context;
        AppDatabase.getDb(context).recordDAO().findAll().observe((LifecycleOwner) context,
                records -> {
                    this.records = records;
                }
        );
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
        if(checked) itemsChecked++;
        else itemsChecked--;
        actionMode.setTitle(itemsChecked + " ausgewählt");
        //TODO funktioniert löschen immer richtig? wird das richtige gelöscht wenn ids durcheinander
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
        return true;
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
                            Thread deleteThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for(Record r : records_selected) {
                                        AppDatabase.getDb(context).recordDAO().delete(r);
                                    }
                                }
                            });
                            deleteThread.start();
                        try {
                            //waiting for Tread to finish
                            deleteThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        actionMode.finish();
                    }
                });
                builder.show();
                return true;
            case R.id.action_email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Meine Leistungen " + records_selected.size());
                StringBuilder emailText = new StringBuilder();
                emailText.append("hier meine Leistungen:\n\n");
                for(Record record : records_selected){
                    emailText.append(record.toString()).append("\n");
                }
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailText.toString());
                context.startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        itemsChecked = 0;
        records_selected.clear();
    }
}
