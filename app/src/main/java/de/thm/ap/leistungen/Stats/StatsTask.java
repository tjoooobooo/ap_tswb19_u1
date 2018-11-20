package de.thm.ap.leistungen.Stats;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import de.thm.ap.leistungen.R;
import de.thm.ap.leistungen.model.Record;

public class StatsTask extends AsyncTask<List<Record>, Void, Stats> {

    @SuppressLint("StaticFieldLeak")
    private Context context = null;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar = null;
    private long timestamp1;

    public StatsTask(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        // start progress bar
        timestamp1 = System.currentTimeMillis();
        progressBar = ((Activity)context).findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    protected Stats doInBackground(List<Record> ...records) {
        // calculate stats
        Stats stats = new Stats(records);
        long timestamp2 = System.currentTimeMillis();
        if(timestamp2 - timestamp1 < 2000) {
            try {
                Thread.sleep(2000 - (timestamp2 -timestamp1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return stats;
    }
    @Override
    protected void onPostExecute(Stats stats) {
        // stop progress bar
        // show result dialog
        progressBar.setVisibility(View.GONE);
        new AlertDialog.Builder(context)
                .setTitle(R.string.stats)
                .setMessage(stats.toString())
                .setNeutralButton(R.string.close, null)
                .show();
    }

    public void showStats() {

    }
}
