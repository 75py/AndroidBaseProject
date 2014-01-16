
package com.nagopy.android.common.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * //TODO
 * 
 * @author 75py <dev.75py@gmail.com>
 */
public abstract class AbsFloatViewWrapper {

    protected WindowManager mWindowManager;
    public LayoutParams params;
    public View view;
    private boolean showing;
    private Context mContext;

    public AbsFloatViewWrapper(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mContext = context;
    }

    protected Context getContext() {
        return this.mContext;
    }

    /**
     * オーバーレイ表示を開始する.
     */
    public synchronized void show() {
        if (!isShowing()) {
            mWindowManager.addView(view, params);
            setShowing(true);
        }
    }

    /**
     * オーバーレイ表示を終了する.
     */
    public synchronized void hide() {
        if (isShowing()) {
            mWindowManager.removeView(view);
            setShowing(false);
        }
    }

    /**
     * オーバーレイ表示を行っているかどうかを取得する.
     * 
     * @return 表示中ならtrue
     */
    public synchronized boolean isShowing() {
        return showing;
    }

    /**
     * @param showing
     */
    public synchronized void setShowing(boolean showing) {
        this.showing = showing;
    }

    public void setTouchable(boolean touchable) {
        if (touchable) {
            params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
        }
        updateLayoutParams();
    }

    public void updateLayoutParams() {
        if (isShowing()) {
            mWindowManager.updateViewLayout(view, params);
        }
    }

    /**
     * {@link WindowManager.LayoutParams}郢ｧ螳夲ｽｿ譁絶�<br>
     * 
     * @param touchable
     *            郢ｧ�ｿ郢晢ｿｽ繝｡郢ｧ�､郢晏生ﾎｦ郢晏現�定愾髢�ｽｾ蜉ｱ笘�ｹｧ荵敖ｰ邵ｺ�ｩ邵ｺ�ｽﾂｰ邵ｲ�ｵrue邵ｺ�
     *            ｪ郢ｧ谺拑PE_SYSTEM_ALERT邵ｲ�ｽ
     *            false邵ｺ�ｪ郢ｧ谺拑PE_SYSTEM_OVERLAY邵ｺ�ｫ邵ｺ蜷ｶ�狗ｸｲ�ｽbr>
     *            陷大ｴ趣ｿｽ邵ｺ�ｯ郢晢ｽｭ郢晢ｿｽ縺鷹��ｻ鬮ｱ�｢邵ｺ�ｧ邵ｺ�ｯ髯ｦ�ｨ驕会ｽｺ闕ｳ讎雁ｺ�
     * @param width 陝ｷ�ｽ�帝坎�ｭ陞ｳ螢ｹ�ｽ
     *            <ul>
     *            <li>{@link WindowManager.LayoutParams.MATCH_PARENT}</li>
     *            <li>{@link WindowManager.LayoutParams.WRAP_CONTENT}</li>
     *            </ul>
     * @param height 鬯ｮ蛟･��ｹｧ螳夲ｽｨ�ｭ陞ｳ�ｽ
     * @return 
     *         郢昜ｻ｣ﾎ帷ｹ晢ｽ｡郢晢ｽｼ郢ｧ�ｿ邵ｺ�ｧ隰厄ｿｽ�ｮ螢ｹ��ｸｺ�ｦ闖ｴ諛岩夢邵ｺ貅倥′郢晄じ縺夂ｹｧ�ｧ郢ｧ�ｯ郢晏現�帝恆譁絶
     *         �
     */
    public static WindowManager.LayoutParams createLayoutParams(boolean touchable, int width,
            int height) {
        WindowManager.LayoutParams params = new LayoutParams();
        params.format = PixelFormat.TRANSLUCENT;

        if (touchable) {
            params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
        }

        params.width = width;
        params.height = height;

        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        return params;
    }
}
