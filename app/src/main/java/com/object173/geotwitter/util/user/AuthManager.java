package com.object173.geotwitter.util.user;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.Profile;
import com.object173.geotwitter.gui.main.MainActivity;
import com.object173.geotwitter.server.json.AuthProfile;
import com.object173.geotwitter.server.json.AuthToken;
import com.object173.geotwitter.service.authorization.AuthAccount;
import com.object173.geotwitter.service.notification.InstanceIDService;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.resources.ImagesManager;

import java.io.IOException;

public final class AuthManager {

    private static final String KEY_CURRENT_USER_NAME = "current_user_name";
    private static final String KEY_CURRENT_USER_STATUS = "current_user_status";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_CURRENT_USER_HASH_KEY = "current_user_hash_key";
    private static final String KEY_CURRENT_USER_AVATAR = "current_user_avatar";

    private static AuthToken authToken = null;
    private static AuthProfile authProfile = null;
    private static Image avatar = null;

    private static Account account = null;

    public static boolean setCurrentUser(final Context context, final AuthProfile profile, final AuthToken token,
                                        final String login, final String password) {
        if(context == null || profile == null || token == null || login == null || password == null) {
            return false;
        }
        if(profile.getUsername() == null || token.getUserId() <= DatabaseContract.NULL_ID
                || token.getHashKey() == null) {
            return false;
        }

        //получаем account manager для данного приложения
        final AccountManager accountManager = AccountManager.get(context);
        //создаем новый аккаунт
        final Account account = new Account(login, AuthAccount.TYPE);
        final Bundle userData = new Bundle(); //контейнер пользовательской информации
        userData.putString(KEY_CURRENT_USER_ID, String.valueOf(token.getUserId()));
        userData.putString(KEY_CURRENT_USER_HASH_KEY, token.getHashKey());
        userData.putString(KEY_CURRENT_USER_NAME, profile.getUsername());
        userData.putString(KEY_CURRENT_USER_STATUS, profile.getStatus());
        userData.putString(KEY_CURRENT_USER_AVATAR, profile.getAvatarUrl());
        //добавляем созданный аккаунт
        accountManager.addAccountExplicitly(account, password, userData);
        accountManager.setAuthToken(account, AuthAccount.TOKEN_FULL_ACCESS, token.getHashKey());

        authToken = token;
        authProfile = profile;
        if(authProfile.getAvatarUrl() != null) {
            avatar = new Image(AvatarManager.getAvatarPath(), authProfile.getAvatarUrl());
        }
        else {
            avatar = null;
        }
        AvatarManager.invalidateAvatar(context);
        ImagesManager.initImage(context, avatar);

        CacheManager.syncData(context);

        return getCurrentAccount(context) != null;
    }

    public static void readCurrentUser(final Context context) {
        if(context == null) {
            authProfile = null;
            authToken = null;
            avatar = null;
            return;
        }

        account = getCurrentAccount(context);
        if(account == null) {
            return;
        }

        final AccountManager accountManager = AccountManager.get(context);
        final long userId = Long.parseLong(accountManager.getUserData(account, KEY_CURRENT_USER_ID));
        final String hashKey = accountManager.getUserData(account, KEY_CURRENT_USER_HASH_KEY);
        final String userName = accountManager.getUserData(account, KEY_CURRENT_USER_NAME);
        final String userStatus = accountManager.getUserData(account, KEY_CURRENT_USER_STATUS);
        final String avatarUrl = accountManager.getUserData(account, KEY_CURRENT_USER_AVATAR);

        if(userId > DatabaseContract.NULL_ID && hashKey != null && userName != null) {
            authToken = new AuthToken(userId, hashKey);
            authProfile = new AuthProfile(userName, userStatus, avatarUrl, avatarUrl);
            authProfile.setUserId(userId);

            if(authProfile.getAvatarUrl() != null) {
                avatar = new Image(AvatarManager.getAvatarPath(), authProfile.getAvatarUrl());
            }
            else {
                avatar = null;
            }
            AvatarManager.invalidateAvatar(context);
            ImagesManager.initImage(context, avatar);
            CacheManager.syncData(context);
        }
        else {
            authProfile = null;
            authToken = null;
            avatar = null;
        }
    }

    private static Account getCurrentAccount(final Context context) {
        if(context == null) {
            return null;
        }
        if(account == null) {
            final Account[] accounts = AccountManager.get(context).getAccountsByType(AuthAccount.TYPE);
            if(accounts.length > 0) {
                account = accounts[0];
            }
        }
        return account;
    }

    public static AuthToken getAuthToken() {
        return authToken;
    }

    public static AuthProfile getAuthProfile() {
        return authProfile;
    }

    public static Profile getProfile() {
        final Profile profile = new Profile(authProfile);
        profile.setUserId(authToken.getUserId());
        profile.setAvatar(avatar);
        profile.setAvatarMini(avatar);
        return profile;
    }

    public static Image getAvatar() {
        return avatar;
    }

    public static void setUsername(final Context context, final String username) {
        if(context != null || username != null || isAuth()) {
            final AccountManager accountManager = AccountManager.get(context);
            accountManager.setUserData(getCurrentAccount(context), KEY_CURRENT_USER_NAME, username);
            authProfile.setUsername(username);
        }
    }

    public static void setStatus(final Context context, final String status) {
        if(context != null && isAuth()) {
            final AccountManager accountManager = AccountManager.get(context);
            accountManager.setUserData(getCurrentAccount(context), KEY_CURRENT_USER_STATUS, status);
            authProfile.setStatus(status);
        }
    }

    public static void setAvatar(final Context context, final String avatarUrl) {
        if(context != null) {
            final AccountManager accountManager = AccountManager.get(context);
            accountManager.setUserData(getCurrentAccount(context), KEY_CURRENT_USER_AVATAR, avatarUrl);
            if(avatarUrl == null) {
                ImagesManager.invalidateImage(context, avatar);
                AvatarManager.removeAvatar(context);
                avatar = null;
            }
            else {
                avatar = new Image(AvatarManager.getAvatarPath(), avatarUrl);
                AvatarManager.invalidateAvatar(context);
            }
        }
    }

    public static boolean isAuth() {
        return authToken != null && authProfile != null;
    }

    public static boolean signOut(final Context context) {
        if(context == null) {
            return false;
        }

        authToken = null;
        authProfile = null;
        avatar = null;

        CacheManager.clearCache(context);
        InstanceIDService.signOut();

        final AccountManager am = AccountManager.get(context);
        final Account[] accounts = AccountManager.get(context).getAccountsByType(AuthAccount.TYPE);

        for(Account account : accounts) {
            am.removeAccount(account, null, null);
        }

        final Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        return true;
    }

    public static void onStart(final Activity activity) {
        if(activity == null) {
            return;
        }
        final AccountManager am = AccountManager.get(activity);
        if (am.getAccountsByType(AuthAccount.TYPE).length == 0) {
            am.addAccount(AuthAccount.TYPE, AuthAccount.TOKEN_FULL_ACCESS, null, null, activity,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            try {
                                future.getResult();
                            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                Log.d("AuthAccount",e.toString());
                            }
                        }
                    }, null
            );
        }
    }
}
