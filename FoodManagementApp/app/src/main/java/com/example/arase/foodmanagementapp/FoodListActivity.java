package com.example.arase.foodmanagementapp;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arase.foodmanagementapp.DBContract.CategoryDBEntry;
import com.example.arase.foodmanagementapp.DBContract.FoodDBEntry;
import com.example.arase.foodmanagementapp.ListAdapter.FListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 食品一覧表示画面処置クラス
 */
public class FoodListActivity extends AppCompatActivity {

    //DataOpenHelperインスタンス
    private DataOpenHelper DBhelper;

    //idを格納するArrylist
    private ArrayList<Integer> idlist;

    //Textviewのインスタンス
    TextView textView;

    //Databaseのcategoryidを格納する変数
    private int cid;

    //Spinnerの選択した選択肢の位置を格納する変数
    private int itemposition;

    //現在表示しているページ数を格納する変数
    private int page =1;

    //現在表示できる最大ページ数を格納する変数
    private int maxpage=1;

    //１ページ単位でlistに表示する行数を格納する変数
    private int limit=10;

    /**
     * 無かったら生成されるメソッド
     * @param savedInstanceState 初回の起動か、再起動なのかを捕捉する
     */
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        textView = findViewById(R.id.pagedisplay);
        textView.setText(page+"/"+maxpage);

        if(DBhelper == null) DBhelper = new DataOpenHelper(this);
        if(idlist == null) this.idlist = new ArrayList<>();

