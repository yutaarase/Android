package com.example.arase.foodmanagementapp;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arase.foodmanagementapp.DBContract.CategoryDBEntry;
import com.example.arase.foodmanagementapp.DBContract.FoodDBEntry;

import java.util.List;

/**
 * 食品詳細入力画面処理クラス
 */
public class DataInputActivity extends AppCompatActivity {

    //Insertをするかどうか判別する変数
    private static boolean InsertFlg;

    //Activityを判別する変数
    private static int Activitynum;

    //各Activityの判別値を格納する変数
    public static final int ACTIVITY_START=0;
    public static final int ACTIVITY_DATE=1;
    public static final int ACTIVITY_LIST=2;

    //DataOpenHelperインスタンス
    private DataOpenHelper DBhelper;

     //各EditTextインスタンス
    private EditText FEText;
    private EditText LEText;

    //Spinnerの選択した選択肢の位置を格納する変数
    private int itemposition;

    //categoryspinnerの初期値用変数
    private int cpsition;

    //各NumberPickerのインスタンス
    //G:Gram Q:Quantity N:Number P:Picker O:One P:Place T:Ten H:Hundred
    NumberPicker GNPOP, GNPTP, GNPHP;
    NumberPicker QNPOP, QNPTP, QNPHP;

    //各DB格納Data受け渡し用変数
    private static int foodid;
    private static int categoryid;
    private static int gramvalue;
    private static int quantityvalue;
    private static int foodlimit;
    private static String foodname;


    /**
     * 無かったら生成されるメソッド
     * @param savedInstanceState　初回の起動か、再起動なのかを捕捉する
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //superクラスの継承
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);

        //Gram入力用ドラムロール設定
        SetValue(GNPOP = findViewById(R.id.GPOnePlace));
        SetValue(GNPTP = findViewById(R.id.GPTensPlace));
        SetValue(GNPHP = findViewById(R.id.GPHundredsPlace));

        SetValue(QNPOP = findViewById(R.id.QPOnePlace));
        SetValue(QNPTP = findViewById(R.id.QPTensPlace));
        SetValue(QNPHP = findViewById(R.id.QPHundredsPlace));

        //各id指定
        FEText = findViewById(R.id.FoodeditText);
        LEText = findViewById(R.id.LimiteditText);

        //DataOprnHelperのインスタンス生成
        if(DBhelper == null) DBhelper = new DataOpenHelper(this);

        //activityの違いによって初期化
        if(Activitynum == ACTIVITY_START) {
            //各初期化
            FEText.setText(null, TextView.BufferType.NORMAL);
            LEText.setText(null, TextView.BufferType.NORMAL);
            foodid = 0;
            foodname = null;
            gramvalue = 0;
            quantityvalue = 0;
            foodlimit = 0;
            categoryid = 0;
            cpsition = 0;
        }else {
            FEText.setText(foodname, TextView.BufferType.NORMAL);
            LEText.setText(String.valueOf(foodlimit), TextView.BufferType.NORMAL);
            GNPHP.setValue(gramvalue / 100);
            GNPTP.setValue((gramvalue / 10) % 10);
            GNPOP.setValue(gramvalue % 10);
            QNPHP.setValue(quantityvalue / 100);
            QNPTP.setValue((quantityvalue / 10) % 10);
            QNPOP.setValue(quantityvalue % 10);

            //Databateを読む
            try(SQLiteDatabase db = DBhelper.getReadableDatabase()){
                String cols = "Select * From "+CategoryDBEntry.C_TABLE_NAME;
                @SuppressLint("Recycle")
                Cursor cursor = db.rawQuery(cols,null);
                if(cursor.moveToFirst()){
                    do {
                        if(cursor.getInt(0)==categoryid){
                            cpsition = Integer.valueOf(cursor.getPosition());
                        }
                    } while (cursor.moveToNext());
                }
            }
        }

        //確定button
        Button button = findViewById(R.id.StartButton);
        button.setOnClickListener((View v) -> {
            if(LEText.getText().toString().isEmpty() == true || Integer.valueOf(LEText.getText().toString()) == 0){
                LEText.setText("0");
                new AlertDialog.Builder(DataInputActivity.this)
                        .setTitle("注意")
                        .setMessage("消費期限が未入力ですが大丈ですか？")
                        .setPositiveButton("はい", (dialog, which) -> {
                            trans();
                        })
                        .setNegativeButton("キャンセル", null)
                        .show();
            }else{
                trans();
            }

        });

        //カレンダーボタン
        button = findViewById(R.id.DateSelectButton);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this, DateSelectActivity.class));
            DateSelectActivity.setdate(foodlimit);
            foodname = FEText.getText().toString();
            gramvalue = getSumValue(GNPHP, GNPTP, GNPOP);
            quantityvalue = getSumValue(QNPHP, QNPTP, QNPOP);

            // Databaseを読み込む
            try (SQLiteDatabase db = DBhelper.getReadableDatabase()) {
                // データベースから取得する項目を設定
                String[] cols = {CategoryDBEntry._ID};
                // データを取得するSQLを実行
                // 取得したデータがCursorオブジェクトに格納される
                @SuppressLint("Recycle")
                Cursor cursor = db.query(CategoryDBEntry.C_TABLE_NAME, cols, null,
                        null, null, null, null, null);
                cursor.moveToPosition(itemposition);
                categoryid = cursor.getInt(0);

            }

            finish();
        });

        //削除ボタン
        button = findViewById(R.id.DeleteButton);
        button.setOnClickListener((View v) -> {
            new AlertDialog.Builder(DataInputActivity.this)
                .setTitle("注意")
                .setMessage("削除しても宜しいですか?")
                .setPositiveButton("はい", (dialog, which) -> {
                    if(InsertFlg){
                        startActivity(new Intent(this, StartActivity.class));
                    }else {
                        startActivity(new Intent(this, FoodListActivity.class));
                        delete(foodid);
                    }
                    finish();
                })
                .setNegativeButton("キャンセル", null)
                .show();
        });

        //Spinner設定
        Spinner Cspinner = findViewById(R.id.CategorySpinner);
        loadSpinnerData(CategoryDBEntry.C_TABLE_NAME,Cspinner);
        Cspinner.setSelection(cpsition);

        // リスナーを登録
        Cspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                itemposition = spinner.getSelectedItemPosition();

            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                itemposition = 0;
            }
        });

    }


    /**
     * 画面遷移確認用値取得メソッド
     * @param num 確認用値
     */
    public static void setActivitynum(int num) {
        Activitynum = num;
    }

