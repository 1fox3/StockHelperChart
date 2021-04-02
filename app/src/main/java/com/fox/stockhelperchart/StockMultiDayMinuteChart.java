package com.fox.stockhelperchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.fox.stockhelperchart.chart.StockMultiDayMinuteBarChart;
import com.fox.stockhelperchart.chart.StockMultiDayMinuteLineChart;
import com.fox.stockhelperchart.formatter.StockPriceFormatter;
import com.fox.stockhelperchart.renderer.yaxis.StockMultiDayMinuteBarYAxisRenderer;
import com.fox.stockhelperchart.renderer.yaxis.StockMultiDayMinuteLineYAxisRenderer;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lusongsong
 * @date 2021/3/26 18:07
 */
public class StockMultiDayMinuteChart extends BaseStockChart {
    /**
     * 默认展示的天数
     */
    public static int DAY_NUM = 5;
    public static int MULTI_DAY_Y_NODE_COUNT = 4 * X_NODE_COUNT + Y_NODE_COUNT;

    @BindView(R.id.stockMultiDayMinuteLineChart)
    StockMultiDayMinuteLineChart lineChart;

    @BindView(R.id.stockMultiDayMinuteBarChart)
    StockMultiDayMinuteBarChart barChart;

    public StockMultiDayMinuteChart(Context context) {
        super(context);
    }

    public StockMultiDayMinuteChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StockMultiDayMinuteChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StockMultiDayMinuteChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化图表
     */
    public void initChart() {
        //父类的初始化图表
        super.initChart();
        //绑定视图
        bindLayout();
        //设置数据选择监听器
        setValueSelectedListener();
        //初始化线图
        initLineChart();
        //初始化柱图
        initBarChart();
    }

