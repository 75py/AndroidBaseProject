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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nagopy.android.common.R;
import com.nagopy.android.common.util.DimenUtil;
import com.nagopy.android.common.util.ImageUtil;

/**
 * アプリ選択ダイアログ.
 */
public class AppListPreference extends DialogPreference implements TextWatcher,
        OnCheckedChangeListener {

    private ListView mListView;
    private AppListAdapter mAdapter;
    private List<AppData> mAppList;

    public AppListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // ここでアプリを読み込むと、ページの表示に時間がかかってしまうので
        // onCreateDialogViewで読み込むようにする
    }

    @Override
    protected View onCreateDialogView() {
        Context context = getContext();

        if (mAdapter == null) {
            // 初回はアプリ一覧を読み込み、アダプタを作成
            mAdapter = makeAdapter();
        }
        // リストを作成
        mListView = new ListView(context);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setFastScrollEnabled(true);

        // リストにアダプタをセット
        mListView.setAdapter(mAdapter);

        // 前回のチェック状態を取得（ソート前に取得）
        updateCheckedLastTimeFlag();
        // アプリをソート
        mAdapter.sort();

        // 前回のチェック状態を反映
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            AppData appData = mAdapter.getItem(i);
            if (appData.checkedLastTime) {
                mListView.setItemChecked(i, true);
            }
        }

        // リストビューにリスナーをセット
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // チェック状態をAppDataに反映
                AppData appData = mAdapter.getItem(position);
                appData.checkedLastTime = !appData.checkedLastTime;
                // 表示を更新
                mAdapter.notifyDataSetChanged();
            }
        });

        // フィルター用のEditTextを作成
        EditText filterEditText = new EditText(context);
        filterEditText.addTextChangedListener(this);
        filterEditText.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);

        // 全チェックボタンを作成
        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(R.string.check_all_reverse);
        checkBox.setOnCheckedChangeListener(this);

        //
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = DimenUtil.getPixelFromDp(context, 4);

        // LinearLayoutを作成し、EditTextとListViewを並べる
        LinearLayout linearLayout = new LinearLayout(context);
        // 縦に並べる
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(filterEditText, params);
        linearLayout.addView(checkBox, params);
        linearLayout.addView(mListView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        return linearLayout;
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

        // アプリ一覧をフィールドにセット
        mAppList = items;

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
                for (AppData appData : mAppList) {
                    if (appData.checkedLastTime) {
                        checkedPkgs.add(appData.pkg);
                    }
                }
                edit.putStringSet(getKey(), checkedPkgs);

                edit.apply();
                break;
        }
        super.onClick(dialog, which);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // フィルタを更新
        mAdapter.getFilter().filter(s);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // チェックを全てONまたはOFF
        for (AppData appData : mAppList) {
            appData.checkedLastTime = isChecked;
        }

        // 表示中のもののチェック状態を切り替え
        int count = mListView.getCount();
        for (int i = 0; i < count; i++) {
            mListView.setItemChecked(i, isChecked);
        }

        // 一応更新
        mAdapter.notifyDataSetChanged();
    }

    private class AppListAdapter extends BaseAdapter implements Filterable {

        private PackageManager mPackageManager;
        private int mIconSize;
        private Filter mFilter;
        /** フィルタを掛ける前のオリジナルデータ */
        public List<AppData> mOriginalItems;
        /** フィルタ結果 */
        public List<AppData> mFilteredItems;

        public AppListAdapter(Context context, List<AppData> items) {
            mPackageManager = context.getPackageManager();
            mIconSize = ImageUtil.getIconSize(context);
            mOriginalItems = items;
            mFilteredItems = new ArrayList<AppListPreference.AppData>(items.size());
            for (AppData appData : items) {
                mFilteredItems.add(appData);
            }
        }

        @Override
        public int getCount() {
            return mFilteredItems.size();
        }

        @Override
        public AppData getItem(int position) {
            return mFilteredItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * アプリのソートを実行する.
         */
        public void sort() {
            Collections.sort(mOriginalItems, mAppComparator);
            Collections.sort(mFilteredItems, mAppComparator);
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults filterResults = new FilterResults();
                        if (StringUtils.isEmpty(constraint)) {
                            // テキストが空の場合、全てのアプリを表示
                            filterResults.values = mOriginalItems;
                            filterResults.count = mOriginalItems.size();
                            return filterResults;
                        } else {
                            // 何らかの入力がある場合、アプリ名・パッケージ名と比較してみる
                            List<AppData> filteredItems = new ArrayList<AppListPreference.AppData>();
                            for (int i = 0; i < mOriginalItems.size(); i++) {
                                AppData appData = mOriginalItems.get(i);
                                if (appData.name.contains(constraint)
                                        || appData.pkg.contains(constraint)) {
                                    // アプリ名、パッケージ名のいずれかに含まれていれば追加
                                    filteredItems.add(appData);
                                }
                            }
                            // 結果を返す
                            filterResults.values = filteredItems;
                            filterResults.count = filteredItems.size();
                            return filterResults;
                        }
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        // 変更を反映
                        mFilteredItems = (List<AppData>) results.values;
                        notifyDataSetChanged();

                        // 表示を更新後、チェック状態を反映する
                        for (int position = 0; position < getCount(); position++) {
                            mListView.setItemChecked(position, getItem(position).checkedLastTime);
                        }
                    }
                };
            }
            return mFilter;
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

            // チェックを入れる操作はここでやるとうまくいかないので別の場所で

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
