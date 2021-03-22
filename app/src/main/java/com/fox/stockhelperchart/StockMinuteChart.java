package com.fox.stockhelperchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.fox.stockhelperchart.chart.StockMinuteLineChart;
import com.fox.stockhelperchart.formatter.StockPercentFormatter;
import com.fox.stockhelperchart.formatter.StockPriceFormatter;
import com.fox.stockhelperchart.formatter.StockXAxisFormatter;
import com.fox.stockhelperchart.renderer.StockXAxisRenderer;
import com.fox.stockhelperchart.renderer.StockYAxisRenderer;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 股票分钟力度数据
 *
 * @author lusongsong
 * @date 2021/2/26 15:54
 */
public class StockMinuteChart extends BaseStockChart {
    /**
     * X轴数据量
     */
    public static int X_NODE_COUNT = 241;
    /**
     * Y轴数据量
     */
    public static int Y_NODE_COUNT = 130;
    /**
     * Y轴默认显示的刻度数
     */
    public static int Y_LABEL_COUNT = 5;
    /**
     * Y轴左侧最大值
     */
    public static int LEFT_Y_VALUE_MAX = 100;
    /**
     * Y轴左侧最小值
     */
    public static int LEFT_Y_VALUE_MIN = 80;
    /**
     * Y轴右侧最大值
     */
    public static int RIGHT_Y_VALUE_MAX = 10;
    /**
     * Y轴右侧最小值
     */
    public static int RIGHT_Y_VALUE_MIN = -10;

    /**
     * 分钟线图数据
     */
    @BindView(R.id.stockMinuteLineChart)
    StockMinuteLineChart lineChart;
    /**
     * 线图X轴
     */
    XAxis lineX;
    /**
     * 线图左Y轴
     */
    YAxis lineLeftY;
    /**
     * 线图右Y轴
     */
    YAxis lineRightY;

    /**
     * 分钟柱图数据
     */
//    @BindView(R.id.stockMinuteBarChart)
//    StockMinuteBarChart barChart;
    /**
     * 柱图X轴
     */
    XAxis barX;
    /**
     * 柱图左Y轴
     */
    YAxis barLeftY;
    /**
     * 柱图右Y轴
     */
    YAxis barRightY;

    public StockMinuteChart(Context context) {
        super(context);
    }

