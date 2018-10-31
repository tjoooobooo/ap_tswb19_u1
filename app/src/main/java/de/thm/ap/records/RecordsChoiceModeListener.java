package de.thm.ap.records;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;

import java.util.LinkedList;
import java.util.List;

import de.thm.ap.records.model.Record;

public class RecordsChoiceModeListener implements AbsListView.MultiChoiceModeListener {
    private RecordDAO recordDAO = null;
    private Context context = null;
    private List<Record> records = null;
    RecordsChoiceModeListener(Context context){
        this.context = context;
        recordDAO = new RecordDAO(context);
        records = recordDAO.findAll();
    }
    private int itemsChecked = 0;
    private List<Record> records_selected = new LinkedList<>();
    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
        if(checked) itemsChecked++;
        else itemsChecked--;
        actionMode.setTitle(itemsChecked + " ausgewählt");
        if(checked) records_selected.add(records.get(position-1));
        else records_selected.remove(records.get(position-1));
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
                context.startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        //updates nachdem modus beendet wird
        itemsChecked = 0;
        records_selected = new LinkedList<>();
    }
}
