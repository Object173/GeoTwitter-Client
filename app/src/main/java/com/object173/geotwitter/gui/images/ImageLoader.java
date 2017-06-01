package com.object173.geotwitter.gui.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.util.resources.ImagesManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

public final class ImageLoader {

    private final SubsamplingScaleImageView imageView;
    private final OnImageLoadListener listener;
    private Context context;

    private Image image;

    interface OnImageLoadListener {
        void startLoad();
        void endLoad();
    }

    public ImageLoader(final SubsamplingScaleImageView imageView, final OnImageLoadListener listener) {
        this.imageView = imageView;
        this.listener = listener;
    }

    public final void loadImage(final Context context, final Image image) {
        if(image == null || context == null) {
            return;
        }
        this.image = image;
        this.context = context;

        if(image.getLocalPath() != null) {
            try {
                final File file = new File(image.getLocalPath());
                if (file.exists()) {
                    Picasso.with(context)
                            .load(file)
                            .into(offlineTarget);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        loadOnline();
    }

    public final void loadOnline() {
        if(image == null || context == null) {
            return;
        }
        try {
            Picasso.with(context)
                    .load(ServerContract.getAbsoluteUrl(image.getOnlineUrl()))
                    .into(onlineTarget);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            setImage(null);
        }
    }

    private void setImage(final Bitmap bitmap) {
        if(imageView == null) {
            return;
        }
        if(bitmap != null) {
            imageView.setImage(ImageSource.bitmap(bitmap));
        }
        else  {
            imageView.setImage(ImageSource.resource(R.mipmap.ic_error_load));
        }
        if(listener != null) {
            listener.endLoad();
        }
        context = null;
    }

    private final Target offlineTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setImage(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            loadOnline();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if(listener != null) {
                listener.startLoad();
            }
        }
    };

    private final Target onlineTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setImage(bitmap);

            if(image != null && image.getLocalPath() != null) {
                ImagesManager.writeAsync(bitmap, image.getLocalPath());
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            setImage(null);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if(listener != null) {
                listener.startLoad();
            }
        }
    };
}
