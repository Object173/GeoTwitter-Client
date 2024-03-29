package com.object173.geotwitter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(final Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if(sqLiteDatabase == null) {
            return;
        }
        final String[] createTable = DatabaseContract.SQL_CREATE_TABLE_ARRAY;
        for (final String aCreateTable : createTable) {
            sqLiteDatabase.execSQL(aCreateTable);
        }
    }
    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(sqLiteDatabase == null) {
            return;
        }
        final String[] deleteTable = DatabaseContract.SQL_DELETE_TABLE_ARRAY;
        for(final String delete: deleteTable) {
            sqLiteDatabase.execSQL(delete);
        }
        onCreate(sqLiteDatabase);
    }
}
