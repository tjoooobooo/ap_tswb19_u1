package de.thm.ap.leistungen.Stats;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.List;

import de.thm.ap.leistungen.model.Record;

public class StatsLiveData extends LiveData<Stats> implements Observer<List<Record>> {
    private LiveData<List<Record>> recordsLiveData;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public StatsLiveData(LiveData<List<Record>> records) {
        this.recordsLiveData = records;
    }
    @Override
    protected void onActive() {
        recordsLiveData.observeForever(this);
    }
    @Override
    protected void onInactive() {
        recordsLiveData.removeObserver(this);
    }
    @Override
    public void onChanged(@Nullable List<Record> records) {
        if (getValue() != null && getValue().getRecords() == records) {
            return;
        }
        setValue(null);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mainThreadHandler.post(() -> setValue(new Stats(records)));
        }).start();
    }
}
