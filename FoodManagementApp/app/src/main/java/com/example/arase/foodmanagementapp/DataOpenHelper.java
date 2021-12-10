package com.example.arase.foodmanagementapp;

import static com.example.arase.foodmanagementapp.DBContract.CategoryDBEntry;
import static com.example.arase.foodmanagementapp.DBContract.FoodDBEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// データベースをアプリから使用するために、 SQLiteOpenHelperを継承する
// SQLiteOpenHelperは、データベースやテーブルが存在する場合はそれを開き、存在しない場合は作成してくれる
/**
 * Databaseを扱うクラス
 */
public class DataOpenHelper extends SQLiteOpenHelper {
    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;

    // データベース名
    private static final String DATABASE_NAME = "MyDB.db";

    //　食品テーブルCREATE文
    private static final String SQL_CREATE_FOOD =
            "CREATE TABLE " + FoodDBEntry.F_TABLE_NAME + " (" +
                    FoodDBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FoodDBEntry.F_COLUMN_NAME1 + " TEXT," +
                    FoodDBEntry.F_COLUMN_NAME2 + " INTEGER," +
                    FoodDBEntry.F_COLUMN_NAME3 + " INTEGER," +
                    FoodDBEntry.F_COLUMN_NAME4 + " INTEGER," +
                    FoodDBEntry.F_COLUMN_NAME5 + " TEXT," +
                    FoodDBEntry.F_CATEGORY_ID + " INTEGER)";

    // カテゴリテーブルCREATE文
    private static final String SQL_CREATE_CATEGORY =
            "CREATE TABLE " + CategoryDBEntry.C_TABLE_NAME + " (" +
                    CategoryDBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CategoryDBEntry.C_COLUMN_NAME1 + " TEXT)";

    // 食品テーブルDROPE文
    private static final String SQL_DELETE_FOOD =
            "DROP TABLE IF EXISTS " + FoodDBEntry.F_TABLE_NAME;

    // カテゴリテーブルDROP文
    private static final String SQL_DELETE_CATEGORY =
            "DROP TABLE IF EXISTS " + CategoryDBEntry.C_TABLE_NAME;

    /**
     * コンストラクタ
     * @param context　コンテキスト
     */
    DataOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * 無かったら生成されるメソッド
     * @param db　データベース
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される
        db.execSQL(SQL_CREATE_FOOD);
        db.execSQL(SQL_CREATE_CATEGORY);

        db.execSQL("insert into " + CategoryDBEntry.C_TABLE_NAME + " (" + CategoryDBEntry.C_COLUMN_NAME1 + ")" + " values ('その他');");
        db.execSQL("insert into " + CategoryDBEntry.C_TABLE_NAME + " (" + CategoryDBEntry.C_COLUMN_NAME1 + ")" + " values ('肉類');");
        db.execSQL("insert into " + CategoryDBEntry.C_TABLE_NAME + " (" + CategoryDBEntry.C_COLUMN_NAME1 + ")" + " values ('野菜');");
        db.execSQL("insert into " + CategoryDBEntry.C_TABLE_NAME + " (" + CategoryDBEntry.C_COLUMN_NAME1 + ")" + " values ('魚類');");

        Log.d("debug", "onCreate(SQLiteDatabase db)");

    }

    /**
     * アップグレードメソッド
     * @param db データベース
     * @param oldVersion 古いバージョン
     * @param newVersion 新しいバージョン
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートの判別
        db.execSQL(SQL_DELETE_FOOD);
        db.execSQL(SQL_DELETE_CATEGORY);
        onCreate(db);
    }

    /**
     * ダウングレードメソッド
     * @param db データベース
     * @param oldVersion 古いバージョン
     * @param newVersion 新しいバージョン
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * データを全て取得するメソッド
     * @param tablename テーブル名
     * @return cursor
     */
    public List<String> getAllLabels(String tablename){
        List<String> data = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + tablename;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                data.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return data;
    }

}
