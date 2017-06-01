package com.object173.geotwitter.gui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.object173.geotwitter.R;

public final class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener {

    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;

    private final int mDefaultValue;
    private final int mMaxValue;
    private final int mMinValue;
    private final String mSummary;

    private int mCurrentValue;

    private TextView mValueText;

    public SeekBarPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        if(context == null || attrs == null) {
            throw new NullPointerException();
        }
        @SuppressLint("Recycle")
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);

        mMinValue = array.getInteger(R.styleable.SeekBarPreference_minValue, DEFAULT_MIN_VALUE);
        mMaxValue = array.getInteger(R.styleable.SeekBarPreference_maxValue, DEFAULT_MAX_VALUE);
        mDefaultValue = array.getInteger(R.styleable.SeekBarPreference_defaultSeekValue, DEFAULT_CURRENT_VALUE);
        mSummary = array.getString(R.styleable.SeekBarPreference_summary);
    }

    @Override
    protected View onCreateDialogView() {
        mCurrentValue = getPersistedInt(mDefaultValue);

        final LayoutInflater inflater = LayoutInflater.from (getContext());
        final View view = inflater.inflate(R.layout.dialog_preference_slider, null);

        ((TextView) view.findViewById(R.id.min_value)).setText(String.valueOf(mMinValue));
        ((TextView) view.findViewById(R.id.max_value)).setText(String.valueOf(mMaxValue));

        final SeekBar mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);

        mValueText = (TextView) view.findViewById(R.id.current_value);
        mValueText.setText(String.valueOf(mCurrentValue));

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult) {
            return;
        }

        if (shouldPersist()) {
            persistInt(mCurrentValue);
        }

        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        final int value = getPersistedInt(mDefaultValue);
        return mSummary + " " + Integer.toString(value);
    }

    @Override
        public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        mCurrentValue = value + mMinValue;
        mValueText.setText(String.valueOf(mCurrentValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seek) {
    }
}