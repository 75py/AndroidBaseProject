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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.nagopy.android.common.util.ImageUtil;

/**
 * アプリ選択ダイアログ.
 */
public class AppListPreference extends DialogPreference {

    private ListView mListView;
    private AppListAdapter mAdapter;

    public AppListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // ここでアプリを読み込むと、ページの表示に時間がかかってしまうので
        // onCreateDialogViewで読み込むようにする
    }

    @Override
    protected View onCreateDialogView() {
        if (mAdapter == null) {
            // 初回はアプリ一覧を読み込み、アダプタを作成
            mAdapter = makeAdapter();
        }
        // リストを作成
        mListView = new ListView(getContext());
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setFastScrollEnabled(true);

        // リストにアダプタをセット
        mListView.setAdapter(mAdapter);

        // 前回のチェック状態を取得（ソート前に取得）
        updateCheckedLastTimeFlag();
        // アプリをソート
        mAdapter.sort(mAppComparator);

        // 前回のチェック状態を反映
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            AppData appData = mAdapter.getItem(i);
            if (appData.checkedLastTime) {
                mListView.setItemChecked(i, true);
            }
        }

        return mListView;
    }

    private AppListAdapter makeAdapter() {
        ArrayList<AppData> items = new ArrayList<AppData>();
        // パッケージマネージャーの作成
        PackageManager packageManager = getContext().getPackageManager();
        // アプリケーションの一覧
        List<ApplicationInfo> pkgList =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        // アプリケーション名の取得
        if (pkgList != null) {
            for (ApplicationInfo info : pkgList) {
                AppData appData = new AppData();
                // アプリ名、パッケージ名を取得
                appData.name = (String) info.loadLabel(packageManager);
                appData.pkg = info.packageName;
                // リストに追加
                items.add(appData);
            }
        }
        return new AppListAdapter(getContext(), items);
    }

    private void updateCheckedLastTimeFlag() {
        SharedPreferences sp = getSharedPreferences();
        Set<String> checkedPkgs = sp.getStringSet(getKey(), new HashSet<String>());
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            AppData appData = mAdapter.getItem(i);
            // 保存されている値に含まれているかを判定
            appData.checkedLastTime = checkedPkgs.contains(appData.pkg);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Editor edit = getPreferenceManager().getSharedPreferences().edit();

                // 選択されたデータを取得し、保存
                Set<String> checkedPkgs = new HashSet<String>();
                SparseBooleanArray positions = mListView.getCheckedItemPositions();
                for (int i = 0; i < positions.size(); i++) {
                    if (positions.valueAt(i)) {
                        int checkedPosition = positions.keyAt(i);
                        AppData appData = (AppData) mListView.getItemAtPosition(checkedPosition);
                        checkedPkgs.add(appData.pkg);
                    }
                }

                edit.putStringSet(getKey(), checkedPkgs);

                edit.apply();
                break;
        }
        super.onClick(dialog, which);
    }

    private class AppListAdapter extends ArrayAdapter<AppData> {

        private PackageManager mPackageManager;
        private int mIconSize;

        public AppListAdapter(Context context, List<AppData> items) {
            super(context, android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                    items);
            mPackageManager = context.getPackageManager();
            mIconSize = ImageUtil.getIconSize(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Context mContext = getContext();

            // ビューを受け取る
            View view = convertView;
            if (view == null) {
                // 受け取ったビューがnullなら新しくビューを生成
                view = View.inflate(mContext,
                        android.R.layout.simple_list_item_single_choice, null);
                CheckedTextView titleTextView = (CheckedTextView) view
                        .findViewById(android.R.id.text1);

                holder = new ViewHolder();
                holder.titleTextView = titleTextView;

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // 表示すべきデータの取得
            AppData appData = (AppData) getItem(position);
            holder.titleTextView.setText(appData.name);
            // フォントのキャッシュがなければ新規作成
            if (appData.iconCache == null) {
                try {
                    appData.iconCache = mPackageManager.getApplicationIcon(appData.pkg);
                    appData.iconCache.setBounds(0, 0, mIconSize, mIconSize);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            // アイコンを表示
            holder.titleTextView.setCompoundDrawables(appData.iconCache,
                    null, null, null);

            // チェックを入れるべきか
            // 不要かも？

            return view;
        }

    }

    private static class ViewHolder {
        public CheckedTextView titleTextView;
    }

    public static class AppData {

        @Override
        public String toString() {
            return "AppData [name=" + name + ", iconCache=" + iconCache + ", pkg=" + pkg
                    + ", checkedLastTime=" + checkedLastTime + "]";
        }

        public String name;
        public Drawable iconCache;
        public String pkg;

        /**
         * 保存されているパッケージをソートで上にもっていくために使用するフィールド.
         */
        public Boolean checkedLastTime;
    }

    private Comparator<AppData> mAppComparator = new Comparator<AppData>() {
        @Override
        public int compare(AppData lhs, AppData rhs) {
            Boolean checked0 = lhs.checkedLastTime;
            Boolean checked1 = rhs.checkedLastTime;
            if (checked0.equals(checked1)) {
                // どちらもデフォルトでチェックあり
                // または、デフォルトでチェックなしの場合

                // アプリ名で比較
                String label0 = lhs.name;
                String label1 = rhs.name;
                int ret = label0.compareToIgnoreCase(label1);

                if (ret == 0) {
                    // アプリ名に差異がない場合は、パッケージ名で比較
                    String pkgName0 = lhs.pkg;
                    String pkgName1 = rhs.pkg;
                    ret = pkgName0.compareToIgnoreCase(pkgName1);
                }
                return ret;
            } else {
                // どちらかにチェックが入っている場合
                if (checked0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    };
}
