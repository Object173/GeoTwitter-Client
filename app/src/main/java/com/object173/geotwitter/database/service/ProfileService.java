package com.object173.geotwitter.database.service;


import android.content.Context;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.DatabaseHelper;
import com.object173.geotwitter.database.MyContentProvider;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.database.repository.ProfileRepository;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.object173.geotwitter.util.user.AuthManager;

import java.util.ArrayList;
import java.util.List;

public final class ProfileService {
    //поиск пользователя по id
    public static Profile getProfile(final Context context, final long id){
        //проверка входных данных
        if(context == null || id <= DatabaseContract.NULL_ID) {
            return null;
        }
        try { //если id соотетсвует авторизованному пользователю
            if(id == AuthManager.getAuthToken().getUserId()) {
                return AuthManager.getProfile();
            } //открываем соединение с БД
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            //измелаем сущность пользователь из БД
            final Profile profile = ProfileRepository
                    .select(dbHelper.getReadableDatabase(), id);
            dbHelper.close(); //закрываем соединение
            return profile;
        } catch (Exception ex) {return null;}
    }

    public static boolean addProfile(final Context context, final AuthProfile profile) {
        final List<AuthProfile> profileList = new ArrayList<>();
        profileList.add(profile);
        return addProfile(context, profileList);
    }

    public static boolean updateProfile(final Context context, final AuthProfile authProfile) {
        if(context == null || authProfile == null) {
            return false;
        }
        try {
            context.getContentResolver().update(MyContentProvider.CONTACT_CONTENT_URI, new Profile(authProfile).update(),
                    DatabaseContract.ProfileTableScheme._ID + "=?", new String[] {String.valueOf(authProfile.getUserId())});
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean updateAvatar(final Context context, final AuthProfile authProfile) {
        if(context == null || authProfile == null) {
            return false;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final Profile oldProfile = ProfileRepository.select(dbHelper.getReadableDatabase(), authProfile.getUserId());
            dbHelper.close();

            ImagesManager.invalidateImage(context, oldProfile.getAvatar());
            ImagesManager.invalidateImage(context, oldProfile.getAvatarMini());

            authProfile.setRelation(oldProfile.getRelationStatus());
            addProfile(context, authProfile);

            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean addProfile(final Context context, final List<AuthProfile> profileList) {
        if(context == null || profileList == null) {
            return false;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);

            for(final AuthProfile profile: profileList) {
                final Profile newProfile = new Profile(profile);
                final Profile oldProfile = ProfileRepository.select(db.getReadableDatabase(), newProfile.getUserId());

                if (oldProfile == null) {
                    if (newProfile.getAvatar() != null) {
                        final long avatarId = ImageService.addImage(context, newProfile.getAvatar());
                        newProfile.getAvatar().setId(avatarId);
                    }
                    if (newProfile.getAvatarMini() != null) {
                        final long avatarMiniId = ImageService.addImage(context, newProfile.getAvatarMini());
                        newProfile.getAvatarMini().setId(avatarMiniId);
                    }
                    context.getContentResolver().insert(MyContentProvider.CONTACT_CONTENT_URI, newProfile.insert());
                }
                else {
                    if (newProfile.getAvatar() != null) {
                        final long avatarId = ImageService.addImage(context, newProfile.getAvatar());
                        newProfile.getAvatar().setId(avatarId);
                    }
                    else {
                        if (oldProfile.getAvatar() != null) {
                            ImageService.removeImage(context, oldProfile.getAvatar().getId());
                            ImagesManager.invalidateImage(context, oldProfile.getAvatar());
                        }
                    }
                    if (newProfile.getAvatarMini() != null) {
                        final long avatarMiniId = ImageService.addImage(context, newProfile.getAvatarMini());
                        newProfile.getAvatarMini().setId(avatarMiniId);
                    }
                    else {
                        if (oldProfile.getAvatarMini() != null) {
                            ImageService.removeImage(context, oldProfile.getAvatarMini().getId());
                            ImagesManager.invalidateImage(context, oldProfile.getAvatarMini());
                        }
                    }

                    context.getContentResolver().update(MyContentProvider.CONTACT_CONTENT_URI, newProfile.insert(),
                            DatabaseContract.ProfileTableScheme._ID + "=?",
                            new String[]{String.valueOf(newProfile.getUserId())});
                }
            }

            db.close();
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static Cursor getContactList(final Context context) {
        return getContactList(context, null);
    }

    public static Cursor getContactList(final Context context, final String search) {
        if(context == null) {
            return null;
        }
        final String query = search == null ? "%%" : "%" + search + "%";
        return context.getContentResolver().query(MyContentProvider.CONTACT_CONTENT_URI, null,
                "( " + DatabaseContract.ProfileTableScheme.COLUMN_RELATION + "=? OR " +
                        DatabaseContract.ProfileTableScheme.COLUMN_RELATION + "=? ) AND " +
                        DatabaseContract.ProfileTableScheme.COLUMN_USERNAME + " LIKE ?",
                new String[] {AuthProfile.RelationStatus.CONTACT.toString(),
                        AuthProfile.RelationStatus.SUBSCRIBER.toString(), query},
                DatabaseContract.ProfileTableScheme.COLUMN_USERNAME + " ASC");
    }

    public static Cursor getRequireContactList(final Context context) {
        if(context == null) {
            return null;
        }
        return context.getContentResolver().query(MyContentProvider.CONTACT_CONTENT_URI, null,
                DatabaseContract.ProfileTableScheme.COLUMN_RELATION + " = ?",
                new String[] {AuthProfile.RelationStatus.CONTACT.toString()}, null);
    }

    public static Cursor getContact(final Context context, final long id) {
        if(context == null) {
            return null;
        }
        return context.getContentResolver().query(MyContentProvider.CONTACT_CONTENT_URI, null,
                DatabaseContract.ProfileTableScheme._ID + "=?",
                new String[] {String.valueOf(id)}, null);
    }

    public static Profile getContact(final Context context, final Cursor cursor) {
        if(context == null || cursor == null) {
            return null;
        }
        try {
            final Profile profile = new Profile(cursor);

            if(profile.getAvatar_id() > DatabaseContract.NULL_ID) {
                profile.setAvatar(ImageService.getImage(context, profile.getAvatar_id()));
            }
            if(profile.getAvatar_mini_id() > DatabaseContract.NULL_ID) {
                profile.setAvatarMini(ImageService.getImage(context, profile.getAvatar_mini_id()));
            }
            return profile;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static List<Profile> getAllProfile(final Context context) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final List<Profile> profileList = ProfileRepository.select(db.getReadableDatabase());
            db.close();
            return profileList;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static void removeProfile(final Context context, final long id) {
        if(context == null) {
            return;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final Profile profile = ProfileRepository.select(db.getReadableDatabase(), id);

            if(profile == null) {
                return;
            }
            if(profile.getAvatar() != null) {
                ImageService.removeImage(context, profile.getAvatar().getId());
            }
            if(profile.getAvatarMini() != null) {
                ImageService.removeImage(context, profile.getAvatarMini().getId());
            }

            context.getContentResolver().delete(MyContentProvider.CONTACT_CONTENT_URI,
                    DatabaseContract.ProfileTableScheme._ID + "=?",
                    new String[] {String.valueOf(profile.getUserId())});

            db.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
