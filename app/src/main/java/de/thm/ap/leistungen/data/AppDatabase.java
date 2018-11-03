package de.thm.ap.leistungen.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import de.thm.ap.leistungen.model.Record;

@Database(entities = {Record.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract RecordDAO recordDAO();
    public static AppDatabase getDb(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(ctx.getApplicationContext(), AppDatabase.class, "app-database")
                            .build();
        }
        return INSTANCE;
    }
}
