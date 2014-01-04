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

package com.nagopy.android.common.pref;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.nagopy.android.common.view.CustomSeekBarGroup;

public class SeekBarPreference extends DialogPreference {

    private int max, min, scale, defValue;

    private CustomSeekBarGroup mCustomSeekBarGroup;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        max = attrs.getAttributeIntValue(null, "max", 100);
        min = attrs.getAttributeIntValue(null, "min", 0);
        scale = attrs.getAttributeIntValue(null, "scale", 1);
        defValue = attrs.getAttributeIntValue(null, "defValue", max);
    }

    @Override
    protected View onCreateDialogView() {
        mCustomSeekBarGroup = new CustomSeekBarGroup(getContext());
        mCustomSeekBarGroup
                .init(max, min, scale,
                        getPreferenceManager().getSharedPreferences().getInt(getKey(), defValue));
        return mCustomSeekBarGroup;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            getPreferenceManager().getSharedPreferences().edit()
                    .putInt(getKey(), mCustomSeekBarGroup.getIntValue()).apply();
        }
        super.onClick(dialog, which);
    }
}
