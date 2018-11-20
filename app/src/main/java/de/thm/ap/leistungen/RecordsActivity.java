package de.thm.ap.leistungen;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import de.thm.ap.leistungen.Stats.Stats;
import de.thm.ap.leistungen.Stats.StatsLiveData;
import de.thm.ap.leistungen.Stats.StatsTask;
import de.thm.ap.leistungen.data.AppDatabase;
import de.thm.ap.leistungen.data.ModuleDAO;
import de.thm.ap.leistungen.model.Module;
import de.thm.ap.leistungen.model.Record;

public class RecordsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private List<Record> records = null;
    ArrayAdapter<Record> adapterRecord = null;
    private ListView recordsListView = null;
    // Verschönerung Statistik als Header in Liste
    private boolean USE_HEADER_VIEW = false;
    private View headerView;
    private TextView recordCount;
    private Stats statistics = null;
    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!Intent.ACTION_PICK.equals(getIntent().getAction())) {
            getMenuInflater().inflate(R.menu.records, menu);

            MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
            searchViewMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    recordsListView.removeHeaderView(headerView);
                    return true;
                }
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    recordsListView.addHeaderView(headerView, null, true);
                    return true;
                }
            });
            searchView = (SearchView) searchViewMenuItem.getActionView();
            searchView.setQueryHint("Leistung suchen...");
            searchView.setOnQueryTextListener(this);
            return true;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        recordsListView = findViewById(R.id.records_list);
        recordsListView
                .setEmptyView(findViewById(R.id.records_list_empty));

        recordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Statistik-Header
                if (recordsListView.getHeaderViewsCount() == 1 && position == 0) {
                    showStatsDialog();
                    return;
                }
                String action = getIntent().getAction();
                // Aufruf aus anderer APP ----------------------------------------------------------
                if(Intent.ACTION_PICK.equals(action)){
                    Uri uri = Uri.parse("content://de.thm.ap.records.cp/records/"+
                            ((Record) recordsListView.getItemAtPosition(position)).getId());
                    setResult(RESULT_OK,new Intent().setData(uri));
                    finish();
                } else { // ------------------------------------------------------------------------
                    Intent i = new Intent(view.getContext(), RecordFormActivity.class);
                    int pos = ((Record) recordsListView.getItemAtPosition(position)).getId();
                    i.putExtra("selected_record",pos);
                    startActivity(i);
                }

            }
        });
        if(!Intent.ACTION_PICK.equals(getIntent().getAction())){
            recordsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            recordsListView.setMultiChoiceModeListener(new RecordsChoiceModeListener(this, USE_HEADER_VIEW));
            ImageButton addImageButton = findViewById(R.id.add_record_imagebutton);
            addImageButton.setOnClickListener(v -> {
                Intent i = new Intent(v.getContext(), RecordFormActivity.class);
                startActivity(i);
            });
            // Statistik-Header
            if(USE_HEADER_VIEW) {
                headerView = getLayoutInflater().inflate(R.layout.record_list_header, null);
                recordsListView.addHeaderView(headerView);
                recordCount = headerView.findViewById(R.id.record_count);
                LinearLayout show_details = findViewById(R.id.show_details);
                TextView progress_text = findViewById(R.id.progress_text);
                ProgressBar averageMarkProgressBar = findViewById(R.id.average_mark_progressBar);
                TextView averageMark = findViewById(R.id.average_mark);

                new StatsLiveData(AppDatabase.getDb(this).recordDAO().findAll()).observe(this, stats -> {
                    statistics = stats;
                    if (stats != null) {
                        // new stats ready
                        averageMarkProgressBar.setVisibility(View.GONE);
                        averageMark.setText("" + stats.getAverageMark() + "%");
                        averageMark.setVisibility(View.VISIBLE);
                        progress_text.setVisibility(View.GONE);
                        show_details.setVisibility(View.VISIBLE);
                        recordsListView.removeHeaderView(headerView);
                        recordsListView.addHeaderView(headerView, null, true);
                    } else {
                        // calculating new stats
                        averageMarkProgressBar.setVisibility(View.VISIBLE);
                        averageMark.setVisibility(View.GONE);
                        show_details.setVisibility(View.GONE);
                        progress_text.setVisibility(View.VISIBLE);
                        recordsListView.removeHeaderView(headerView);
                        recordsListView.addHeaderView(headerView, null, false);
                    }
                });
            }
            ModuleDAO moduleDAO = new ModuleDAO(getApplicationContext());
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getActiveNetworkInfo() != null) {
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build();
                PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                        .Builder(UpdateModulesWorker.class,30, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .build();
                WorkManager.getInstance().
                        enqueueUniquePeriodicWork("update modules",
                                ExistingPeriodicWorkPolicy.KEEP, workRequest);
            } else if(moduleDAO.findAll().size() == 0){
                // keine Internet Verbindung und erster Aufruf lade raw modules.json
                InputStream rawModules = getBaseContext().getResources().openRawResource(R.raw.modules);
                Module[] modules = new Gson().fromJson(new InputStreamReader(rawModules), Module[].class);
                moduleDAO.persistAll(modules);
            }
        }


        // Ue4--------------------------------------------------------------------------------------

        // TODO Failure abfangen raw json- datei laden
    }

    @Override
    public void onStart(){
        super.onStart();
        // Datenbank -------------------------------------------------------------------------------
        Room.databaseBuilder(this, AppDatabase.class, "app-database").build();
        AppDatabase.getDb(this).recordDAO().findAll().observe(this,
                records -> {
                    this.records = records;
                    Collections.sort(records);
                    adapterRecord = new RecordArrayAdapter(this, records);
                    recordsListView.setAdapter(adapterRecord);
                    if(USE_HEADER_VIEW && !Intent.ACTION_PICK.equals(getIntent().getAction()))
                        recordCount.setText(String.format(getString(R.string.header_avarage_mark), records.size()));
                }
        );
        //------------------------------------------------------------------------------------------
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_stats:
                new StatsTask(this).execute(records);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String action = getIntent().getAction();
        if(Intent.ACTION_PICK.equals(action)){
            setResult(RESULT_CANCELED);
            finish();
        } else super.onBackPressed();
    }

    public void showStatsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.stats)
                .setMessage(statistics.toString())
                .setNeutralButton(R.string.close, null)
                .show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // close keyboard
        searchView.clearFocus();
        return true;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterRecord.getFilter().filter(newText);
        return true;
    }
}
