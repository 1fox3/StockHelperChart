package com.fox.stockhelperchart.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.StockMultiDayMinuteChart;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 对天分钟线图
 *
 * @author lusongsong
 * @date 2021/2/25 16:41
 */
public class MultiDayMinuteChartActivity extends AppCompatActivity {
    @BindView(R.id.stockMultiDayMinuteChart)
    StockMultiDayMinuteChart stockMultiDayMinuteChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day_kline);
        ButterKnife.bind(MultiDayMinuteChartActivity.this);
        stockMultiDayMinuteChart.initChart();
    }
}
