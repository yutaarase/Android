package com.example.arase.foodmanagementapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import java.text.ParseException;

/**
 * list設定クラス
 */
public final class ListAdapter {

    /**
     * エラー対策コンストラクタ
     */
    private ListAdapter() {}

    /**
     * Foodlist設定クラス
     */
    public static class FListAdapter extends SimpleCursorAdapter {
        Cursor cursor;

        /**
         * コンストラクタ
         * @param context コンテキス
         * @param listlayout レイアウト
         * @param c Cursor:Databaseのデータ
         * @param from 取得する項目
         * @param to データを設定するレイアウトのフィールド
         * @param flags アダプタの動作を決定するために使用される
         */
        public FListAdapter(Context context, int listlayout, Cursor c, String[] from, int[] to, int flags) {
            super(context, listlayout, c, from, to, flags);
            cursor = c;
            cursor.moveToFirst();
        }


        /**
         * 指定データのview取得するメソッド
         * @param position セットするviewのリソースの位置
         * @param convertView 描画データ
         * @param parent　listのフィールドに含まれるlayoutのデータ
         * @return 描画するView
         */
        // 指定データのビューを取得
        @SuppressLint("Range")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if(cursor.getPosition() < cursor.getCount()) {
                try {
                    DateAdapter DAdapter = new DateAdapter();
                    int  result = DateAdapter.differenceDays(cursor.getString(cursor.getColumnIndex(DBContract.FoodDBEntry.F_COLUMN_NAME5)), DAdapter.getToday());
                    if (result < -4) {
                        // 背景色を変える
                        view.setBackgroundColor(Color.parseColor("#ae8585"));
                    } else if (result < 0) {
                        view.setBackgroundColor(Color.parseColor("#fff7b1"));
                    } else if (result < 7) {
                        // 背景色を変える
                        view.setBackgroundColor(Color.parseColor("#ffddbd"));
                    } else {
                        // 背景色を変える
                        view.setBackgroundColor(Color.WHITE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return view;
        }
    }

    /**
     * Categorylist設定クラス
     */
    public static class CListAdapter extends SimpleCursorAdapter {
        /**
         * コンストラクタ
         * @param context コンテキス
         * @param listlayout レイアウト
         * @param c Cursor:Databaseのデータ
         * @param from 取得する項目
         * @param to データを設定するレイアウトのフィールド
         * @param flags アダプタの動作を決定するために使用される
         */
        public CListAdapter(Context context, int listlayout, Cursor c, String[] from, int[] to, int flags) {
            super(context, listlayout, c, from, to, flags);
        }


        /**
         * 指定データのview取得するメソッド
         * @param position セットするviewのリソースの位置
         * @param convertView 描画データ
         * @param parent　listのフィールドに含まれるlayoutのデータ
         * @return 描画するView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }
}

