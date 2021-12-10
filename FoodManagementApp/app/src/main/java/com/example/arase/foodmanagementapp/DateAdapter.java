package com.example.arase.foodmanagementapp;

import android.support.v7.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日付を扱うクラス
 */
public final class DateAdapter extends AppCompatActivity {

    /**
     * int型の日付をString型に変換するメソッド
     * @param value 日付(String:文字列)
     * @return 日付(int:2バイトの符号付整数)
     */
    public static String strconvert(int value){
        String str = String.valueOf(value);
        String today;
        if(value == 0){
            today = "####/##/##";
        }else{
            int year = Integer.parseInt(str.substring(0,4));
            int month = Integer.parseInt(str.substring(4,6));
            int date = Integer.parseInt(str.substring(6));
            today = year + "/" + month + "/" +date;
        }
        return today;
    }

    /**
     * 2つの日付の差を求めるメソッド
     * @param strDate1    日付(String:文字列)
     * @param strDate2    日付(String:文字列)
     * @return    2つの日付の差(int:2バイトの符号付整数)
     * @throws ParseException 日付フォーマットが不正な場合
     */
    public static int differenceDays(String strDate1,String strDate2)
            throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date1 = simpleDateFormat.parse(strDate1);
        Date date2 = simpleDateFormat.parse(strDate2);
        return differenceDays(date1,date2);
    }

    /*
        java.util.Date 型の日付 date1 – date2 が何日かを返します。
         1.最初に2つの日付を long 値に変換
            ※この long 値は 1970 年 1 月 1 日 00:00:00 GMT からの経過ミリ秒数となります。
         2.次にその差を求めます。
         3.上記の計算で出た数量を 1 日の時間で割ることで日付の差を求めることができます。
         ※1 日 ( 24 時間) は、86,400,000 ミリ秒です。
     */
    /**
     * 2つの日付の差を求めるメソッド
     * @param date1    日付 java.util.Date
     * @param date2    日付 java.util.Date
     * @return    2つの日付の差(int:2バイトの符号付整数)
     */
    public static int differenceDays(Date date1, Date date2) {
        long datetime1 = date1.getTime();
        long datetime2 = date2.getTime();
        long one_date_time = 1000 * 60 * 60 * 24;
        long diffDays = (datetime1 - datetime2) / one_date_time;
        return (int)diffDays;
    }

    /**
     * 現在日付を取得するメソッド
     */
    public static String getToday(){
        TimeZone timeZone = TimeZone.getDefault();
        String date;
        // デフォルトのCalendarオブジェクト
        Calendar cal = Calendar.getInstance(timeZone);
        date = cal.get(Calendar.YEAR) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE);
        return date;
    }
}
