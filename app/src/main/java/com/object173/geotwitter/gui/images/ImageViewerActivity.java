package com.object173.geotwitter.gui.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.util.ShareManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends MyBaseActivity {

    private MyFragmentPagerAdapter adapter = null;
    private ViewPager pager = null;

    private static final String KEY_CURRENT_SLIDE = "current_slide";
    private static final String KEY_LIST_IMAGES = "images_list";
    private static final String KEY_FIRST_SLIDE = "first_slide";
    private static final String KEY_TITLE = "title";

    public static void startActivity(final Context context, final Image image, final String title) {
        if(context != null && image != null) {
            final ArrayList<Image> imageList = new ArrayList<>();
            imageList.add(image);
            startActivity(context, imageList, 0, title);
        }
    }

    public static void startActivity(final Context context, final List<String> urlList,
                                     final int firstSlide, final String title) {
        if(context != null && urlList != null && !urlList.isEmpty()) {

            final ArrayList<Image> imageList = new ArrayList<>();
            for(String url : urlList) {
                imageList.add(Image.newNetworkImage(url));
            }

            final Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putExtra(KEY_LIST_IMAGES, imageList);
            intent.putExtra(KEY_FIRST_SLIDE, firstSlide);
            intent.putExtra(KEY_TITLE, title);
            context.startActivity(intent);
        }
    }

    public static void startActivity(final Context context, final ArrayList<Image> imageList,
                                     final int firstSlide, final String title) {
        if(context != null && imageList != null && !imageList.isEmpty()) {
            final Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putExtra(KEY_LIST_IMAGES, imageList);
            intent.putExtra(KEY_FIRST_SLIDE, firstSlide);
            intent.putExtra(KEY_TITLE, title);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_image_viewer, true)) {
            finish();
            return;
        }
        setToolbarPadding();
        final String title = getIntent().getExtras().getString(KEY_TITLE);
        if(title != null) {
            setTitle(title);
        }

        final List<Image> imageList = (List<Image>) getIntent().getExtras().getSerializable(KEY_LIST_IMAGES);

        pager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), imageList);
        pager.setAdapter(adapter);

        final int currentPage;
        if(savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(KEY_CURRENT_SLIDE);
        }
        else {
            currentPage = getIntent().getIntExtra(KEY_FIRST_SLIDE, 0);
        }
        pager.setCurrentItem(currentPage);
    }

    public final void showHideToolbar() {
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        if (appBar.getTop() < 0)
            appBar.setExpanded(true);
        else
            appBar.setExpanded(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_SLIDE, pager.getCurrentItem());
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.image_viewer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item == null) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.menu_share:
                shareImage(adapter.getImage(pager.getCurrentItem()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<Image> imagesList = new ArrayList<>();

        MyFragmentPagerAdapter(final FragmentManager fm, final List<Image> imagesList) {
            super(fm);
            this.imagesList.addAll(imagesList);
        }

        public Image getImage(final int position) {
            if(position < 0 || position > imagesList.size()) {
                return null;
            }
            return imagesList.get(position);
        }

        @Override
        public Fragment getItem(final int position) {
            return ImageFragment.newInstance(imagesList.get(position));
        }

        @Override
        public int getCount() {
            return imagesList.size();
        }
    }

    private void shareImage(final Image image) {
        if(image == null) {
            return;
        }
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ShareManager.shareImage(ImageViewerActivity.this, bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        if(image.getLocalPath() != null) {
            final File file = new File(image.getLocalPath());
            Picasso.with(this)
                    .load(file)
                    .into(target);
        }
        else {
            Picasso.with(this)
                    .load(ServerContract.getAbsoluteUrl(image.getOnlineUrl()))
                    .into(target);
        }
    }
}
