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

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.nagopy.android.common.R;

public class DateFormatEditPreference extends EditTextPreference {

    public DateFormatEditPreference(Context context) {
        super(context);
    }

    public DateFormatEditPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateFormatEditPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE && !isValidPattern()) {
            Toast.makeText(getContext(), R.string.msg_invalidDateFormat, Toast.LENGTH_SHORT).show();
            return;
        }

        super.onClick(dialog, which);
    }

    @SuppressWarnings("unused")
    @SuppressLint("SimpleDateFormat")
    private Boolean isValidPattern() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getEditText().getText()
                    .toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
