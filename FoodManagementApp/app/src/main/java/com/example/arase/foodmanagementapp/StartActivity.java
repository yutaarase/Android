package com.example.arase.foodmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * スタート画面処理クラス
 */
public class StartActivity extends AppCompatActivity {

    /**
     * 無かったら生成されるメソッド
     * @param savedInstanceState　初回の起動か、再起動なのかを捕捉する
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //食品詳細入力画面遷移ボタン
        Button button = findViewById(R.id.DataInputButton);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this, DataInputActivity.class));
            DataInputActivity.setActivitynum(DataInputActivity.ACTIVITY_START);
            DataInputActivity.setInsertFlg(true);
            finish();
        });

        //食品一覧表示画面遷移ボタン
        button = findViewById(R.id.FoodListButton);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this, FoodListActivity.class));
            finish();
        });

        //カテゴリ設定画面遷移ボタン
        button = findViewById(R.id.CategoryButton);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this, CategoryConfigActivity.class));
            finish();
        });


    }


}