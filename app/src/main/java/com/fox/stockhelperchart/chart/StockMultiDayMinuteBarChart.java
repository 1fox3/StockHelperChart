package com.fox.stockhelperchart.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

/**
 * @author lusongsong
 * @date 2021/3/26 15:09
 */
public class StockMultiDayMinuteBarChart extends BarChart {
    public StockMultiDayMinuteBarChart(Context context) {
        super(context);
    }

    public StockMultiDayMinuteBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StockMultiDayMinuteBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
