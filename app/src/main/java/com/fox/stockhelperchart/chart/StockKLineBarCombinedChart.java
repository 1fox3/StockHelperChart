package com.fox.stockhelperchart.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

public class StockKLineBarCombinedChart extends CombinedChart {
    public StockKLineBarCombinedChart(Context context) {
        super(context);
        initSelf();
    }

    public StockKLineBarCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf();
    }

    public StockKLineBarCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSelf();
    }

    /**
     * 初始化
     */
    protected void initSelf() {
        initChart();
        initXAxis();
        initLeftYAxis();
        initRightYAxis();
    }

    /**
     * 初始化图表
     */
    private void initChart() {
        //画外框线
        setDrawBorders(true);
        //不显示线图描述文案
        Description description = new Description();
        description.setEnabled(false);
        setDescription(description);
        //不显示数据集合名称
        getLegend().setEnabled(false);
    }

    /**
     * 初始化X轴
     */
    private void initXAxis() {
        XAxis xAxis = getXAxis();
        //设置位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //不显示刻度值
//        xAxis.setDrawLabels(false);
    }

    /**
     * 初始化左Y轴
     */
    private void initLeftYAxis() {
        YAxis leftYAxis = getAxisLeft();
        //不显示刻度值
        leftYAxis.setDrawLabels(true);
        //刻度显示再里边
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
    }

    /**
     * 初始化右Y轴
     */
    private void initRightYAxis() {
        YAxis rightYAxis = getAxisRight();
        //不显示刻度值
        rightYAxis.setDrawLabels(false);
    }
}