        //スタート画面に遷移するボタン
        Button button = findViewById(R.id.StartButton);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        });

        //詳細編集ボタン
        button = findViewById(R.id.DataInputButton);
        button.setOnClickListener((View v) -> {

            boolean check = false;
            int fid= 0;
            ListView list = findViewById(R.id.foodlist);
            int count = list.getCount();
            for (int i = 0; i < count; i++) {
                CheckBox checkBox = list.getChildAt(i).findViewById(R.id.fraw5);
                if (checkBox.isChecked()) {
                    fid = Integer.valueOf(idlist.get(i));
                    check = true;
                    break;
                }
            }
            if(!check) {
                Toast.makeText(this, "項目を一つ選択してください", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, DataInputActivity.class));
                DataInputActivity.setActivitynum(DataInputActivity.ACTIVITY_LIST);
                DataInputActivity.setInsertFlg(false);
                try (SQLiteDatabase db = DBhelper.getReadableDatabase()){
                    String cols = " SELECT * FROM " + FoodDBEntry.F_TABLE_NAME + " WHERE " + FoodDBEntry._ID +"="+ fid;

                    @SuppressLint("Recycle")
                    Cursor cursor = db.rawQuery(cols,null);
                    cursor.moveToFirst();
                    DataInputActivity.setEditDetail(cursor.getInt(cursor.getColumnIndex(FoodDBEntry._ID)),
                            cursor.getString(cursor.getColumnIndex(FoodDBEntry.F_COLUMN_NAME1)),
                            cursor.getInt(cursor.getColumnIndex(FoodDBEntry.F_COLUMN_NAME2)),
                            cursor.getInt(cursor.getColumnIndex(FoodDBEntry.F_COLUMN_NAME3)),
                            cursor.getInt(cursor.getColumnIndex(FoodDBEntry.F_COLUMN_NAME4)),
                            cursor.getInt(cursor.getColumnIndex(FoodDBEntry.F_CATEGORY_ID)));
                }
            }

        });


        //表示ボタン
        button = findViewById(R.id.DisplayButton);
        button.setOnClickListener((View v) ->{
            listshow(limit, page-1);
            maxpageupdate();

        });



        //削除ボタン
        button = findViewById(R.id.DeleteButton);
        button.setOnClickListener((View v) ->{
            ListView list = findViewById(R.id.foodlist);
            new AlertDialog.Builder(FoodListActivity.this)
                .setTitle("注意")
                .setMessage("削除しても宜しいですか?")
                .setPositiveButton("はい", (dialog, which) -> {
                    int count = list.getCount();
                    int down=0;
                    CheckBox checkBox;
                    for(int i=0; i < count; ++i){
                        checkBox = list.getChildAt(i).findViewById(R.id.fraw5);
                        if (checkBox.isChecked()) {
                            delete(Integer.valueOf(idlist.get(i - down)));
                            idlist.remove(i - down);
                            ++down;
                        }
                    }
                    listshow(limit, page-1);
                    maxpageupdate();
                })
                .setNegativeButton("キャンセル", null)
                .show();
        });

        //前のページに移動するボタン
        ImageButton imageButton = findViewById(R.id.returnbutton);
        imageButton.setOnClickListener((View v) ->{
            if(page > 1) --page;
            listshow(limit, page-1);
            maxpageupdate();
        });

        //次のページに移動するボタン
        imageButton = findViewById(R.id.nextbutton);
        imageButton.setOnClickListener((View v) ->{
            maxpageupdate();
            if(page < maxpage) ++page;
            listshow(limit, page-1);
            textView.setText(page+"/"+maxpage);
        });

        //Spinner設定
        Spinner Cspinner = findViewById(R.id.CategorySpinner);
        loadSpinnerData(CategoryDBEntry.C_TABLE_NAME,Cspinner);

        // リスナーを登録
        Cspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * アイテムが選択された時
             * @param parent 描画しているオブジェクト
             * @param view　描画している内容
             * @param position　選んだ位置
             * @param id 選んだ場所のid
             */
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                itemposition = spinner.getSelectedItemPosition();
                if(itemposition == spinner.getCount()-1){
                    cid = 0;
                }else{
                    try (SQLiteDatabase db = DBhelper.getReadableDatabase()) {
                        // データベースから取得する項目を設定
                        String[] cols = {CategoryDBEntry._ID};
                        // データを取得するSQLを実行
                        // 取得したデータがCursorオブジェクトに格納される
                        @SuppressLint("Recycle")
                        Cursor cursor = db.query(CategoryDBEntry.C_TABLE_NAME, cols, null,
                                null, null, null, null, null);
                        cursor.moveToPosition(itemposition);
                        cid = Integer.valueOf(cursor.getInt(0));
                    }
                }

            }

            /**
             * アイテムが選択されなかった時
             * @param parent 描画しているオブジェクト
             */
            public void onNothingSelected(AdapterView<?> parent) {
                Spinner spinner = findViewById(R.id.CategorySpinner);
                spinner.setSelection(spinner.getCount());
                cid = 0;
            }
        });

    }

    /**
     * listの描画メソッド
     * @param limit 描画行数
     * @param cnt　現在ページ数
     */
    private void listshow(int limit, int cnt){

        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()){

            String selectcols;
            if(cid == 0){
                selectcols = "SELECT * FROM " + FoodDBEntry.F_TABLE_NAME +" ORDER BY "+FoodDBEntry.F_COLUMN_NAME4 +
                        " ASC LIMIT "+ limit +" OFFSET "+limit*cnt;
            }else{
                selectcols = "SELECT * FROM "+ FoodDBEntry.F_TABLE_NAME +" WHERE " + FoodDBEntry.F_CATEGORY_ID + " = " + cid +
                        " ORDER BY " + FoodDBEntry.F_COLUMN_NAME4 +" ASC LIMIT " + limit +" OFFSET "+limit*cnt;
            }

            Cursor cursor = db.rawQuery(selectcols, null);

            // 検索結果から取得する項目を定義
            String[] from = {FoodDBEntry.F_COLUMN_NAME1,FoodDBEntry.F_COLUMN_NAME2,FoodDBEntry.F_COLUMN_NAME3,FoodDBEntry.F_COLUMN_NAME5};

            // データを設定するレイアウトのフィールドを定義
            int[] to = {R.id.fraw1,R.id.fraw2,R.id.fraw3,R.id.fraw4};

            // ListViewの1行分のレイアウト(row_main.xml)と検索結果を関連付け
            FListAdapter fladapter = new FListAdapter(
                    this, R.layout.foodlist_row, cursor, from, to, 0);


            // activity_main.xmlに定義したListViewオブジェクトを取得
            ListView list = findViewById(R.id.foodlist);

            // ListViewにアダプターを設定
            list.setAdapter(fladapter);

            //idlistを初期化してidを保存
            idlist = null;
            this.idlist = new ArrayList<>();

            if(cursor.moveToFirst()) {
                do {
                    idlist.add(Integer.valueOf(cursor.getInt(0)));
                } while (cursor.moveToNext());
            }
        }


    }

    /**
     * Spinner設定メソッド
     * @param tablename　Databaseのテーブル名
     * @param spinner　Spinnerのインスタンス
     */
    public void loadSpinnerData(String tablename, Spinner spinner) {
        // database handler
        DataOpenHelper db = new DataOpenHelper(getApplicationContext());

        // Spinner Drop down elements
        List<String> lables = db.getAllLabels(tablename);
        lables.add("全て");

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
     * Databaseのデータ削除メソッド
     * @param fid 食品テーブルのid
     */
    private void delete(int fid){
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()){
            db.delete(FoodDBEntry.F_TABLE_NAME, FoodDBEntry._ID +"="+ fid, null);
        }
    }

    /**
     * 最大ページ数更新メソッド
     */
    private void maxpageupdate(){
        // Databaseを読み込み
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()) {

            String selectcols;
            if (cid == 0) {
                selectcols = "SELECT * FROM " + FoodDBEntry.F_TABLE_NAME + " ORDER BY " + FoodDBEntry.F_COLUMN_NAME4 +
                        " ASC";
            } else {
                selectcols = "SELECT * FROM " + FoodDBEntry.F_TABLE_NAME + " WHERE " + FoodDBEntry.F_CATEGORY_ID + " = " + cid +
                        " ORDER BY " + FoodDBEntry.F_COLUMN_NAME4 + " ASC";
            }

            Cursor cursor = db.rawQuery(selectcols, null);
            maxpage = cursor.getCount() / limit +1;
            if(cursor.getCount()%limit ==0) --maxpage;
            textView.setText(page+"/"+maxpage);
        }
    }

}