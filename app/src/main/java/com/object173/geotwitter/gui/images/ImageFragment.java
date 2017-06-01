package com.object173.geotwitter.gui.images;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.gui.util.FrameController;

public final class ImageFragment extends Fragment implements ImageLoader.OnImageLoadListener {

    private static final int FRAME_LOAD = 1;
    private static final int FRAME_IMAGE = 2;

    private final FrameController frameController =
            new FrameController(R.id.frame_layout, new FrameController.Frame[]
                    {
                            new FrameController.Frame(FRAME_LOAD, R.id.progress_bar),
                            new FrameController.Frame(FRAME_IMAGE, R.id.image_view)
                    });

    private static final String KEY_IMAGE = "image";

    private ImageLoader imageLoader;
    private Image image = null;

    private static final int MAX_SCALE_DPI = 50;
    private static final int DOUBLE_TAP_ZOOM_DPI = 100;

    static ImageFragment newInstance(final Image image) {
        final ImageFragment fragment = new ImageFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_IMAGE, image);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        final View view = inflater.inflate(R.layout.slide_image, null);

        image = (Image) getArguments().getSerializable(KEY_IMAGE);

        final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) view.findViewById(R.id.image_view);
        configImageView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImageViewerActivity activity = (ImageViewerActivity) getActivity();
                if(activity != null) {
                    activity.showHideToolbar();
                }
            }
        });

        imageLoader = new ImageLoader(imageView, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(imageLoader != null) {
            imageLoader.loadImage(getContext(), image);
        }
    }

    private void configImageView(final SubsamplingScaleImageView  imageView) {
        imageView.setMinimumDpi(MAX_SCALE_DPI);
        imageView.setDoubleTapZoomDpi(DOUBLE_TAP_ZOOM_DPI);
    }

    @Override
    public void startLoad() {
        frameController.setViewState(getView(), FRAME_LOAD);
    }

    @Override
    public void endLoad() {
        frameController.setViewState(getView(), FRAME_IMAGE);
    }
}
