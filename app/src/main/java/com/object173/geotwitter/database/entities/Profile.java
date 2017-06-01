package com.object173.geotwitter.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.server.json.AuthProfile;

import java.io.Serializable;

public final class Profile implements Serializable{
    public Profile(final Cursor cursor) { //конструктор из курсора
        if(cursor == null) { //проверка курсора
            return;
        }
        userId = cursor.getLong(0); //id пользователя
        username = cursor.getString(1); //имя пользователя
        status = cursor.getString(2); //статус
        relationStatus = AuthProfile.RelationStatus //тип связи
                .valueOf(cursor.getString(3));
        avatar_id = cursor.getInt(4); //id изображения профиля
        avatar_mini_id = cursor.getInt(5); //id миниатюры
    }

    private long userId; //id пользователя
    private String username; //имя пользователя
    private String status; //статус
    private long avatar_id; //id изображения
    private Image avatar; //сущность изображение
    private long avatar_mini_id; //id миниатюры
    private Image avatarMini; //сущность миниатюры
    //тип связи с пользователем
    private AuthProfile.RelationStatus relationStatus;

    public Profile() {
    }

    public Profile(final AuthProfile profile) {
        if(profile != null) {
            this.userId = profile.getUserId();
            this.username = profile.getUsername();
            this.status = profile.getStatus();
            this.relationStatus = profile.getRelation();

            if(profile.getAvatarUrl() != null) {
                this.avatar = Image.newNetworkImage(profile.getAvatarUrl());
            }
            if(profile.getAvatarUrlMini() != null) {
                this.avatarMini = Image.newNetworkImage(profile.getAvatarUrlMini());
            }
        }
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public Image getAvatarMini() {
        return avatarMini;
    }

    public void setAvatarMini(Image avatarMini) {
        this.avatarMini = avatarMini;
    }

    public AuthProfile.RelationStatus getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(AuthProfile.RelationStatus relationStatus) {
        this.relationStatus = relationStatus;
    }

    public ContentValues insert() {
        final ContentValues values = new ContentValues();
        values.put(DatabaseContract.ProfileTableScheme._ID, userId);
        values.put(DatabaseContract.ProfileTableScheme.COLUMN_USERNAME, username);
        values.put(DatabaseContract.ProfileTableScheme.COLUMN_STATUS, status);
        if(avatar != null) {
            values.put(DatabaseContract.ProfileTableScheme.COLUMN_AVATAR, avatar.getId());
        }
        if(avatarMini != null) {
            values.put(DatabaseContract.ProfileTableScheme.COLUMN_AVATAR_MINI, avatarMini.getId());
        }
        values.put(DatabaseContract.ProfileTableScheme.COLUMN_RELATION, relationStatus.toString());
        return values;
    }

    public ContentValues update() {
        final ContentValues values = new ContentValues();
        values.put(DatabaseContract.ProfileTableScheme.COLUMN_USERNAME, username);
        values.put(DatabaseContract.ProfileTableScheme.COLUMN_STATUS, status);
        return values;
    }

    public long getAvatar_id() {
        return avatar_id;
    }

    public void setAvatar_id(long avatar_id) {
        this.avatar_id = avatar_id;
    }

    public long getAvatar_mini_id() {
        return avatar_mini_id;
    }

    public void setAvatar_mini_id(long avatar_mini_id) {
        this.avatar_mini_id = avatar_mini_id;
    }
}