    /**
     * 食品テーブルデータ更新メソッド
     */
    private void update() {
        // 入力欄に入力されたデータを取得
        String foodname = FEText.getText().toString();
        int gram = getSumValue(GNPHP, GNPTP, GNPOP);
        int quantity = getSumValue(QNPHP, QNPTP, QNPOP);
        int limit = Integer.parseInt(LEText.getText().toString());
        int cid;
        String dlimit = DateAdapter.strconvert(limit);
        String fcols = FoodDBEntry._ID + "=" + foodid;

        // Databaseを読み込む
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()) {
            // データベースから取得する項目を設定
            String[] cols = {CategoryDBEntry._ID};
            // データを取得するSQLを実行
            // 取得したデータがCursorオブジェクトに格納される
            @SuppressLint("Recycle")
            Cursor cursor = db.query(CategoryDBEntry.C_TABLE_NAME, cols, null,
                    null, null, null, null, null);
            cursor.moveToPosition(itemposition);
            cid = cursor.getInt(0);

        }

        // Databaseに書き込む
        try (SQLiteDatabase db = DBhelper.getWritableDatabase()) {

            // 入力されたタイトルとコンテンツをContentValuesに設定
            // ContentValuesは、項目名と値をセットで保存できるオブジェクト
            ContentValues cv = new ContentValues();
            cv.put(FoodDBEntry.F_CATEGORY_ID, cid);
            cv.put(FoodDBEntry.F_COLUMN_NAME1, foodname);
            cv.put(FoodDBEntry.F_COLUMN_NAME2, gram);
            cv.put(FoodDBEntry.F_COLUMN_NAME3, quantity);
            cv.put(FoodDBEntry.F_COLUMN_NAME4, limit);
            cv.put(FoodDBEntry.F_COLUMN_NAME5, dlimit);

            db.update(FoodDBEntry.F_TABLE_NAME, cv, fcols, null);
        }
    }

    /**
     * 食品テーブルデータ追加処理メソッド
     */
    private void insert() {
        // 入力欄に入力されたタイトルとコンテンツを取得
        String foodname = FEText.getText().toString();
        int gram = getSumValue(GNPHP, GNPTP, GNPOP);
        int quantity = getSumValue(QNPHP, QNPTP, QNPOP);
        int limit = Integer.valueOf(LEText.getText().toString());
        int cid;
        String dlimit = DateAdapter.strconvert(limit);

        // Databaseを読み込む
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()) {
            // データベースから取得する項目を設定
            String[] cols = {CategoryDBEntry._ID};
            // データを取得するSQLを実行
            // 取得したデータがCursorオブジェクトに格納される
            @SuppressLint("Recycle")
            Cursor cursor = db.query(CategoryDBEntry.C_TABLE_NAME, cols, null,
                    null, null, null, null, null);
            cursor.moveToPosition(itemposition);
            cid = cursor.getInt(0);

        }

        // Databaseに書き込む
        try (SQLiteDatabase db = DBhelper.getWritableDatabase()) {

            // 入力されたタイトルとコンテンツをContentValuesに設定
            // ContentValuesは、項目名と値をセットで保存できるオブジェクト
            ContentValues cv = new ContentValues();
            cv.put(FoodDBEntry.F_CATEGORY_ID, cid);
            cv.put(FoodDBEntry.F_COLUMN_NAME1, foodname);
            cv.put(FoodDBEntry.F_COLUMN_NAME2, gram);
            cv.put(FoodDBEntry.F_COLUMN_NAME3, quantity);
            cv.put(FoodDBEntry.F_COLUMN_NAME4, limit);
            cv.put(FoodDBEntry.F_COLUMN_NAME5, dlimit);

            db.insert(FoodDBEntry.F_TABLE_NAME, null, cv);

        }

    }


    /**
     * レコード削除処理メソッド
     * @param fid 食品id
     */
    private void delete(int fid){
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()){
            db.delete(FoodDBEntry.F_TABLE_NAME, FoodDBEntry._ID +"="+ fid, null);
        }
    }

    /**
     * spinnerのデータを設定するメソッド
     * @param tablename　Databaseのテーブル名
     * @param spinner　Spinnerのインスタンス
     */
    private void loadSpinnerData (String tablename, Spinner spinner){
        // database handler
        DataOpenHelper db = new DataOpenHelper(getApplicationContext());

        // Spinner Drop down elements
        List<String> lables = db.getAllLabels(tablename);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    /**
     * NumberPickerの最低値、最大値を入力するメソッド
     * @param np NumberPickerのインスタンス
     */
    private void SetValue (NumberPicker np){
            np.setMinValue(0);
            np.setMaxValue(9);

    }

    /**
     * NumberPickerの値を取得するメソッド
     * @param np NumberPickerのインスタンス
     * @param place 桁数
     * @return 値
     */
    public static int getValue (NumberPicker np, int place){
        return Integer.valueOf(np.getValue()) * place;
    }

    /**
     * NumberPickerの合計値を取得するメソッド
     * @param nph NumberPikerのインスタンス
     * @param npt NumberPikerのインスタンス
     * @param npo NumberPikerのインスタンス
     * @return 合計値
     */
    public int getSumValue (NumberPicker nph, NumberPicker npt, NumberPicker npo){
        return getValue(nph, 100) + getValue(npt, 10) + getValue(npo, 1);
    }

    /**
     * 入力情報の初期値を設定するメソッド
     * @param id 食品テーブルid
     * @param fname 食品名
     * @param gram グラム数
     * @param quantity 数量
     * @param limit 消費期限
     * @param cid カテゴリテーブルid
     */
    public static void setEditDetail ( int id, String fname,int gram, int quantity, int limit, int cid){
        foodid = id;
        foodname = fname;
        gramvalue = gram;
        quantityvalue = quantity;
        foodlimit = limit;
        categoryid = cid;
    }

    /**
     * foodlimitの値格納メソッド
     * @param limit 消費期限
     */
    public static void setlimit(int limit){
        foodlimit = limit;
    }

    /**
     * InsertFlgの値格納メソッド
     * @param flg　true or false
     */
    public static void setInsertFlg(boolean flg){
        InsertFlg = flg;
    }


    /**
     * 変更するかどうを問うメソッド
     */
    private void trans(){
        if(InsertFlg){
            if(FEText.getText().toString().isEmpty()){
                Toast.makeText(this, "名前を入力してください", Toast.LENGTH_SHORT).show();
            } else {
                insert();
            }
            startActivity(new Intent(this, StartActivity.class));
        }else {
            if(FEText.getText().toString().isEmpty()){
                Toast.makeText(this, "名前を入力してください", Toast.LENGTH_SHORT).show();
            } else {
                update();
            }
            startActivity(new Intent(this, FoodListActivity.class));
        }
        finish();
    }
}


