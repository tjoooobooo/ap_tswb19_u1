package de.thm.ap.leistungen.data;

import java.util.List;
import java.util.Optional;

import de.thm.ap.leistungen.model.Record;

public interface RecordDAO {
    public List<Record> findAll();

    public Optional<Record> findById(int id);

    /**
     * Ersetzt das übergebene {@link Record} Objekt mit einem bereits
     gespeicherten {@link Record} Objekt mit gleicher id.
     *
     * @param record
     * @return true = update ok, false = kein {@link Record} Objekt mit gleicher
    id im Speicher gefunden
     */
    public boolean update(Record record);

    /**
     * Persistiert das übergebene {@link Record} Objekt und liefert die neue id
     zurück.
     *
     * @param record
     * @return neue record id
     */
    public int persist(Record record);

    public void initRecords();

    public void saveRecords();
}
