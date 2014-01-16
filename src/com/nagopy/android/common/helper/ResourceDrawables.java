package com.nagopy.android.common.helper;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * <p>
 * Drawableをリソースから取得するクラス。<br>
 * API15以上の場合は解像度を考慮。
 * </p>
 * <h3>使い方</h3> <code>
 * ResourceDrawables resourcesDrawables = ResourceDrawables.getInstance(context);<br>
 * resourcesDrawables.getDrawable(R.drawable.ic_launcher);
 * </code>
 * @author 75py <dev.75py@gmail.com>
 * 
 */
public abstract class ResourceDrawables {
	protected Resources mResources;

	ResourceDrawables(Resources resources) {
		mResources = resources;
	}

	/**
	 * <p>
	 * Drawableをリソースから取得する<br>
	 * API15以上の場合は解像度を加えたメソッドを実行。
	 * </p>
	 * @param id
	 * @return
	 */
	public abstract Drawable getDrawable(int id);

	/**
	 * インスタンスの取得<br>
	 * API15以上ならGetDrawableIcsMr1クラス、そうでなければGetDrawableCompatクラスを返す<br>
	 * でもどっちもGetResourcesDrawableクラスとして扱えばおっけー
	 * @param context
	 * @return
	 */
	public static ResourceDrawables getInstance(Context context, Resources resources) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			return new ResourceDrawablesIcsMr1(context, resources);
		} else {
			return new ResourceDrawablesCompat(resources);
		}
	}

	static class ResourceDrawablesCompat extends ResourceDrawables {

		ResourceDrawablesCompat(Resources resources) {
			super(resources);
		}

		@Override
		public Drawable getDrawable(int id) {
			return mResources.getDrawable(id);
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	static class ResourceDrawablesIcsMr1 extends ResourceDrawables {

		private final int iconDensity;

		ResourceDrawablesIcsMr1(Context context, Resources resources) {
			super(resources);
			iconDensity = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
					.getLauncherLargeIconDensity();
		}

		@Override
		public Drawable getDrawable(int id) {
			return mResources.getDrawableForDensity(id, iconDensity);
		}
	}
}
