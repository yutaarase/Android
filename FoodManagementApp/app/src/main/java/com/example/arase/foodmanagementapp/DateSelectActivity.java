package com.example.arase.foodmanagementapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日付選択画面処理クラス
 */
public class DateSelectActivity extends AppCompatActivity {

    //現在の日付を格納する変数
    private static String today;

    /**
     * 無かったら生成されるメソッド
     * @param savedInstanceState　初回の起動か、再起動なのかを捕捉する
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);

        //カレンダーについて
        CalendarView calendar = findViewById(R.id.Calendar);

        if(today == null);
        else {
            calendar.setDate(convertDateStringToLong(today));
        }


        // 曜日文字のスタイルを変更する
        calendar.setWeekDayTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large);

        /*
         * ・Calendarの曜日定数を使用する。
         * ・Calendar.SUNDAY:日曜日
         * ・Calendar.MONDAY:月曜日
         * ・Calendar.TUESDAY:火曜日
         * ・Calendar.WEDNESDAY:水曜日
         * ・Calendar.THURSDAY:木曜日
         * ・Calendar.FRIDAY:金曜日
         * ・Calendar.SATURDAY:土曜日
         */

        // 週の開始曜日を木曜日に変更する
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        calendar.setOnDateChangeListener((CalendarView calendarView, int year,int month,int date) -> {
            String message = year + "/" +(month+1)+"/"+date;
            DataInputActivity.setlimit(year*10000+(month+1)*100+date);
            DataInputActivity.setActivitynum(DataInputActivity.ACTIVITY_DATE);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, DataInputActivity.class));
            finish();
        });
    }

    /**
     * String型の日付をlong値に変換するメソッド
     * return Date.getTime(); 日付をミリ秒で返す
     * @param value 日付(String:文字列)
     * @return 日付(Long:4バイトの符号付整数)
     */
    private long convertDateStringToLong(String value) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert parse != null;
        return parse.getTime();
    }

    /**
     * 日付を設定するメソッド
     * @param value　日付(int:2バイトの符号付整数)
     */
    public static void setdate(int value){
        if(value == 0)
            today = null;
        else {
            today = DateAdapter.strconvert(value);
        }

    }

}