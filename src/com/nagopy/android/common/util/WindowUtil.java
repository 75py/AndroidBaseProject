
package com.nagopy.android.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class WindowUtil {

    private WindowUtil() {
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * ディスプレイの幅を取得する
     */
    public static int getDisplayWidth(Context context) {
        return getDisplaySizeInstance(getWindowManager(context).getDefaultDisplay())
                .getDisplayWidth();
    }

    /**
     * ディスプレイの高さを取得する
     */
    public static int getDisplayHeight(Context context) {
        return getDisplaySizeInstance(getWindowManager(context).getDefaultDisplay())
                .getDisplayHeight();
    }

    /**
     * ディスプレイの大きさを取得するためのクラスを取得する
     * 
     * @param display {@link WindowManager#getDefaultDisplay()}
     */
    private static DisplaySize getDisplaySizeInstance(Display display) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return new DisplaySizeHoneyComb(display);
        } else {
            return new DisplaySizeCompat(display);
        }
    }

    /**
     * ディスプレイの大きさを取得するためのインターフェース<br>
     * バージョンによって取得方法が変わる
     */
    private interface DisplaySize {
        /**
         * ディスプレイの幅を取得する
         */
        public int getDisplayWidth();

        /**
         * ディスプレイの高さを取得する
         */
        public int getDisplayHeight();
    }

    /**
     * API13以上
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static class DisplaySizeHoneyComb implements DisplaySize {
        private Point mPoint;

        public DisplaySizeHoneyComb(Display display) {
            mPoint = new Point();
            display.getSize(mPoint);
        }

        @Override
        public int getDisplayWidth() {
            return mPoint.x;
        }

        @Override
        public int getDisplayHeight() {
            return mPoint.y;
        }
    }

    /**
     * API13未満用
     */
    @SuppressWarnings("deprecation")
    private static class DisplaySizeCompat implements DisplaySize {
        private Display mDisplay;

        public DisplaySizeCompat(Display display) {
            mDisplay = display;
        }

        @Override
        public int getDisplayWidth() {
            return mDisplay.getWidth();
        }

        @Override
        public int getDisplayHeight() {
            return mDisplay.getHeight();
        }
    }

}
