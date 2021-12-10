package com.example.arase.foodmanagementapp;

import android.provider.BaseColumns;

/**
 * データベースのテーブル名・項目名を定義しまとめるクラス
 */
public final class DBContract {
    // 誤ってインスタンス化しないようにコンストラクタをプライベート宣言
    /**
     * インスタンス化対策コンストラクタ
     */
    private DBContract() {}

    // テーブルの内容を定義
    /**
     * 食品テーブルの内容を定義するクラス
     */
    public static class FoodDBEntry implements BaseColumns {
        // BaseColumns インターフェースを実装することで、内部クラスは_IDを継承できる
        public static final String F_TABLE_NAME   = "food_tbl";
        public static final String F_CATEGORY_ID  = "categoryid";
        public static final String F_COLUMN_NAME1 = "foodname";
        public static final String F_COLUMN_NAME2 = "gramvalue";
        public static final String F_COLUMN_NAME3 = "quantity";
        public static final String F_COLUMN_NAME4 = "foodlimit";
        public static final String F_COLUMN_NAME5 = "displayfoodlimit";
    }

    /**
     * カテゴリテーブルの内容を定義するクラス
     */
    public static class CategoryDBEntry implements BaseColumns {
        // BaseColumns インターフェースを実装することで、内部クラスは_IDを継承できる
        public static final String C_TABLE_NAME   = "category_tbl";
        public static final String C_COLUMN_NAME1 = "category";

    }
}
