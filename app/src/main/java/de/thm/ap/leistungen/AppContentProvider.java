package de.thm.ap.leistungen;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

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

    @androidx.annotation.Nullable
    @Override
    public Cursor query(@androidx.annotation.NonNull Uri uri, @androidx.annotation.Nullable String[] projection, @androidx.annotation.Nullable String selection, @androidx.annotation.Nullable String[] selectionArgs, @androidx.annotation.Nullable String sortOrder) {
        return null;
    }

    @androidx.annotation.Nullable
    @Override
    public String getType(@androidx.annotation.NonNull Uri uri) {
        return null;
    }

    @androidx.annotation.Nullable
    @Override
    public Uri insert(@androidx.annotation.NonNull Uri uri, @androidx.annotation.Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@androidx.annotation.NonNull Uri uri, @androidx.annotation.Nullable String selection, @androidx.annotation.Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@androidx.annotation.NonNull Uri uri, @androidx.annotation.Nullable ContentValues values, @androidx.annotation.Nullable String selection, @androidx.annotation.Nullable String[] selectionArgs) {
        return 0;
    }
}
