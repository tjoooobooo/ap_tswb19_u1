package de.thm.ap.records;

import android.content.Context;
import android.graphics.Path;
import android.icu.text.AlphabeticIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.thm.ap.records.model.Record;

public class RecordDAO {
    private static String FILE_NAME = "records.obj";
    private Context ctx;
    private List<Record> records;
    private int nextId = 1;
    public RecordDAO(Context ctx) {
        this.ctx = ctx;
        initRecords();
    }
    public List<Record> findAll() {
        return records;
    }

    public Optional<Record> findById(int id) {
        for(Record record : records){
            if(record.getId().equals(id)){
                return Optional.ofNullable(record);
            }
        }
        return Optional.empty();
    }
    /**
     * Ersetzt das übergebene {@link Record} Objekt mit einem bereits
     gespeicherten {@link Record} Objekt mit gleicher id.
     *
     * @param record
     * @return true = update ok, false = kein {@link Record} Objekt mit gleicher
    id im Speicher gefunden
     */
    public boolean update(Record record) {
        if(record.getId() != null){
            if(findById(record.getId()).isPresent()){
                for(int i = 0; i < records.size(); i++) {
                    if(records.get(i).getId().equals(record.getId())){
                        records.set(i,record);
                    }
                }
            }
            saveRecords();
            return true;
        } else {
            return false;
        }
    }
    /**
     * Persistiert das übergebene {@link Record} Objekt und liefert die neue id
     zurück.
     *
     * @param record
     * @return neue record id
     */
    public int persist(Record record) {
        record.setId(nextId++);
        records.add(record);
        saveRecords();
        return record.getId();
    }
    @SuppressWarnings("unchecked")
    private void initRecords() {
        File f = ctx.getFileStreamPath(FILE_NAME);
        if (f.exists()) {
            try (FileInputStream in = ctx.openFileInput(FILE_NAME)) {
                Object obj = obj = new ObjectInputStream(in).readObject();
                records = (List<Record>) obj;
// init next id
                records.stream()
                        .mapToInt(Record::getId)
                        .max()
                        .ifPresent(id -> nextId = id + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            records = new ArrayList<>();
        }
    }
    private void saveRecords() {
        try (FileOutputStream out = ctx.openFileOutput(FILE_NAME, Context.
                MODE_PRIVATE)) {
            new ObjectOutputStream(out).writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean deleteRecords(List<Record> recordsToDelete){
        for(Record record : recordsToDelete){
            if(findById(record.getId()).isPresent()){
                records.remove(findById(record.getId()).get());
            } else return false;
        }
        saveRecords();
        return true;
    }
}
