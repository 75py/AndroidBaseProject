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

import android.os.Build;

public class VersionUtil {

    private VersionUtil() {
    }

    /**
     * 4.1以上ならtrueを返す.<br>
     * {@link Build.VERSION_CODES#JELLY_BEAN}
     */
    public static Boolean isJBOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 4.2以上ならtrueを返す.<br>
     * {@link Build.VERSION_CODES#JELLY_BEAN_MR1}
     */
    public static Boolean isJBmr1OrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * 4.3以上ならtrueを返す.<br>
     * {@link Build.VERSION_CODES#JELLY_BEAN_MR2}
     */
    public static Boolean isJBmr2OrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * 4.4以上ならtrueを返す.<br>
     * {@link Build.VERSION_CODES#KITKAT}
     */
    public static Boolean isKitKatOrLator() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

}
