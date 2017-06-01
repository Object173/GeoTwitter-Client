package com.object173.geotwitter.gui.map;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public final class SeekBarController implements SeekBar.OnSeekBarChangeListener {

    private int progress;
    private int min;
    private TextView progressView;
    private final String key;

    private OnChangeProgressListener listener;

    interface OnChangeProgressListener {
        void setProgress(String key, int progress);
    }

    public SeekBarController(final String key, final OnChangeProgressListener listener) {
        this.key = key;
        this.listener = listener;
    }

    public void setListener(final OnChangeProgressListener listener) {
        this.listener = listener;
    }

    public final void onCreate(final Bundle state, final SeekBar seekBar, final TextView progressView,
                               final int value, final int min, final int max) {
        if(seekBar == null) {
            return;
        }

        this.progressView = progressView;

        if(state != null) {
            setProgress(state.getInt(key, value));
        }
        else {
            setProgress(value);
        }
        this.min = min;

        seekBar.setMax(max - min);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public final void onSaveInstanceState(final Bundle outState) {
        if(outState != null && key != null) {
            outState.putInt(key, progress);
        }
    }

    public int getProgress() {
        return progress + min;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if(progressView != null) {
            progressView.setText(String.valueOf(getProgress()));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        setProgress(progress);
        if(listener != null) {
            listener.setProgress(key, getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
