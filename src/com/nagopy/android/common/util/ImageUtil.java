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

import java.io.IOException;
import java.io.InputStream;

import com.nagopy.android.common.helper.ResourceDrawables;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class ImageUtil {

    public static int getIconSize(Context context) {
        return (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
    }

    public static Drawable getDrawable(Context context, int resId) {
        return ResourceDrawables.getInstance(context, context.getResources()).getDrawable(resId);
    }

    public static Bitmap reduceByMatrix(Bitmap bitmap, int width, int height) {
        if (bitmap.getWidth() > width || bitmap.getHeight() > height) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            float newScale = Math.min((float) width / w, (float) height / h);
            // 最終的なサイズにするための縮小率を求める
            Matrix matrix = new Matrix();
            matrix.postScale(newScale, newScale);
            // 画像変形用のオブジェクトに拡大・縮小率をセットし
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }
        return bitmap;
    }

    /**
     * BitmapをDrawableに変換
     * 
     * @param bitmap
     * @return
     */
    public static Drawable getDrawable(Resources res, Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(res, bitmap);
        return drawable;
    }

    /**
     * DrawableをBitmapに変換
     * 
     * @param drawable
     * @return
     */
    public static Bitmap getBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    /** simply resizes a given drawable resource to the given width and height */
    public static Drawable resizeImage(Context context, Drawable drawable, int iconWidth,
            int iconHeight) {

        // load the origial Bitmap
        Bitmap BitmapOrg = ((BitmapDrawable) drawable).getBitmap();

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = iconWidth;
        int newHeight = iconHeight;

        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        // BitmapOrg.recycle();

        // make a Drawable from Bitmap to allow to set the Bitmap
        // to the ImageView, ImageButton or what ever
        Drawable result = getDrawable(context.getResources(), resizedBitmap);
        // resizedBitmap.recycle();
        return result;

    }

    public static Bitmap loadBitmap(ContentResolver contentResolver, Uri uri, int width, int height)
            throws IOException {
        // まずはサイズを取得
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream in = contentResolver.openInputStream(uri);
        BitmapFactory.decodeStream(in, null, options);
        in.close();

        // 良い感じのスケールを計算
        int scaleW = options.outWidth / width + 1;
        int scaleH = options.outHeight / height + 1;

        // 縮尺は整数値で、2なら画像の縦横のピクセル数を1/2にしたサイズ。
        // 3なら1/3にしたサイズで読み込まれます。
        int scale = Math.max(scaleW, scaleH);

        Log.d("debug", "width:" + width + " scaleW:" + scaleW + " scaleH:" + scaleH + " scale:"
                + scale
                + " options.outWidth:" + options.outWidth + " options.outHeight:"
                + options.outHeight);

        // 今度は画像を読み込みたいのでfalseを指定
        options.inJustDecodeBounds = false;

        // 先程計算した縮尺値を指定
        options.inSampleSize = scale;

        // 読み込み
        in = contentResolver.openInputStream(uri);
        Bitmap icon = BitmapFactory.decodeStream(in, null, options);
        in.close();

        Log.d("debug", "width:" + icon.getWidth() + " height:" + icon.getHeight());

        if (icon.getWidth() > width || icon.getHeight() > height) {
            int w = icon.getWidth();
            int h = icon.getHeight();
            float newScale = Math.min((float) width / w, (float) height / h);
            // 最終的なサイズにするための縮小率を求める
            Matrix matrix = new Matrix();
            matrix.postScale(newScale, newScale);
            // 画像変形用のオブジェクトに拡大・縮小率をセットし
            icon = Bitmap.createBitmap(icon, 0, 0, w, h, matrix, true);
            Log.d("debug", "width:" + icon.getWidth() + " height:" + icon.getHeight());
        }

        return icon;
    }
}
