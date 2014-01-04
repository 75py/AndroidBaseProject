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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.nagopy.android.common.R;
import com.nagopy.android.common.util.DimenUtil;

/**
 * フォント選択ダイアログ.
 */
public class FontListPreference extends DialogPreference {

    /** フォント表示用のリストビュー */
    private ListView mListView;
    /** フォント表示用 */
    private FontListAdapter mAdapter;
    /** システムフォント 表示名 */
    private String[] systemFontFamilyDispNames;
    /** システムフォント Typeface作成で使用する文字列 */
    private String[] systemFontFamilyNames;
    /** ダイアログでデフォルトで選択するフォントのフォント名 */
    private String mDefFontName;
    /** ダイアログでデフォルトで選択するフォントのフォント区分 */
    private FontKbn mDefFontKbn;
    /** ダイアログでデフォルトで選択するフォントのフォントスタイル */
    private Integer mDefFontStyle;
    /** チェック位置. */
    private Integer mCheckedPosition;
    private static final Integer POSITION_UNINITIALIZED = -1;

    /** プレビュー表示用のテキストビュー */
    private TextView mPreviewTextView;

    private String mAttribute_previewDateFormatKey;
    private String mAttribute_previewForceEnglishKey;
    private String mAttribute_previewText;

    public FontListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // XMLの値をフィールドにセット
        mAttribute_previewDateFormatKey = attrs.getAttributeValue(null, "preview_date_format_key");
        mAttribute_previewForceEnglishKey = attrs.getAttributeValue(null,
                "preview_force_english_key");
        mAttribute_previewText = attrs.getAttributeValue(null, "preview_text");

        // フォントファイルの置き場所を表示
        File fontsDir = context.getExternalFilesDir("fonts");
        if (!fontsDir.exists()) {
            fontsDir.mkdirs();
        }
        setSummary(context.getString(R.string.pref_summary_font_list_preference,
                fontsDir.getAbsolutePath()));

