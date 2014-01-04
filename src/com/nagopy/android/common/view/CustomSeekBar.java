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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * 最小値の設定を可能にしたシークバー
 */
public class CustomSeekBar extends SeekBar {
    private int min = 0;
    private int scale = 1;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int def) {
        super(context, attrs, def);
        setup(attrs);
    }

    /**
     * やってはみたけどテストはしてないですｗｗ
     *
     * @param attrs
     */
    public void setup(AttributeSet attrs) {
        String NS = "http://schemas.android.com/apk/res/"
                + getContext().getPackageName();
        final int scale = attrs.getAttributeIntValue(NS, "scale", 1);
        final int max = attrs.getAttributeIntValue(NS, "max", 100);
        final int min = attrs.getAttributeIntValue(NS, "min", 1);
        final int defValue = attrs.getAttributeIntValue(NS, "defValue", 10);
        init(max, min, scale, defValue);
    }

    public synchronized void setMin(int min) {
        this.min = min * getScale();
        setMax(getMax());
    }

    public synchronized int getMin() {
        return this.min / getScale();
    }

    @Override
    public synchronized void setMax(int max) {
        super.setMax(max * getScale() - min);
    }

    // @Override
    // public synchronized int getMax() {
    // return super.getMax() / getScale() + getMin();
    // }

    public synchronized int getFixedMax() {
        return super.getMax() / getScale() + getMin();
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress * getScale() - min);
    }

    /**
     * バーの値を取得（{@link #getProgress()}だとminを考慮してくれないからこっちで！）
     *
     * @return
     */
    public synchronized int getIntValue() {
        return (getProgress() + min) / scale;
    }

    /**
     * バーの値を取得（{@link #getProgress()}だとminを考慮してくれないからこっちで！）
     *
     * @return
     */
    public synchronized float getFloatValue() {
        return ((float) (getProgress() + min)) / scale;
    }

    public synchronized int getScale() {
        if (scale < 1) {
            scale = 1;
        }
        return scale;
    }

    public synchronized void setScale(int scale) {
        if (scale < 1) {
            scale = 1;
        }
        this.scale = scale;
    }

    /**
     * @param max
     * @param min
     * @param x
     * @param defaultValue
     */
    public synchronized void init(int max, int min, int x, int defaultValue) {
        setScale(x);
        setMin(min);
        setMax(max);
        if (defaultValue == 0) {
            setProgress(1);
        }
        setProgress(defaultValue);
    }

}
