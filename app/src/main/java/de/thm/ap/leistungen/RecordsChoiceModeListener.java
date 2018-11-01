package de.thm.ap.leistungen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;

import java.util.LinkedList;
import java.util.List;

import de.thm.ap.leistungen.data.RecordFileDAO;
import de.thm.ap.leistungen.model.Record;

public class RecordsChoiceModeListener implements AbsListView.MultiChoiceModeListener {
    private Context context = null;
    private List<Record> records = null;
    private int itemsChecked = 0;
    private List<Record> records_selected = new LinkedList<>();
    private boolean isDeleted = false;
    RecordsChoiceModeListener(Context context){
        this.context = context;
    }

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
        records = new RecordFileDAO(context).findAll();
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
                        if(!new RecordFileDAO(context).deleteRecords(records_selected)) throw new RuntimeException("Löschen war nicht erfolgreich");
                        records.removeAll(records_selected);
                        isDeleted = true;
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
        //updates nachdem modus beendet wird
        if(isDeleted) new RecordFileDAO(context).revertIsChanged();
        isDeleted = false;
        itemsChecked = 0;
        records_selected = new LinkedList<>();
        AppCompatActivity compat = (AppCompatActivity)context;
        compat.recreate();
    }
}
