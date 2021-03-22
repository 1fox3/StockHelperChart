package com.fox.stockhelperchart.activity;

import android.os.Bundle;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.StockMinuteChart;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 分钟粒度K线图界面
 *
 * @author lusongsong
 * @date 2021/2/25 16:40
 */
public class MinuteKLineActivity extends AppCompatActivity {
    @BindView(R.id.stockMinuteChart)
    StockMinuteChart stockMinuteChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minute_kline);
        ButterKnife.bind(MinuteKLineActivity.this);
        stockMinuteChart.initChart();
    }
}
