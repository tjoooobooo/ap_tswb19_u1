package de.thm.ap.leistungen.data;

import java.util.List;
import java.util.Optional;

import de.thm.ap.leistungen.model.Record;

public class RecordRoomDAO implements RecordDAO {
    @Override
    public List<Record> findAll() {
        return null;
    }

    @Override
    public Optional<Record> findById(int id) {
        return Optional.empty();
    }

    @Override
    public boolean update(Record record) {
        return false;
    }

    @Override
    public int persist(Record record) {
        return 0;
    }

    @Override
    public void initRecords() {

    }

    @Override
    public void saveRecords() {

    }
}
