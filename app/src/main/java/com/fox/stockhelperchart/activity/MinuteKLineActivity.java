package com.fox.stockhelperchart.activity;

import android.os.Bundle;

import com.fox.stockhelperchart.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

/**
 * @author lusongsong
 * @date 2021/2/25 16:40
 */
public class MinuteKLineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minute_kline);
        ButterKnife.bind(MinuteKLineActivity.this);
    }
}
