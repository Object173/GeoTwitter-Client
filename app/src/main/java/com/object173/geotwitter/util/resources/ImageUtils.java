package com.object173.geotwitter.util.resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by Object173
 * on 28.04.2017.
 */

public final class ImageUtils {

    public static final int SMALL_WIDTH = 300;
    public static final int MEDIUM_WIDTH = 600;
    public static final int LARGE_WIDTH = 1200;

    public static Bitmap resizeBitmap(final Bitmap image, final int newWidth) {
        if(image == null || newWidth <= 0) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        float scale = (float) newWidth / width;

        final Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return  Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
    }

    public static int calculateInSampleSize(final BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while (((halfHeight / inSampleSize) > reqHeight)
                    || ((halfWidth / inSampleSize) > reqWidth)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(String imgPath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }
}
