package de.thm.ap.leistungen;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.thm.ap.leistungen.data.AppDatabase;

public class AppContentProvider extends ContentProvider {
    // ID für diesen Content Provider
    public static String AUTHORITY = "de.thm.ap.records.cp";
    private static final String RECORD_PATH = "records";
    private static UriMatcher URI_MATCHER;
    private static final int RECORDS = 1;
    private static final int RECORD_ID = 2;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        // content://de.thm.ap.records.cp/records
        URI_MATCHER.addURI(AUTHORITY, RECORD_PATH, RECORDS);
        // content://de.thm.ap.records.cp/records/# (# Nummernplatzhalter)
        URI_MATCHER.addURI(AUTHORITY, RECORD_PATH + "/#", RECORD_ID);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {

        SupportSQLiteQueryBuilder builder = SupportSQLiteQueryBuilder
                .builder("Record")
                .columns(projection)
                .orderBy(sortOrder);
        switch (URI_MATCHER.match(uri)) {
            case RECORDS:
                break;
            case RECORD_ID:
                builder.selection("id = ?", new Object[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        SupportSQLiteDatabase db = AppDatabase.getDb(getContext())
                .getOpenHelper()
                .getReadableDatabase();
        return db.query(builder.create());
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case RECORDS:
                return "vnd.android.cursor.dir/vnd.thm.ap.record";
            case RECORD_ID:
                return "vnd.android.cursor.item/vnd.thm.ap.record";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode){
        File file = null;

            file = new File(getContext().getFilesDir(), "export.csv");
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(file));
            SupportSQLiteQueryBuilder builder = SupportSQLiteQueryBuilder.builder("Record");
            SupportSQLiteDatabase db = AppDatabase.getDb(getContext()).getOpenHelper().getReadableDatabase();
            Cursor curCSV = db.query(builder.create());
            csvWriter.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String line[] = new String[8];
                for (int i = 0; i < 8; i++) line[i] = curCSV.getString(i);
                csvWriter.writeNext(line);
            }
            csvWriter.close();
            curCSV.close();
            return ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
