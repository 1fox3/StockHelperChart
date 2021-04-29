package com.fox.stockhelperchart.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.formatter.StockPercentFormatter;
import com.fox.stockhelperchart.formatter.StockPriceFormatter;
import com.fox.stockhelperchart.formatter.StockXAxisFormatter;
import com.fox.stockhelperchart.markerview.StockMarkerView;
import com.fox.stockhelperchart.renderer.chart.StockMultiDayMinuteLineChartRenderer;
import com.fox.stockhelperchart.renderer.xaxis.StockMultiDayMinuteLineXAxisRenderer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.fox.stockhelperchart.BaseStockChart.NO_DATA_STR;
import static com.fox.stockhelperchart.BaseStockChart.X_NODE_COUNT;
import static com.fox.stockhelperchart.StockMultiDayMinuteChart.DAY_NUM;

/**
 * @author lusongsong
 * @date 2021/3/26 15:11
 */
public class StockMultiDayMinuteLineChart extends StockMinuteLineChart {
    public StockMultiDayMinuteLineChart(Context context) {
        super(context);
        initSelf();
    }

    public StockMultiDayMinuteLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf();
    }

    public StockMultiDayMinuteLineChart(Context context, AttributeSet attrs, int defStyle) {
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
        //设置提示
        StockMarkerView stockMarkerView =
                new StockMarkerView(getContext(), R.layout.markerview_str);
        stockMarkerView.setChartView(this);
        setMarker(stockMarkerView);
        //设置划线渲染器，并指定需要断开的点
        StockMultiDayMinuteLineChartRenderer stockMultiDayMinuteLineChartRenderer =
                new StockMultiDayMinuteLineChartRenderer(this, mAnimator, mViewPortHandler);
        List<Integer> breakPosList = new ArrayList<>(DAY_NUM);
        for (int i = 0; i < DAY_NUM - 1; i++) {
            breakPosList.add(X_NODE_COUNT * (i + 1) + 1);
        }
        stockMultiDayMinuteLineChartRenderer.setBreakPos(breakPosList);
        setRenderer(stockMultiDayMinuteLineChartRenderer);
    }

    /**
     * 初始化X轴
     */
    private void initXAxis() {
        XAxis xAxis = getXAxis();
        //X轴显示在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //X轴不显示坐标
        xAxis.setDrawLabels(true);
        //X轴显示网格线
        xAxis.setDrawGridLines(true);
        //设置X轴渲染器
        StockMultiDayMinuteLineXAxisRenderer stockMultiDayMinuteLineXAxisRenderer =
                new StockMultiDayMinuteLineXAxisRenderer(
                        getViewPortHandler(), xAxis, getTransformer(YAxis.AxisDependency.LEFT)
                );
        int[] gradLinePos = new int[DAY_NUM + 1];
        int[] labelPos = new int[DAY_NUM];
        TreeMap<Integer, String> labelMap = new TreeMap<>();
        for (int i = 0; i <= DAY_NUM; i++) {
            gradLinePos[i] = X_NODE_COUNT * i;
        }
        for (int i = 0; i < DAY_NUM; i++) {
            labelMap.put(X_NODE_COUNT / 2 + X_NODE_COUNT * i, "21/3/1");
            labelPos[i] = X_NODE_COUNT / 2 + X_NODE_COUNT * i;
        }
        stockMultiDayMinuteLineXAxisRenderer.setGradLinePos(gradLinePos);
        stockMultiDayMinuteLineXAxisRenderer.setLabelPos(labelPos);
        setXAxisRenderer(stockMultiDayMinuteLineXAxisRenderer);
        //设置X轴Label格式器
        StockXAxisFormatter stockXAxisFormatter = new StockXAxisFormatter();
        stockXAxisFormatter.setLabels(labelMap);
        xAxis.setValueFormatter(stockXAxisFormatter);
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
        //设置右Y轴数值格式器
        leftYAxis.setValueFormatter(
                new StockPriceFormatter()
                        .setNumberFormatter(false)
                        .initFormatter()
        );
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
        //设置右Y轴数值格式器
        rightYAxis.setValueFormatter(
                new StockPercentFormatter()
                        .setNumberFormatter(false)
                        .initFormatter()
        );
    }
}