    public StockMinuteChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StockMinuteChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StockMinuteChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 绑定布局文件
     */
    private void bindLayout() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.stock_chart_minute, this, true
        );
        ButterKnife.bind(this, view);
    }

    /**
     * 初始化图表
     */
    public void initChart() {
        //父类的初始化图表
        super.initChart();
        //绑定视图
        bindLayout();
        //初始化线图
        initLineChart();
        //初始化柱图
//        initBarChart();
    }

    /**
     * 初始化线图
     */
    private void initLineChart() {
        //画外框线
        lineChart.setDrawBorders(true);
        //设置边框颜色
        lineChart.setBorderColor(borderColor);
        //设置无数据时的显示文案
        lineChart.setNoDataText(NO_DATA_STR);
        //不显示线图描述文案
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);
        //不显示数据集合名称
        lineChart.getLegend().setEnabled(false);
        //设置显示数据
        lineChart.setData(getTestLineData());

        //初始化线图X轴
        initLineXAxis();
        //初始化线图左Y轴
        initLineLeftYAxis();
        //初始化线图右Y轴
        initLineRightYAxis();
    }

    /**
     * 初始化线图X轴
     */
    private void initLineXAxis() {
        lineX = lineChart.getXAxis();
        //X轴显示在2边
        lineX.setPosition(XAxis.XAxisPosition.BOTTOM);
        //X轴不显示坐标
        lineX.setDrawLabels(true);
        //X轴显示网格线
        lineX.setDrawGridLines(true);
        //X轴设置对大的显示点数
        lineX.setAxisMaximum(X_NODE_COUNT);
        lineX.setLabelCount(5, true);
        //设置X轴渲染器
        StockXAxisRenderer stockXAxisRenderer = new StockXAxisRenderer(
                lineChart.getViewPortHandler(),
                lineX,
                lineChart.getTransformer(YAxis.AxisDependency.LEFT)
        );
        lineChart.setXAxisRenderer(stockXAxisRenderer);
        lineX.setValueFormatter(new StockXAxisFormatter());
    }

    /**
     * 初始化线图左Y轴
     */
    private void initLineLeftYAxis() {
        lineLeftY = lineChart.getAxisLeft();
        //左Y轴显示在图标内部
        lineLeftY.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //设置左Y轴的最小值
        lineLeftY.setAxisMinimum(LEFT_Y_VALUE_MIN);
        //设置左Y轴的最大值
        lineLeftY.setAxisMaximum(LEFT_Y_VALUE_MAX);
        //Y轴不显示网格线
        lineLeftY.setDrawGridLines(true);
        //默认值显示3个刻度
        lineLeftY.setLabelCount(Y_LABEL_COUNT, true);
        //设置左Y轴渲染器
        StockYAxisRenderer stockYAxisRenderer = new StockYAxisRenderer(
                lineChart.getViewPortHandler(),
                lineLeftY,
                lineChart.getTransformer(YAxis.AxisDependency.LEFT)
        );
        stockYAxisRenderer.setLabelColorArr(colorArr);
        stockYAxisRenderer.setFlatValue((LEFT_Y_VALUE_MAX + LEFT_Y_VALUE_MIN) / 2);
        lineChart.setRendererLeftYAxis(stockYAxisRenderer);
        //设置右Y轴数值格式器
        lineLeftY.setValueFormatter(
                new StockPriceFormatter()
                        .setNumberFormatter(false)
                        .initFormatter()
        );
    }

    /**
     * 初始化线图右Y轴
     */
    private void initLineRightYAxis() {
        lineRightY = lineChart.getAxisRight();
        //右Y轴显示在图标内部
        lineRightY.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //设置右Y轴的最小值
        lineRightY.setAxisMinimum(RIGHT_Y_VALUE_MIN);
        //设置右Y轴的最大值
        lineRightY.setAxisMaximum(RIGHT_Y_VALUE_MAX);
        //Y轴不显示网格线
        lineRightY.setDrawGridLines(false);
        //添加增幅为0的提示线
        LimitLine zeroLimitLine = new LimitLine(0);
        zeroLimitLine.enableDashedLine(10f,10f, 0);
        zeroLimitLine.setLineColor(borderColor);
        lineRightY.addLimitLine(zeroLimitLine);
        //默认值显示3个刻度
        lineRightY.setLabelCount(Y_LABEL_COUNT, true);
        //设置右Y轴渲染器
        StockYAxisRenderer stockYAxisRenderer = new StockYAxisRenderer(
                lineChart.getViewPortHandler(),
                lineRightY,
                lineChart.getTransformer(YAxis.AxisDependency.RIGHT)
        );
        stockYAxisRenderer.setLabelColorArr(colorArr);
        stockYAxisRenderer.setFlatValue(0);
        lineChart.setRendererRightYAxis(stockYAxisRenderer);
        //设置右Y轴数值格式器
        lineRightY.setValueFormatter(
                new StockPercentFormatter()
                        .setNumberFormatter(false)
                        .initFormatter()
        );
    }

//    /**
//     * 初始化柱图
//     */
//    private void initBarChart() {
//        //设置边框颜色
//        barChart.setBorderColor(borderColor);
//        barChart.setNoDataText(NO_DATA_STR);
//        barChart.setData(getTestBarData());
//        initBarXAxis();
//        initBarLeftYAxis();
//        initBarRightYAxis();
//    }
//
//    /**
//     * 初始化柱图X轴
//     */
//    private void initBarXAxis() {
//        barX = barChart.getXAxis();
//    }
//
//    /**
//     * 初始化柱图左Y轴
//     */
//    private void initBarLeftYAxis() {
//        barLeftY = barChart.getAxisLeft();
//    }
//
//    /**
//     * 初始化柱图右Y轴
//     */
//    private void initBarRightYAxis() {
//        barRightY = barChart.getAxisRight();
//    }

    /**
     * 获取随机值
     * @param min
     * @param max
     * @return
     */
    private float random(double min, double max) {
        return (float) (Math.random() * (max - min) + min);
    }

    private LineData getTestLineData()
    {
        List<Entry> lineListEntry = new ArrayList<>(Y_NODE_COUNT);
        for (int i = 0; i < Y_NODE_COUNT; i++) {
            lineListEntry.add(
                    new Entry(i, random(LEFT_Y_VALUE_MIN, LEFT_Y_VALUE_MAX))
            );
        }
        LineDataSet lineDataSet = new LineDataSet(lineListEntry, "线图");
        //不显示圆圈
        lineDataSet.setDrawCircles(false);
        //不显示数值
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(priceLineColor);
        LineData lineData = new LineData(lineDataSet);
        return lineData;
    }


    private BarData getTestBarData()
    {
        List<BarEntry> lineListEntry = new ArrayList<>(Y_NODE_COUNT);
        for (int i = 0; i < Y_NODE_COUNT; i++) {
            lineListEntry.add(new BarEntry(i, (float) random(LEFT_Y_VALUE_MIN, LEFT_Y_VALUE_MAX)));
        }
        BarData barData = new BarData(new BarDataSet(lineListEntry, "柱图"));
        return barData;
    }
}
