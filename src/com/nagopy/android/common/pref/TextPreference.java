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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nagopy.android.common.util.DimenUtil;

/**
 * テキストを表示するだけのPreference.
 */
public class TextPreference extends Preference {

    /** 表示テキスト */
    private String mText;

    public TextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        int id = attrs.getAttributeResourceValue(null, "text", 0);
        mText = context.getString(id);

        // 選択不可に
        setSelectable(false);

        final String onClickMethodName = attrs.getAttributeValue(null, "onClick");
        if (StringUtils.isNotBlank(onClickMethodName)) {
            try {
                final Method method = getContext().getClass().getMethod(onClickMethodName,
                        Preference.class);
                setSelectable(true);
                setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        try {
                            return (Boolean) method.invoke(getContext(), preference);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public void setText(String text) {
        this.mText = text;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        TextView textView = new TextView(getContext());
        int padding = DimenUtil.getPixelFromDp(getContext(), 10);
        textView.setPadding(0, padding, 0, padding);
        textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);
        textView.setText(mText);
        return textView;
    }

}
