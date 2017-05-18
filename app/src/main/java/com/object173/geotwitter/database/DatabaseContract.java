package com.object173.geotwitter.database;

import android.provider.BaseColumns;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class DatabaseContract {

    public static final int NULL_ID = 0;

    static final int DATABASE_VERSION = 10;
    static final String DATABASE_NAME = "database.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String PRIMARY_KEY_TYPE = " INTEGER PRIMARY KEY";
    private static final String COMMA_SEP = ",";
    private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE";
    private static final String ON_UPDATE_CASCADE = " ON UPDATE CASCADE";
    private static final String UNIQUE = " UNIQUE ";

    DatabaseContract() {}

    public static abstract class ImageTableScheme implements BaseColumns {
        public static final String TABLE_NAME = "images";
        public static final String COLUMN_LOCAL_PATH = "local_path";
        public static final String COLUMN_ONLINE_URL = "online_url";

        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_LOCAL_PATH + TEXT_TYPE + COMMA_SEP +
                COLUMN_ONLINE_URL + TEXT_TYPE + ")";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ProfileTableScheme implements BaseColumns {
        public static final String TABLE_NAME = "profiles";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_AVATAR = "avatar";
        public static final String COLUMN_AVATAR_MINI = "avatar_mini";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_RELATION = "relation";

        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_USERNAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_STATUS + TEXT_TYPE + COMMA_SEP +
                COLUMN_RELATION + TEXT_TYPE  + COMMA_SEP +
                COLUMN_AVATAR + INT_TYPE + COMMA_SEP +
                COLUMN_AVATAR_MINI + INT_TYPE + ")";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static final String[] SQL_CREATE_TABLE_ARRAY = {
            ImageTableScheme.CREATE_TABLE,
            ProfileTableScheme.CREATE_TABLE
    };

    static final String[] SQL_DELETE_TABLE_ARRAY = {
            ImageTableScheme.DELETE_TABLE,
            ProfileTableScheme.DELETE_TABLE
    };
}
