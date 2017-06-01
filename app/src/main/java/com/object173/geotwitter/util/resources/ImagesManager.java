package com.object173.geotwitter.util.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.service.ImageService;
import com.object173.geotwitter.server.ServerContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;

public final class ImagesManager {

    public static boolean preparationImage(final Context context, final String imagePath, final String outPath) {
        if(context == null || imagePath == null || outPath == null) {
            return false;
        }
        final Bitmap image = readImage(imagePath);
        final Bitmap scaleImage = ImageUtils.resizeBitmap(image, ImageUtils.LARGE_WIDTH);

        if(scaleImage == null) {
            return false;
        }
        return writeImage(context, scaleImage, outPath);
    }

    public static byte[] preparationImage(final Context context, final String imagePath) {
        if(context == null || imagePath == null) {
            return null;
        }
        final Bitmap image = readImage(imagePath);
        final Bitmap scaleImage = ImageUtils.resizeBitmap(image, ImageUtils.LARGE_WIDTH);

        if(scaleImage == null) {
            return null;
        }
        return toByteArray(scaleImage);
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
        return writeImage(image, new File(path));
    }

    public static boolean writeNewImage(final Bitmap image, final String path) {
        final File file = new File(path);
        return !file.exists() && writeImage(image, file);
    }

    public static boolean writeImage(final Bitmap image, final File newImage) {
        if(image == null || newImage == null) {
            return false;
        }

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

    public static boolean deleteImage(final String path) {
        if(path == null) {
            return false;
        }
        final File image = new File(path);
        return image.delete();
    }

    public static void writeAsync(final Bitmap image, final String path) {
        if(image != null && path != null) {
            final WriteTask task = new WriteTask();
            task.execute(new WriteTask.InputArg(image, path));
        }
    }

    public static void setImageViewCache(final Context context, final ImageView view, final int placeholderId,
                                         final Image image) {
        if(context == null || image == null || image.getLocalPath() == null) {
            Log.d("picasso","setImageViewCache null");
            return;
        }
        final File file = new File(image.getLocalPath());
        if(!file.exists()) {
            setImageViewOnline(context, view, placeholderId, image);
            return;
        }
        final Picasso picasso = Picasso.with(context);
        try {
            picasso.load(file)
                    .placeholder(placeholderId)
                    .error(placeholderId)
                    .fit()
                    .into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("picasso", "offline success " + file.getAbsolutePath());
                        }

                        @Override
                        public void onError() {
                            Log.d("picasso", "offline fail");
                            setImageViewOnline(context, view, placeholderId, image);
                        }
                    });
        }
        catch (Exception ex) {
        }
    }

    static void setImageViewOnline(final Context context, final ImageView view, final int placeholderId,
                                   final Image image) {
        Log.d("ImagesManager","setImageViewOnline start " + image.getOnlineUrl());
        if(context == null || image == null || image.getOnlineUrl() == null) {
            Log.d("picasso","setImageViewOnline null");
            return;
        }
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                Log.d("picasso", "online success " + ServerContract.getAbsoluteUrl(image.getOnlineUrl()));
                final Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
                writeAsync(bitmap, image.getLocalPath());
            }

            @Override
            public void onError() {
                Log.d("picasso", "online fail " + ServerContract.getAbsoluteUrl(image.getOnlineUrl()));
            }
        };
        try {
            if(image.getLocalPath() == null) {
                Log.d("ImagesManager", "load online only");
                Picasso.with(context)
                        .load(ServerContract.getAbsoluteUrl(image.getOnlineUrl()))
                        .placeholder(placeholderId)
                        .error(placeholderId)
                        .fit()
                        .into(view, callback);
            }
            else {
                Log.d("ImagesManager", "load online to cach");
                Picasso.with(context)
                        .load(ServerContract.getAbsoluteUrl(image.getOnlineUrl()))
                        .placeholder(placeholderId)
                        .error(placeholderId)
                        .into(view, callback);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initImage(final Context context, final Image image) {
        if(context != null && image != null && image.getLocalPath() != null) {
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    downloadImage(context, image);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            try {
                Picasso.with(context)
                        .load(new File(image.getLocalPath()))
                        .into(target);
            }
            catch (Exception ex) {
            }
        }
    }

    private static void downloadImage(final Context context, final Image image) {
        if (context != null && image != null && image.getOnlineUrl() != null) {
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            try {
                Picasso.with(context)
                        .load(ServerContract.getAbsoluteUrl(image.getOnlineUrl()))
                        .into(target);
            }
            catch (Exception ex) {
            }
        }
    }

    public static void invalidateAll(final Context context) {
        if(context == null) {
            return;
        }
        final List<Image> imageList = ImageService.getAllImages(context);
        if(imageList == null) {
            return;
        }

        for(Image image : imageList) {
            invalidateImage(context, image);
        }
    }

    public static void invalidateImage(final Context context, final String path) {
        if(context != null && path != null) {
            Picasso.with(context).invalidate(new File(path));
        }
    }

    public static void invalidateImage(final Context context, final Image image) {
        if(context != null && image != null) {
            if(image.getLocalPath() != null) {
                Picasso.with(context).invalidate(new File(image.getLocalPath()));
            }
            if(image.getOnlineUrl() != null) {
                Picasso.with(context).invalidate(image.getOnlineUrl());
            }
        }
    }

    public static void setImageViewOnline(final Context context, final ImageView view, final int placeholderId,
                                          final String onlinePath) {
        Log.d("ImagesManager","setImageViewOnline");
        setImageViewOnline(context, view, placeholderId, Image.newNetworkImage(onlinePath));
    }

    public static void setImageViewCache(final Context context, final ImageView view, final int placeholderId,
                                         final String localPath) {
        setImageViewCache(context, view, placeholderId, Image.newLocalImage(localPath));
    }

    private static class WriteTask extends AsyncTask<WriteTask.InputArg, Void, Void> {

        static class InputArg {
            private Bitmap bitmap;
            private  String path;

            InputArg(final Bitmap bitmap, final String path) {
                this.bitmap = bitmap;
                this.path = path;
            }
        }

        @Override
        protected Void doInBackground(final InputArg... args) {
            for(InputArg input: args) {
                writeNewImage(input.bitmap, input.path);
            }
            return null;
        }
    }
}
