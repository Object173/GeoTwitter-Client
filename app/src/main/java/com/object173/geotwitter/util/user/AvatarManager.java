package com.object173.geotwitter.util.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.util.resources.CacheManager;
import com.object173.geotwitter.util.resources.ImageUtils;
import com.object173.geotwitter.util.resources.ImagesManager;

import java.io.File;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class AvatarManager {

    private static final String FILENAME_AVATAR = "user_avatar.png";

    public static String getAvatarPath() {
        final File avatarFile = new File(CacheManager.getImagesPath(), FILENAME_AVATAR);
        return avatarFile.getAbsolutePath();
    }

    public static void setAvatarView(final Context context, final ImageView view) {
        if(context == null || view == null) {
            return;
        }
        if(AuthManager.getAvatar() != null) {
            ImagesManager.setImageViewCache(context, view, R.mipmap.avatar, AuthManager.getAvatar());
        }
        else {
            view.setImageResource(R.mipmap.avatar);
        }
    }

    static void invalidateAvatar(final Context context) {
        ImagesManager.invalidateImage(context, AuthManager.getAvatar());
        ImagesManager.invalidateImage(context, AuthManager.getAvatar());
    }

    static boolean removeAvatar(final Context context) {
        if(context != null) {
            ImagesManager.deleteImage(getAvatarPath());
            invalidateAvatar(context);
            return true;
        }
        return false;
    }

    public static boolean writeAvatar(final Context context, final String imagePath) {
        if(context == null || imagePath == null) {
            return false;
        }
        final Bitmap avatar = ImageUtils.resizeBitmap(
                ImagesManager.readImage(imagePath), ImageUtils.LARGE_WIDTH);
        return ImagesManager.writeImage(context, avatar, AvatarManager.getAvatarPath());
    }
}
