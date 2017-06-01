package com.object173.geotwitter.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.object173.geotwitter.R;

import java.io.OutputStream;


public final class ShareManager {

    public static void shareImage(final Context context, final Bitmap bitmap) {
        if(context == null || bitmap == null) {
            return;
        }

        final Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");

        final ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        final Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            final OutputStream outstream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(share, context.getString(R.string.title_share_image_chooser)));
    }
}
