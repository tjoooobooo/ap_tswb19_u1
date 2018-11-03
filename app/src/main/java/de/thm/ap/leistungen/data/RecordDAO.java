package de.thm.ap.leistungen.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.Optional;

import de.thm.ap.leistungen.model.Record;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecordDAO {

    @Query("SELECT * FROM record")
    LiveData<List<Record>> findAll();

    @Query("SELECT * FROM record WHERE id = :id")
    public List<Record> findById(int id);

    @Update(onConflict = REPLACE)
    int update(Record record);

    @Insert(onConflict = IGNORE)
    long persist(Record record);

    @Delete
    void delete(Record record);

    @Delete
    void deleteAll(Record... records);
}
