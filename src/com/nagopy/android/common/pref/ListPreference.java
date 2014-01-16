
package com.nagopy.android.common.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;

public class ListPreference extends android.preference.ListPreference implements
        OnPreferenceChangeListener {

    public ListPreference(Context context) {
        super(context);
        init(context);
    }

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        setSummary(getSelectedValueText(sp.getString(getKey(), null)));

        setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setSummary(getSelectedValueText(newValue.toString()));
        return true;
    }

    private CharSequence getSelectedValueText(CharSequence value) {
        CharSequence[] names = getEntries();
        CharSequence[] values = getEntryValues();
        for (int i = 0; i < names.length; i++) {
            if (TextUtils.equals(values[i], value)) {
                return names[i];
            }
        }
        return null;
    }

    @Override
    public void setOnPreferenceChangeListener(
            final OnPreferenceChangeListener onPreferenceChangeListener) {
        final OnPreferenceChangeListener old = getOnPreferenceChangeListener();
        if (old == null) {
            super.setOnPreferenceChangeListener(onPreferenceChangeListener);
        } else {
            OnPreferenceChangeListener l = new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean retOld = old.onPreferenceChange(preference, newValue);
                    if (!retOld) {
                        // falseが返ってくる場合、次には送らないで終了
                        return false;
                    }

                    return onPreferenceChangeListener.onPreferenceChange(preference, newValue);
                }
            };
            super.setOnPreferenceChangeListener(l);
        }
    }

}
