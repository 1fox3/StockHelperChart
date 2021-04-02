package com.fox.stockhelperchart.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.renderer.xaxis.StockKLineLineXAxisRenderer;
import com.fox.stockhelperchart.renderer.yaxis.StockKLineLineYAxisRenderer;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import static com.fox.stockhelperchart.BaseStockChart.NO_DATA_STR;

public class StockKLineLineCombinedChart extends CombinedChart {
    public StockKLineLineCombinedChart(Context context) {
        super(context);
        initSelf();
    }

    public StockKLineLineCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf();
    }

    public StockKLineLineCombinedChart(Context context, AttributeSet attrs, int defStyle) {
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
        //设置边框颜色
        setBorderColor(getContext().getColor(R.color.chartBorder));
        //设置无数据时的显示文案
        setNoDataText(NO_DATA_STR);
        //不显示线图描述文案
        Description description = new Description();
        description.setEnabled(false);
        setDescription(description);
        //不显示数据集合名称
        getLegend().setEnabled(false);
        //设置X轴渲染器
        setXAxisRenderer(
                new StockKLineLineXAxisRenderer(
                        getViewPortHandler(),
                        getXAxis(),
                        getTransformer(YAxis.AxisDependency.LEFT)
                )
        );
        //设置左Y轴渲染器
        setRendererLeftYAxis(
                new StockKLineLineYAxisRenderer(
                        getViewPortHandler(),
                        getAxisLeft(),
                        getTransformer(YAxis.AxisDependency.LEFT)
                )
        );
    }

    /**
     * 初始化X轴
     */
    private void initXAxis() {
        XAxis xAxis = getXAxis();
        //X轴显示在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //X轴不显示坐标
        xAxis.setDrawLabels(false);
        //X轴显示网格线
        xAxis.setDrawGridLines(true);
        //网格虚线
        xAxis.enableGridDashedLine(4, 3, 0);
    }

    /**
     * 初始化左Y轴
     */
    private void initLeftYAxis() {
        YAxis leftYAxis = getAxisLeft();
        //左Y轴显示在图标内部
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //Y轴显示网格线
        leftYAxis.setDrawGridLines(true);
        //网格虚线
        leftYAxis.enableGridDashedLine(4, 3, 0);
    }

    /**
     * 初始化右Y轴
     */
    private void initRightYAxis() {
        YAxis rightYAxis = getAxisRight();
        //右Y轴显示在图标内部
        rightYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //Y轴不显示网格线
        rightYAxis.setDrawGridLines(false);
        //不显示刻度
        rightYAxis.setDrawLabels(false);
    }
}
