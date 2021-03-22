package com.fox.stockhelperchart.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;

/**
 * 分钟粒度线图
 *
 * @author lusongsong
 * @date 2021/2/25 18:04
 */
public class StockMinuteLineChart extends LineChart {
    public StockMinuteLineChart(Context context) {
        super(context);
    }

    public StockMinuteLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StockMinuteLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
