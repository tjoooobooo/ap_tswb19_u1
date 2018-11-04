package de.thm.ap.leistungen.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.thm.ap.leistungen.model.Record;

public class RecordFileDAO implements RecordDAO {
    private static String FILE_NAME = "records.obj";
    private Context ctx;
    private LiveData<List<Record>> records;
    private int nextId = 1;
    // TODO Request Intent
    public RecordFileDAO(Context ctx) {
        this.ctx = ctx;
        initRecords();
    }
    public LiveData<List<Record>> findAll() {
        return records;
    }

    @Override
    public List<Record> findById(int id) {
        List<Record> listRecord = new ArrayList<>();
        for(Record record : records.getValue()){
            if(record.getId().equals(id)){
                listRecord.add(record);
            }
        }
        return listRecord;
    }

    public int update(Record record) {
        if(record.getId() != null){
            if(findById(record.getId()).size() > 0){
                for(int i = 0; i < records.getValue().size(); i++) {
                    if(records.getValue().get(i).getId().equals(record.getId())){
                        records.getValue().set(i,record);
                    }
                }
            }
            saveRecords();
            return 1;
        } else {
            return 0;
        }
    }

    public long persist(Record record) {
        record.setId(nextId++);
        records.getValue().add(record);
        saveRecords();
        return record.getId();
    }

    @Override
    public void delete(Record record) {
        if(findById(record.getId()).size() > 0) records.getValue().removeAll(findById(record.getId()));
        saveRecords();
    }

    @Override
    public void deleteAll(Record... records) {
        for(Record record : records) delete(record);
    }

    @SuppressWarnings("unchecked")
    public void initRecords() {
        File f = ctx.getFileStreamPath(FILE_NAME);
        if (f.exists()) {
            try (FileInputStream in = ctx.openFileInput(FILE_NAME)) {
                Object obj = obj = new ObjectInputStream(in).readObject();
                records = (LiveData<List<Record>>) obj;
// init next id
                records.getValue().stream()
                        .mapToInt(Record::getId)
                        .max()
                        .ifPresent(id -> nextId = id + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            records = null;
        }
    }
    public void saveRecords() {
        try (FileOutputStream out = ctx.openFileOutput(FILE_NAME, Context.
                MODE_PRIVATE)) {
            new ObjectOutputStream(out).writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
