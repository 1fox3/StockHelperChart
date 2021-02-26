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
public class MinuteBarChart extends BarChart {
    public MinuteBarChart(Context context) {
        super(context);
    }

    public MinuteBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinuteBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
