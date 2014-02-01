
package com.nagopy.android.common.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class ViewUtil {

    /**
     * {@link TextView}に画像をセットする.<br>
     * 4.2以上なら
     * {@link TextView#setCompoundDrawablesRelative(Drawable, Drawable, Drawable, Drawable)}
     * 、4.2未満なら
     * {@link TextView#setCompoundDrawables(Drawable, Drawable, Drawable, Drawable)}
     * を実行する。
     * 
     * @param textView
     * @param leftOrStart
     * @param top
     * @param rightOrEnd
     * @param bottom
     */
    @SuppressLint("NewApi")
    public static void setCompoundDrawablesRelative(TextView textView, Drawable leftOrStart,
            Drawable top, Drawable rightOrEnd, Drawable bottom) {
        if (VersionUtil.isJBmr1OrLater()) {
            textView.setCompoundDrawablesRelative(leftOrStart, top, rightOrEnd, bottom);
        } else {
            textView.setCompoundDrawables(leftOrStart, top, rightOrEnd, bottom);
        }
    }
}
