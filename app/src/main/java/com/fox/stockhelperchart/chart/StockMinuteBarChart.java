package com.fox.stockhelperchart.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

/**
 * 分钟粒度柱状图
 *
 * @author lusongsong
 * @date 2021/2/26 15:09
 */
public class StockMinuteBarChart extends BarChart {
    public StockMinuteBarChart(Context context) {
        super(context);
    }

    public StockMinuteBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StockMinuteBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
