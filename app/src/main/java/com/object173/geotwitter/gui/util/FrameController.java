package com.object173.geotwitter.gui.util;

import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FrameController {

    private final int frameLayoutId;
    private final List<Frame> listFrame = new ArrayList<>();

    public FrameController(final int frameLayoutId, final Frame[] frames) {
        this.frameLayoutId =frameLayoutId;
        Collections.addAll(this.listFrame, frames);
    }

    public final void setViewState(final View parentView, final int state) {
        if(parentView == null) {
            return;
        }
        final FrameLayout frameLayout;
        if(parentView.getId() == frameLayoutId) {
            frameLayout = (FrameLayout) parentView;
        }
        else {
            frameLayout = (FrameLayout)parentView.findViewById(frameLayoutId);
        }
        if(frameLayout == null) {
            return;
        }
        for(Frame frame: listFrame) {
            final View view = frameLayout.findViewById(frame.getViewId());
            if(view == null) {
                continue;
            }
            if(frame.getId() == state) {
                view.setVisibility(View.VISIBLE);
            }
            else {
                view.setVisibility(View.GONE);
            }
        }
    }

    public static final class Frame {
        private final int viewId;
        private final int id;

        public Frame(final int id, final int viewId) {
            this.viewId = viewId;
            this.id =id;
        }

        final int getId() {
            return id;
        }

        final int getViewId() {
            return viewId;
        }
    }
}
