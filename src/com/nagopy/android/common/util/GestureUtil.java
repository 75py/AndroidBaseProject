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

package com.nagopy.android.common.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GestureUtil {

    private GestureUtil() {
    }

    public static OnTouchListener makeOnTouchListener(Context context, OnGestureListener listener) {
        return makeOnTouchListener(context, listener, null);
    }

    public static OnTouchListener makeOnTouchListener(Context context, OnGestureListener listener,
            OnDoubleTapListener doubleTapListener) {
        final GestureDetector gestureDetector = new GestureDetector(context, listener);
        if (doubleTapListener != null) {
            gestureDetector.setOnDoubleTapListener(doubleTapListener);
        }

        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        };
    }
}