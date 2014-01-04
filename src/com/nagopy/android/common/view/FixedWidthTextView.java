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
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 幅を「100%」の文字列の幅になるようにしたTextView
 *
 * @author 75py <dev.75py@gmail.com>
 */
public class FixedWidthTextView extends TextView {

    public FixedWidthTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint paint = new Paint();
        paint.setTextSize(getTextSize());
        float textWidth = paint.measureText("100%");
        setWidth((int) textWidth);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

}
