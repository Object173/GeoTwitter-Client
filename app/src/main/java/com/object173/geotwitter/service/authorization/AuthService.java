package com.object173.geotwitter.service.authorization;

import android.content.Context;
import android.content.Intent;

import com.object173.geotwitter.service.BaseService;

public final class AuthService {

    private static final int NULL_ID = BaseService.NULL_ID;

    public static long startToSignIn(final Context context, final String login, final String password) {
        if(context == null || login == null || password == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = SignInTask.createInputIntent(context, login, password, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToRegister(final Context context, final String username,
                                       final String login, final String password, final String avatar) {
        if(context == null || username == null || login == null || password == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = RegisterTask.createInputIntent(
                context, username, login, password, avatar, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToEditUsername(final Context context, final String username) {
        if(context == null || username == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = EditProfileTask.createEditUsernameIntent(context, username, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToEditStatus(final Context context, final String status) {
        if(context == null || status == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = EditProfileTask.createEditStatusIntent(context, status, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToEditPassword(final Context context, final String oldPassword,
                                           final String newPassword) {
        if(context == null || oldPassword == null || newPassword == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = EditProfileTask.createEditPasswordIntent(context, oldPassword, newPassword, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToEditAvatar(final Context context, final String avatarPath) {
        if(context == null || avatarPath == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = EditProfileTask.createEditAvatarIntent(context, avatarPath, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }

    public static long startToRemoveAvatar(final Context context) {
        if(context == null) {
            return NULL_ID;
        }
        final long requestId = BaseService.createRequestId();
        final Intent intent = EditProfileTask.createRemoveAvatarIntent(context, requestId);
        return BaseService.startToIntent(context, intent, requestId);
    }
}
