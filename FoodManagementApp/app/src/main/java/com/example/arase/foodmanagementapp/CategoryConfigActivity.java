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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arase.foodmanagementapp.DBContract.CategoryDBEntry;
import com.example.arase.foodmanagementapp.DBContract.FoodDBEntry;
import com.example.arase.foodmanagementapp.ListAdapter.CListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * カテゴリ設定クラス
 */
public class CategoryConfigActivity extends AppCompatActivity {

    //DataOpenHelperのインスタンス
    private DataOpenHelper DBhelper;

    //EditTextのインスタンス
    private EditText editText;

    //TextViewのインスタンス
    TextView textView;

    //idを格納するArraylist
    private ArrayList<Integer> idlist;

    //その他のidを指定するメソッド
    private int othersid;

    //現在表示しているページ数を格納する変数
    private int page=1;

    //現在表示できる最大ページ数を格納する変数
    private int maxpage;

    //１ページ単位でlistに表示する行数を格納する変数
    private int limit=8;


    /**
     * 無かったら生成されるクラス
     * @param savedInstanceState 初回の起動か、再起動なのかを捕捉する
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_config);

        editText = findViewById(R.id.categoryedit);
        textView = findViewById(R.id.pagedisplay);
        maxpageupdate();


        //戻るボタン
        Button button = findViewById(R.id.StartButton);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this,StartActivity.class));
        });

        //追加ボタン
        button = findViewById(R.id.additionButton);
        button.setOnClickListener((View v) ->{
            insert();
            listshow(limit, page-1);
            maxpageupdate();
            editText.setText(null);
        });

        //削除ボタン
        button = findViewById(R.id.DeleteButton);
        button.setOnClickListener((View v) ->{
            ListView list = findViewById(R.id.categorylist);
            new AlertDialog.Builder(CategoryConfigActivity.this)
                .setTitle("注意")
                .setMessage("削除しても宜しいですか?")
                .setPositiveButton("はい", (dialog, which) -> {
                    int count = list.getCount();
                    int down = 0;
                    othersid = Integer.valueOf(idlist.get(0));
                    CheckBox checkBox;
                    for (int i=0; i < count; ++i) {
                        checkBox = list.getChildAt(i).findViewById(R.id.craw2);
                        if (checkBox.isChecked()) {
                            if(i==0){
                                checkBox.setChecked(false);
                                Toast.makeText(this, "その他は消すことができません", Toast.LENGTH_SHORT).show();
                            }else{
                                delete(Integer.valueOf(idlist.get(i - down)));
                                idlist.remove(i - down);
                                ++down;
                            }
                        }
                    }

                    listshow(limit,page-1);
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


        if(DBhelper == null){
            // データベースヘルパーを準備
            DBhelper = new DataOpenHelper(getApplicationContext());
        }
        listshow(limit,page-1);
        maxpageupdate();

    }

    /**
     * Databaseのデータ追加メソッド
     */
    private void insert(){
        String category = editText.getText().toString();
        //重複したかどうかを格納する変数
        boolean overlapp = false;

        try (SQLiteDatabase db = DBhelper.getReadableDatabase()) {
            String cols = " SELECT * FROM " + CategoryDBEntry.C_TABLE_NAME;
            // データベースを検索
            @SuppressLint("Recycle")
            Cursor cursor = db.rawQuery(cols, null);
            if (cursor.moveToFirst()) {
                do {
                    String string = cursor.getString(1);
                    if (category.equals(string)){
                        overlapp = true;
                        break;
                    }else{
                        overlapp = false;
                    }
                } while (cursor.moveToNext());
            }


        }
        if(overlapp) {
            Toast.makeText(this, "重複しています", Toast.LENGTH_SHORT).show();
        } else {
            if (category.isEmpty()){
                Toast.makeText(this, "カテゴリを入力してください", Toast.LENGTH_SHORT).show();
            } else{
                // 書き込みモードでデータベースをオープン
                try (SQLiteDatabase db = DBhelper.getWritableDatabase()) {

                    // 入力されたタイトルとコンテンツをContentValuesに設定
                    // ContentValuesは、項目名と値をセットで保存できるオブジェクト
                    ContentValues cv = new ContentValues();
                    cv.put(CategoryDBEntry.C_COLUMN_NAME1, category);

                    db.insert(CategoryDBEntry.C_TABLE_NAME, null, cv);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        listshow(limit,page-1);
    }

    /**
     * Databaseのデータ削除メソッド
     * @param cid カテゴリテーブルのid
     */
    private void delete(int cid){
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()){
            ContentValues cv = new ContentValues();
            cv.put(FoodDBEntry.F_CATEGORY_ID, othersid);
            db.delete(CategoryDBEntry.C_TABLE_NAME, CategoryDBEntry._ID +"="+ cid, null);
            db.update(FoodDBEntry.F_TABLE_NAME, cv, FoodDBEntry.F_CATEGORY_ID +"="+ cid, null);
        }
    }

    /**
     * listの描画メソッド
     * @param limit 描画行数
     * @param cnt　現在ページ数
     */
    private void listshow(int limit, int cnt){
        // データベースを検索する項目を定義
        String cols = " SELECT * FROM " + CategoryDBEntry.C_TABLE_NAME + " LIMIT "+ limit + " OFFSET " + 8*cnt;

        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = DBhelper.getReadableDatabase()){

            // データベースを検索
            Cursor cursor = db.rawQuery(cols,null);

            // 検索結果から取得する項目を定義
            String[] from = {CategoryDBEntry.C_COLUMN_NAME1};

            // データを設定するレイアウトのフィールドを定義
            int[] to = {R.id.craw1};

            // ListViewの1行分のレイアウト(categoorylist_row.xml)と検索結果を関連付け
            CListAdapter cladapter = new CListAdapter(
                    this, R.layout.categotylist_row, cursor, from, to,0);

            // activity_main.xmlに定義したListViewオブジェクトを取得
            ListView list = findViewById(R.id.categorylist);

            // ListViewにアダプターを設定
            list.setAdapter(cladapter);

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
     * 最大ページ数更新メソッド
     */
    @SuppressLint("SetTextI18n")
    private void maxpageupdate(){
        // database handler
        DataOpenHelper db = new DataOpenHelper(getApplicationContext());

        // Spinner Drop down elements
        List<String> displaylist = db.getAllLabels(CategoryDBEntry.C_TABLE_NAME);
        maxpage = displaylist.size()/limit+1;
        if(displaylist.size()%limit ==0) --maxpage;
        textView.setText(page+"/"+maxpage);
    }

}