    /**
     * 绑定布局文件
     */
    private void bindLayout() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.stock_multi_day_minute_chart, this, true
        );
        ButterKnife.bind(this, view);
    }

    /**
     * 初始化线图
     */
    private void initLineChart() {
        //初始化线图X轴
        initLineXAxis();
        //初始化线图左Y轴
        initLineLeftYAxis();
        //初始化线图右Y轴
        initLineRightYAxis();
        //设置显示数据
        lineChart.setData(getTestLineData());
    }

    /**
     * 初始化线图X轴
     */
    private void initLineXAxis() {
        lineX = lineChart.getXAxis();
        //X轴设置最大的显示点数
        lineX.setAxisMaximum(X_NODE_COUNT * DAY_NUM);
        //设置默认值显示的刻度数量
        lineX.setLabelCount(DAY_NUM, true);
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
        //设置默认值显示的刻度数量
        lineLeftY.setLabelCount(LINE_Y_LABEL_COUNT, true);
        //设置左Y轴渲染器
        StockMultiDayMinuteLineYAxisRenderer stockMultiDayMinuteLineYAxisRenderer =
                new StockMultiDayMinuteLineYAxisRenderer(
                        lineChart.getViewPortHandler(),
                        lineLeftY,
                        lineChart.getTransformer(YAxis.AxisDependency.LEFT)
                );
        stockMultiDayMinuteLineYAxisRenderer.setLabelColorArr(colorArr);
        stockMultiDayMinuteLineYAxisRenderer.setFlatValue((LEFT_Y_VALUE_MAX + LEFT_Y_VALUE_MIN) / 2);
        stockMultiDayMinuteLineYAxisRenderer.setLabelStep(1);
        lineChart.setRendererLeftYAxis(stockMultiDayMinuteLineYAxisRenderer);
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
        //设置右Y轴的最小值
        lineRightY.setAxisMinimum(RIGHT_Y_VALUE_MIN);
        //设置右Y轴的最大值
        lineRightY.setAxisMaximum(RIGHT_Y_VALUE_MAX);
        //添加增幅为0的提示线
        LimitLine zeroLimitLine = new LimitLine(0);
        zeroLimitLine.enableDashedLine(10f, 10f, 0);
        zeroLimitLine.setLineColor(borderColor);
        lineRightY.addLimitLine(zeroLimitLine);
        //设置默认值显示的刻度数量
        lineRightY.setLabelCount(LINE_Y_LABEL_COUNT, true);
        //设置右Y轴渲染器
        StockMultiDayMinuteLineYAxisRenderer stockMultiDayMinuteLineYAxisRenderer =
                new StockMultiDayMinuteLineYAxisRenderer(
                        lineChart.getViewPortHandler(),
                        lineRightY,
                        lineChart.getTransformer(YAxis.AxisDependency.RIGHT)
                );
        stockMultiDayMinuteLineYAxisRenderer.setLabelColorArr(colorArr);
        stockMultiDayMinuteLineYAxisRenderer.setFlatValue(0);
        stockMultiDayMinuteLineYAxisRenderer.setLabelStep(1);
        lineChart.setRendererRightYAxis(stockMultiDayMinuteLineYAxisRenderer);
    }

    /**
     * 初始化柱图
     */
    private void initBarChart() {
        //设置边框颜色
        barChart.setBorderColor(borderColor);
        //设置无数据时显示的文案
        barChart.setNoDataText(NO_DATA_STR);
        //初始化柱图X轴
        initBarXAxis();
        //初始化柱图左Y轴
        initBarLeftYAxis();
        //初始化柱图右Y轴
        initBarRightYAxis();
        //设置数据
        barChart.setData(getTestBarData());
        //设置与上边无间隔
        ViewPortHandler viewPortHandler = barChart.getViewPortHandler();
        barChart.setViewPortOffsets(
                viewPortHandler.offsetLeft(),
                5,
                viewPortHandler.offsetRight(),
                viewPortHandler.offsetBottom()
        );
    }

    /**
     * 初始化柱图X轴
     */
    private void initBarXAxis() {
        barX = barChart.getXAxis();
        //设置默认值显示的刻度数量
        barX.setLabelCount(X_LABEL_COUNT, true);
        //X轴设置最大的显示点数
        barX.setAxisMaximum(X_NODE_COUNT * DAY_NUM);
    }

    /**
     * 初始化柱图左Y轴
     */
    private void initBarLeftYAxis() {
        barLeftY = barChart.getAxisLeft();
        //设置默认值显示的刻度数量
        barLeftY.setLabelCount(BAR_Y_LABEL_COUNT, true);
        //设置左Y轴渲染器
        StockMultiDayMinuteBarYAxisRenderer stockMultiDayMinuteBarYAxisRenderer =
                new StockMultiDayMinuteBarYAxisRenderer(
                        barChart.getViewPortHandler(),
                        barLeftY,
                        barChart.getTransformer(YAxis.AxisDependency.LEFT)
                );
        //设置左Y轴刻度值
        String[] labelArr = new String[BAR_Y_LABEL_COUNT];
        for (int i = 0; i < BAR_Y_LABEL_COUNT; i++) {
            if (i == 0) {
                labelArr[i] = "万手";
            } else if (i == BAR_Y_LABEL_COUNT - 1) {
                labelArr[i] = "1234";
            } else {
                labelArr[i] = "";
            }
        }
        stockMultiDayMinuteBarYAxisRenderer.setLabels(labelArr);
        barChart.setRendererLeftYAxis(stockMultiDayMinuteBarYAxisRenderer);
    }

    /**
     * 初始化柱图右Y轴
     */
    private void initBarRightYAxis() {
        barRightY = barChart.getAxisRight();
        //设置默认值显示的刻度数量
        barRightY.setLabelCount(BAR_Y_LABEL_COUNT, true);
    }

    /**
     * 设置数值选择监听器
     */
    private void setValueSelectedListener() {
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineChart.highlightValue(h);
                barChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
            }

            @Override
            public void onNothingSelected() {
                barChart.highlightValues(null);
            }
        });
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart.highlightValue(h);
                lineChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
            }

            @Override
            public void onNothingSelected() {
                lineChart.highlightValues(null);
            }
        });
    }

    private LineData getTestLineData() {
        List<Entry> lineListEntry = new ArrayList<>(Y_NODE_COUNT);
        for (int i = 0; i <Y_NODE_COUNT; i++) {
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
        //设置数值选择是的颜色
        lineDataSet.setHighLightColor(colorArr[1]);
        LineData lineData = new LineData(lineDataSet);
        return lineData;
    }


    private BarData getTestBarData() {
        List<BarEntry> lineListEntry = new ArrayList<>(Y_NODE_COUNT);
        int[] colors = new int[Y_NODE_COUNT];
        for (int i = 0; i < Y_NODE_COUNT; i++) {
            lineListEntry.add(new BarEntry(i, (float) random(LEFT_Y_VALUE_MIN, LEFT_Y_VALUE_MAX)));
            //对应的颜色
            colors[i] = colorArr[(int) random(0, 2)];
        }
        BarDataSet barDataSet = new BarDataSet(lineListEntry, "柱图");
        barDataSet.setColors(colors);
        //设置数值选择是的颜色
        barDataSet.setHighLightColor(colorArr[1]);
        barDataSet.setHighlightEnabled(true);
        BarData barData = new BarData(barDataSet);
        return barData;
    }
}
