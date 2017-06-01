package com.object173.geotwitter.database.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Profile;

import java.util.ArrayList;
import java.util.List;

public final class ProfileRepository {
    private static final int NULL_ID = DatabaseContract.NULL_ID;
    //поиск по id
    public static Profile select(final SQLiteDatabase db, final long id){
        if(db == null || id <= NULL_ID) { //проверка входных параметров
            return null;
        } //создаем курсор по соответсвующему запросу
        final Cursor cursor = db.query(DatabaseContract.ProfileTableScheme.TABLE_NAME, null,
                DatabaseContract.ProfileTableScheme._ID + "=?", new String[] {Long.toString(id)},
                null, null, null);
        if(cursor.moveToFirst()) {
            final Profile profile = new Profile(cursor); //считываем профиль
            //считываем изображения провиля
            profile.setAvatar(ImageRepository.select(db, profile.getAvatar_id()));
            profile.setAvatarMini(ImageRepository.select(db, profile.getAvatar_mini_id()));
            cursor.close(); //закрывам курсор
            return profile;
        } else { return null;}
    }

    public static long insert(final SQLiteDatabase db, final Profile profile) {
        if(db == null || profile == null) {
            return NULL_ID;
        }
        if(profile.getAvatar() != null) {
            final long avatarId = ImageRepository.insert(db, profile.getAvatar());
            profile.getAvatar().setId(avatarId);
        }
        if(profile.getAvatarMini() != null) {
            final long avatarMiniId = ImageRepository.insert(db, profile.getAvatarMini());
            profile.getAvatarMini().setId(avatarMiniId);
        }
        return db.insert(DatabaseContract.ProfileTableScheme.TABLE_NAME, null, profile.insert());
    }

    public static List<Profile> select(final SQLiteDatabase db) {
        if(db == null) {
            return null;
        }
        final Cursor cursor = db.query(DatabaseContract.ProfileTableScheme.TABLE_NAME, null, null, null,
                null, null, DatabaseContract.ProfileTableScheme._ID);
        return select(db, cursor);
    }

    private static List<Profile> select(final SQLiteDatabase db, final Cursor cursor) {
        if(db == null || cursor == null) {
            return null;
        }

        final ArrayList<Profile> profileList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                final Profile profile = new Profile(cursor);
                profile.setAvatar(ImageRepository.select(db, profile.getAvatar_id()));
                profile.setAvatarMini(ImageRepository.select(db, profile.getAvatar_mini_id()));
                profileList.add(profile);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return profileList;
    }

    public static long update(final SQLiteDatabase db, final Profile profile) {
        if(db == null || profile == null) {
            return NULL_ID;
        }

        final int result = db.update(DatabaseContract.ProfileTableScheme.TABLE_NAME, profile.insert(),
                DatabaseContract.ProfileTableScheme._ID + " = ?",
                new String[] { String.valueOf(profile.getUserId())});

        final Profile newProfile = select(db, result);

        if(profile.getAvatar() != null) {
            profile.getAvatar().setId(newProfile.getAvatar_id());
            ImageRepository.update(db, profile.getAvatar());
        }
        if(profile.getAvatarMini() != null) {
            profile.getAvatarMini().setId(newProfile.getAvatar_mini_id());
            ImageRepository.update(db, profile.getAvatarMini());
        }

        return result;
    }

    public static boolean remove(final SQLiteDatabase db, final long id) {
        if(db == null || id <= NULL_ID) {
            return false;
        }

        final int result = db.delete(DatabaseContract.ProfileTableScheme.TABLE_NAME,
                DatabaseContract.ProfileTableScheme._ID + " = ?",
                new String[] { String.valueOf(id) });

        return (result > NULL_ID);
    }
}
