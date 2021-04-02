package com.fox.stockhelperchart.activity;

import android.os.Bundle;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.StockKLineChart;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lusongsong
 * @date 2021/2/25 16:41
 */
public class KLineActivity extends AppCompatActivity {
    @BindView(R.id.stockKLineChart)
    StockKLineChart stockKLineChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        ButterKnife.bind(KLineActivity.this);
        stockKLineChart.initChart();
    }
}
