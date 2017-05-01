package com.object173.geotwitter.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by Object173
 * on 30.04.2017.
 */

public final class ImagesManager {

    public static boolean copyFileFromUri(final File srcFile, final File dstFile) {
        if(srcFile == null || dstFile == null) {
            return false;
        }
        if (srcFile.getAbsolutePath().equals(dstFile.getAbsolutePath())) {
            return true;
        }
        try {
            final InputStream in = new FileInputStream(srcFile);
            final OutputStream out = new FileOutputStream(dstFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    public static Bitmap readImage(final String path) {
        if(path == null) {
            return null;
        }

        final Uri uri = Uri.parse(path);
        final File img = new File(uri.getPath());

        if(img.exists()) {
            return BitmapFactory.decodeFile(img.getAbsolutePath());
        }
        return null;
    }

    public static byte[] readByteArray(final String path) {
        if(path == null) {
            return null;
        }

        try {
            final RandomAccessFile file = new RandomAccessFile(path, "r");
            byte[] byteArray = new byte[(int)file.length()];
            file.readFully(byteArray);
            file.close();
            return byteArray;
        } catch (IOException ex) {
            return null;
        }
    }

    public static byte[] toByteArray(final Bitmap image) {
        if(image == null) {
            return null;
        }

        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] byteArray = stream.toByteArray();
            stream.close();
            return byteArray;
        } catch (IOException ex) {
            return null;
        }
    }

    public static boolean writeImage(final Context context, final Bitmap image, final String path) {
        if(context == null || image == null || path == null) {
            return false;
        }

        final Uri uri = Uri.parse(path);
        final File newImage = new File(getRealPathFromURI(context, uri));

        try {
            final OutputStream out = new FileOutputStream(newImage);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    public static String getRealPathFromURI(final Context context, final Uri contentURI) {
        if(context == null || contentURI == null) {
            return null;
        }

        String result;
        final Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
