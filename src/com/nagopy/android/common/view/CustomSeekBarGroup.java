/*
 * Copyright (C) 2013 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.common.view;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nagopy.android.common.R;

public class CustomSeekBarGroup extends LinearLayout {

    private CustomSeekBar sb;

    private Button mButtonPlus, mButtonMinus;

    private OnCustomSeekBarChangedListner listener;

    public CustomSeekBarGroup(Context context) {
        super(context);
        setup(context);
    }

    public CustomSeekBarGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        setup(context);

        String NS = "http://schemas.android.com/apk/res/"
                + context.getPackageName();

        final int scale = attrs.getAttributeIntValue(NS, "scale", 1);
        final int max = attrs.getAttributeIntValue(NS, "max", 100);
        final int min = attrs.getAttributeIntValue(NS, "min", 1);
        int defValue = attrs.getAttributeIntValue(NS, "defValue",
                Integer.MIN_VALUE);
        // if (defValue == Integer.MIN_VALUE) {
        // int keyRedId = attrs.getAttributeResourceValue(NS, "defValue", 0);
        // SharedPreferences sharedPreferences = PreferenceManager
        // .getDefaultSharedPreferences(getContext());
        // defValue = sharedPreferences.getInt(getContext()
        // .getString(keyRedId),
        // (Integer) PreferenceUtil.DEFAULT_VALUE.get(keyRedId));
        // }
        init(max, min, scale, defValue);
    }

    public CustomSeekBarGroup(Context context,
            final OnCustomSeekBarChangedListner l) {
        super(context);
        setup(context);
        setOnCustomSeekBarChangedListner(l);
    }

    public void setOnCustomSeekBarChangedListner(OnCustomSeekBarChangedListner l) {
        listener = l;
    }

    private void setup(Context context) {
        View.inflate(context, R.layout.custom_seekbar_group, this);
        setPadding(5, 5, 5, 5);
        setGravity(Gravity.CENTER);
        mButtonPlus = (Button) findViewById(R.id.custom_seekbar_btn_plus);
        mButtonMinus = (Button) findViewById(R.id.custom_seekbar_btn_minus);
        final TextView tv = (TextView) findViewById(R.id.custom_seekbar_textview);
        sb = (CustomSeekBar) findViewById(R.id.custom_seekbar_seekbar);
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.onStopTrackingTouch(sb);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.onStartTrackingTouch(sb);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                int value = sb.getIntValue();
                tv.setText(createTextFormat(value));
                updateButtonEnabled(value);

                if (listener != null) {
                    listener.onCustomSeekBarChanged(sb, value);
                }
            }
        });
        mButtonPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = sb.getIntValue() + 1;
                sb.setProgress(newValue);
                updateButtonEnabled(newValue);
            }
        });
        mButtonMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = sb.getIntValue() - 1;
                sb.setProgress(newValue);
                updateButtonEnabled(newValue);
            }
        });
    }

    public void updateButtonEnabled(int newValue) {
        mButtonMinus.setEnabled(newValue > sb.getMin());
        mButtonPlus.setEnabled(newValue < sb.getFixedMax());
    }

    public void setMin(int min) {
        sb.setMin(min);
    }

    public void setMax(int max) {
        sb.setMax(max);
    }

    public void setScale(int x) {
        sb.setScale(x);
    }

    public int getScale() {
        return sb.getScale();
    }

    /**
     * @param max
     * @param min
     * @param x
     * @param defaultValue
     */
    public void init(int max, int min, int x, int defaultValue) {
        sb.init(max, min, x, defaultValue);
    }

    public void setValue(int value) {
        sb.setProgress(value);
    }

    public int getIntValue() {
        return sb.getIntValue();
    }

    public float getFloatValue() {
        return sb.getFloatValue();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        int count = getChildCount();
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            view.setEnabled(enabled);
        }
    }

    public static interface OnCustomSeekBarChangedListner {
        public abstract void onCustomSeekBarChanged(CustomSeekBar seekBar,
                int progress);

        public abstract void onStopTrackingTouch(CustomSeekBar seekBar);

        public abstract void onStartTrackingTouch(CustomSeekBar seekBar);
    }

    protected String createTextFormat(int value) {
        switch (sb.getScale()) {
            case 1:
                return String.valueOf(value);
            default:
                // TODO 何というか、このクラス自体作り直したいね
                double dispValue = (double) value / sb.getScale();
                // 10、100とか前提
                String format = "%." + (String.valueOf(sb.getScale()).length() - 1) + "f";
                return String.format(Locale.getDefault(), format, dispValue);
        }
    }
}
