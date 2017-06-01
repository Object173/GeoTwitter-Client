package com.object173.geotwitter.database;

import android.provider.BaseColumns;

public final class DatabaseContract {

    public static final int NULL_ID = 0;

    static final int DATABASE_VERSION = 17;
    static final String DATABASE_NAME = "database.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
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

    public static abstract class DialogsTableScheme implements BaseColumns {
        public static final String TABLE_NAME = "dialogs";
        public static final String COLUMN_COMPANION = "companion_id";

        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_COMPANION + INT_TYPE + ")";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class MessagesTableScheme implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_GLOBAL_ID = "global_id";
        public static final String COLUMN_SENDER = "sender_id";
        public static final String COLUMN_DIALOG = "dialog_id";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_MARKER = "marker";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_STATUS = "status";

        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_GLOBAL_ID + INT_TYPE + COMMA_SEP +
                COLUMN_SENDER + INT_TYPE + COMMA_SEP +
                COLUMN_DIALOG + INT_TYPE + COMMA_SEP +
                COLUMN_TEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_IMAGE + INT_TYPE + COMMA_SEP +
                COLUMN_MARKER + INT_TYPE + COMMA_SEP +
                COLUMN_DATE + INT_TYPE + COMMA_SEP +
                COLUMN_STATUS + INT_TYPE + ")";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class MarkerTableScheme implements BaseColumns {
        public static final String TABLE_NAME = "markers";
        public static final String COLUMN_LATITUDE = "latitude_id";
        public static final String COLUMN_LONGITUDE = "longitude_id";

        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_LATITUDE + REAL_TYPE + COMMA_SEP +
                COLUMN_LONGITUDE + REAL_TYPE + ")";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class PlaceTableScheme implements BaseColumns {
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_AUTHOR = "author_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_MARKER = "marker_id";
        public static final String COLUMN_DATE = "date";

        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_AUTHOR + INT_TYPE + COMMA_SEP +
                COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_BODY + TEXT_TYPE + COMMA_SEP +
                COLUMN_MARKER + INT_TYPE + COMMA_SEP +
                COLUMN_DATE + INT_TYPE + ")";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class PlaceImageTableScheme implements BaseColumns {
        public static final String TABLE_NAME = "place_image";
        public static final String COLUMN_PLACE = "place_id";
        public static final String COLUMN_IMAGE = "image_id";

        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_PLACE + INT_TYPE + COMMA_SEP +
                COLUMN_IMAGE + TEXT_TYPE + COMMA_SEP +
                " FOREIGN KEY(" + COLUMN_PLACE + ") REFERENCES " +
                PlaceTableScheme.TABLE_NAME + "(" + PlaceTableScheme._ID + ")" +
                ON_DELETE_CASCADE + ON_UPDATE_CASCADE + COMMA_SEP +
                " FOREIGN KEY(" + COLUMN_IMAGE + ") REFERENCES " +
                ImageTableScheme.TABLE_NAME + "(" + ImageTableScheme._ID + ")" +
                ON_DELETE_CASCADE + ON_UPDATE_CASCADE + " )";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static final String[] SQL_CREATE_TABLE_ARRAY = {
            ImageTableScheme.CREATE_TABLE,
            ProfileTableScheme.CREATE_TABLE,
            DialogsTableScheme.CREATE_TABLE,
            MessagesTableScheme.CREATE_TABLE,
            MarkerTableScheme.CREATE_TABLE,
            PlaceTableScheme.CREATE_TABLE,
            PlaceImageTableScheme.CREATE_TABLE
    };

    static final String[] SQL_DELETE_TABLE_ARRAY = {
            ImageTableScheme.DELETE_TABLE,
            ProfileTableScheme.DELETE_TABLE,
            DialogsTableScheme.DELETE_TABLE,
            MessagesTableScheme.DELETE_TABLE,
            MarkerTableScheme.DELETE_TABLE,
            PlaceTableScheme.DELETE_TABLE,
            PlaceImageTableScheme.DELETE_TABLE
    };
}