        // ここでフォントの読み込みを行うと表示ラグが生じるので
        // onCreateDialogViewの前で読み込む
    }

    private FontListAdapter prepareAdapter() {
        Context context = getContext();
        ArrayList<FontData> items = new ArrayList<FontData>();

        // SDカード内
        try {
            File fontsDir = context.getExternalFilesDir("fonts");
            for (File file : fontsDir.listFiles()) {
                String dispName = file.getName();
                String fontName = file.getAbsolutePath();
                FontKbn fontKbn = FontKbn.SDCARD;
                FontData data = new FontData(fontKbn, dispName, fontName, Typeface.NORMAL);
                // items.addAll(makeFontStyleList(data));
                items.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 諦めて次へ
        }

        // assets/fonts/のフォント
        try {
            String[] assetsFileList = context.getResources().getAssets().list("fonts");
            for (String fileName : assetsFileList) {
                String dispName = fileName;
                String fontName = "fonts/" + fileName;
                FontData assetFont = new FontData(FontKbn.ASSETS, dispName, fontName,
                        Typeface.NORMAL);
                // items.addAll(makeFontStyleList(assetFont));
                items.add(assetFont);
            }
        } catch (IOException e) {
            // 何もしない
        }

        // 標準システムフォント
        systemFontFamilyDispNames = context.getResources().getStringArray(
                R.array.typeface_family_name);
        systemFontFamilyNames = context.getResources().getStringArray(
                R.array.typeface_family_name_value);
        for (int i = 0; i < systemFontFamilyDispNames.length; i++) {
            String dispName = systemFontFamilyDispNames[i];
            String fontName = systemFontFamilyNames[i];
            FontData systemFont = new FontData(FontKbn.SYSTEM, dispName, fontName,
                    Typeface.NORMAL);
            items.addAll(makeFontStyleList(systemFont));
        }

        return new FontListAdapter(context, items);
    }

    private List<FontData> makeFontStyleList(FontData fontData) {
        List<FontData> list = new ArrayList<FontListPreference.FontData>();
        list.add(fontData);
        list.add(new FontData(fontData.fontKbn, fontData.dispName + "(bold)", fontData.fontName,
                Typeface.BOLD));
        list.add(new FontData(fontData.fontKbn, fontData.dispName + "(italic)", fontData.fontName,
                Typeface.ITALIC));
        list.add(new FontData(fontData.fontKbn, fontData.dispName + "(bold + italic)",
                fontData.fontName,
                Typeface.BOLD_ITALIC));
        return list;
    }

    @Override
    protected View onCreateDialogView() {
        if (mAdapter == null) {
            // フォント読み込みを実施し、アダプターを作成する
            mAdapter = prepareAdapter();
        }

        // デフォルト選択位置を初期化
        mCheckedPosition = POSITION_UNINITIALIZED;
        // デフォルト値をフィールドにセット
        SharedPreferences sp = getSharedPreferences();
        mDefFontKbn = FontKbn.valueOf(sp.getString(makeFontKbnKey(getKey()),
                FontKbn.SYSTEM.name()));
        mDefFontName = sp.getString(makeFontNameKey(getKey()), systemFontFamilyNames[0]);
        mDefFontStyle = sp.getInt(makeFontStyleKey(getKey()), Typeface.NORMAL);

        // プレビュー用のテキストビューを作成
        mPreviewTextView = new TextView(getContext());
        mPreviewTextView.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
        // プレビュー用の文字列をセット
        mPreviewTextView.setText(makePreviewText());
        // padding調整
        int paddingVertical = DimenUtil.getPixelFromDp(getContext(), 10);
        int paddingHorizon = DimenUtil.getPixelFromDp(getContext(), 16);
        mPreviewTextView.setPadding(paddingHorizon, paddingVertical, paddingHorizon,
                paddingVertical);

        // リストを作成
        mListView = new ListView(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // リストにリスナーをセット
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // チェック位置をフィールドにセット
                mCheckedPosition = position;
                // チェックを反映
                mListView.setItemChecked(position, true);
                // プレビューのフォントを反映
                mPreviewTextView.setTypeface(mAdapter.getItem(position).cacheTypeface);
            }
        });

        // プレビューとリストのビューを作成
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout
                .addView(mPreviewTextView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.addView(mListView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        return linearLayout;
    }

    private String makePreviewText() {
        // プレビューのテキストをXMLから取得
        String dateFormatKey = mAttribute_previewDateFormatKey;
        if (!TextUtils.isEmpty(dateFormatKey)) {
            // 日付フォーマットの保存キーが指定された場合
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            String dateFormat = sp.getString(
                    dateFormatKey, "yyyy/MM/dd(E) HH:mm:ss");
            // 英語表記の有無を確認
            String forceEnglishKey = mAttribute_previewForceEnglishKey;
            Boolean forceEnglish = TextUtils.isEmpty(forceEnglishKey) ? false : sp.getBoolean(
                    forceEnglishKey, false);
            Locale locale = forceEnglish ? Locale.ENGLISH : Locale.getDefault();
            // 現在時刻の表示をプレビュー文字にする
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
            return simpleDateFormat.format(new Date());
        } else {
            // プレビュー文字指定がある場合
            String previewText = mAttribute_previewText;
            if (!TextUtils.isEmpty(previewText)) {
                return previewText;
            } else {
                // XMLに指定がない場合は、現在時刻を表示する
                Time time = new Time();
                return DateUtils.formatDateTime(getContext(), time.toMillis(false),
                        DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME);
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                // 選択された値を保存
                Editor edit = getPreferenceManager().getSharedPreferences().edit();
                // 選択されたデータを取得
                FontData fontData = mAdapter.getItem(mListView.getCheckedItemPosition());
                String baseKey = getKey();

                // フォント区分を保存
                edit.putString(makeFontKbnKey(baseKey), fontData.fontKbn.name());
                // フォント名を保存
                edit.putString(makeFontNameKey(baseKey), fontData.fontName);
                // フォントスタイルを保存
                edit.putInt(makeFontStyleKey(baseKey), fontData.fontStyle);

                edit.apply();
                break;
        }
        super.onClick(dialog, which);
    }

    private class FontListAdapter extends ArrayAdapter<FontData> {

        public FontListAdapter(Context context, List<FontData> items) {
            super(context, android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                    items);
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
            FontData fontData = (FontData) getItem(position);
            holder.titleTextView.setText(fontData.dispName);
            // フォントのキャッシュがなければ新規作成
            if (fontData.cacheTypeface == null) {
                fontData.cacheTypeface = makeTypeface(mContext.getResources().getAssets(),
                        fontData.fontKbn.name(), fontData.fontName, fontData.fontStyle);
            }
            // フォントを適用
            holder.titleTextView.setTypeface(fontData.cacheTypeface);

            // チェックを入れるべきか
            if (mCheckedPosition.equals(POSITION_UNINITIALIZED)) {
                // チェック位置が初期化されていない場合
                // デフォルト値をもとに、チェックすべき位置かどうかを判定する
                if (fontData.fontKbn == mDefFontKbn
                        && StringUtils.equals(fontData.fontName, mDefFontName)
                        && fontData.fontStyle != null && mDefFontStyle != null
                        && fontData.fontStyle.equals(mDefFontStyle)) {
                    // チェックを入れる
                    mListView.setItemChecked(position, true);
                    // チェック位置のフィールドにセット
                    mCheckedPosition = position;
                    // プレビューにフォントを反映
                    mPreviewTextView.setTypeface(fontData.cacheTypeface);
                } else {
                    // 不要にも見えるが、ビューが再利用されるため明示的にオフにする必要がある
                    mListView.setItemChecked(position, false);
                }
            } else {
                // チェック位置の初期化が済んでいる場合は、その値を現在の位置を比較する
                if (mCheckedPosition.equals(position)) {
                    // チェックを入れる
                    mListView.setItemChecked(position, true);
                } else {
                    // 不要にも見えるが、ビューが再利用されるため明示的にオフにする必要がある
                    mListView.setItemChecked(position, false);
                }
            }

            return view;
        }

    }

    private static class ViewHolder {
        public CheckedTextView titleTextView;
    }

    public static class FontData {
        @Override
        public String toString() {
            return "FontData [dispName=" + dispName + ", fontKbn=" + fontKbn + ", fontStyle="
                    + fontStyle + ", fontName=" + fontName + "]";
        }

        public String dispName;
        public FontKbn fontKbn;
        public Integer fontStyle;
        public String fontName;

        public Typeface cacheTypeface;

        public FontData(FontKbn fontKbn, String dispName, String fontName, Integer fontStyle) {
            this.fontKbn = fontKbn;
            this.dispName = dispName;
            this.fontName = fontName;
            this.fontStyle = fontStyle;
        }
    }

    /** フォントの区分 */
    public enum FontKbn {
        DEFAULT, SYSTEM, ASSETS, SDCARD
    }

    public static String makeFontKbnKey(String baseKey) {
        return baseKey + "_kbn";
    }

    public static String makeFontNameKey(String baseKey) {
        return baseKey + "_name";
    }

    public static String makeFontStyleKey(String baseKey) {
        return baseKey + "_style";
    }

    /**
     * {@link FontListPreference}で保存したフォントを作成する.
     * 
     * @param sp {@link SharedPreferences}
     * @param assetManager {@link AssetManager}
     * @param baseKey キー
     * @return {@link Typeface}
     */
    public static Typeface makeTypeface(SharedPreferences sp, AssetManager assetManager,
            String baseKey) {
        String fontKbnString = sp.getString(makeFontKbnKey(baseKey), FontKbn.DEFAULT.name());
        String fontName = sp.getString(makeFontNameKey(baseKey), null);
        Integer fontStyle = sp.getInt(makeFontStyleKey(baseKey), Typeface.NORMAL);

        return makeTypeface(assetManager, fontKbnString, fontName, fontStyle);
    }

    /**
     * {@link FontListPreference}で保存したフォントを作成する.
     * 
     * @param assetManager {@link AssetManager}
     * @param fontKbn フォント区分
     * @param fontName フォント名
     * @param fontStyle スタイル
     * @return {@link Typeface}
     */
    public static Typeface makeTypeface(AssetManager assetManager, String fontKbn, String fontName,
            Integer fontStyle) {
        try {
            FontKbn defFontKbn = FontKbn.valueOf(fontKbn);

            switch (defFontKbn) {
                case DEFAULT:
                    return Typeface.create(Typeface.DEFAULT, fontStyle);
                case SYSTEM:
                    return Typeface.create(fontName, fontStyle);
                case ASSETS:
                    return Typeface.create(Typeface.createFromAsset(assetManager, fontName),
                            fontStyle);
                case SDCARD:
                    return Typeface.create(Typeface.createFromFile(fontName), fontStyle);
                default:
                    return null;
            }
        } catch (Exception e) {
            // 何らかのエラーがあった場合は、デフォルトのフォントを返してお茶を濁す
            e.printStackTrace();
            return Typeface.DEFAULT;
        }
    }
}
