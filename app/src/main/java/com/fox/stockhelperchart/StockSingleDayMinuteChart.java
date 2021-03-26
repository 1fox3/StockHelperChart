package com.fox.stockhelperchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.fox.stockhelperchart.chart.StockSingleDayMinuteBarChart;
import com.fox.stockhelperchart.chart.StockSingleDayMinuteLineChart;
import com.fox.stockhelperchart.formatter.StockPriceFormatter;
import com.fox.stockhelperchart.renderer.StockBarYAxisRenderer;
import com.fox.stockhelperchart.renderer.StockYAxisRenderer;
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
public class StockSingleDayMinuteChart extends BaseStockChart {
    /**
     * 分钟线图数据
     */
    @BindView(R.id.stockSingleDayMinuteLineChart)
    StockSingleDayMinuteLineChart lineChart;


    /**
     * 分钟柱图数据
     */
    @BindView(R.id.stockSingleDayMinuteBarChart)
    StockSingleDayMinuteBarChart barChart;

    public StockSingleDayMinuteChart(Context context) {
        super(context);
    }

    public StockSingleDayMinuteChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StockSingleDayMinuteChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StockSingleDayMinuteChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
                R.layout.stock_single_day_minute_chart, this, true
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
        lineX.setAxisMaximum(X_NODE_COUNT);
        //设置默认值显示的刻度数量
        lineX.setLabelCount(X_LABEL_COUNT, true);
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
        //设置右Y轴的最小值
        lineRightY.setAxisMinimum(RIGHT_Y_VALUE_MIN);
        //设置右Y轴的最大值
        lineRightY.setAxisMaximum(RIGHT_Y_VALUE_MAX);
        //添加增幅为0的提示线
        LimitLine zeroLimitLine = new LimitLine(0);
        zeroLimitLine.enableDashedLine(10f, 10f, 0);
        zeroLimitLine.setLineColor(borderColor);
        zeroLimitLine.setLineWidth(1);
        lineRightY.addLimitLine(zeroLimitLine);
        //设置默认值显示的刻度数量
        lineRightY.setLabelCount(LINE_Y_LABEL_COUNT, true);
        //设置右Y轴渲染器
        StockYAxisRenderer stockYAxisRenderer = new StockYAxisRenderer(
                lineChart.getViewPortHandler(),
                lineRightY,
                lineChart.getTransformer(YAxis.AxisDependency.RIGHT)
        );
        stockYAxisRenderer.setLabelColorArr(colorArr);
        stockYAxisRenderer.setFlatValue(0);
        lineChart.setRendererRightYAxis(stockYAxisRenderer);
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
    }

    /**
     * 初始化柱图X轴
     */
    private void initBarXAxis() {
        barX = barChart.getXAxis();
        //设置默认值显示的刻度数量
        barX.setLabelCount(X_LABEL_COUNT, true);
        //X轴设置最大的显示点数
        barX.setAxisMaximum(X_NODE_COUNT);
    }

    /**
     * 初始化柱图左Y轴
     */
    private void initBarLeftYAxis() {
        barLeftY = barChart.getAxisLeft();
        //设置默认值显示的刻度数量
        barLeftY.setLabelCount(BAR_Y_LABEL_COUNT, true);
        //设置左Y轴渲染器
        StockBarYAxisRenderer stockYAxisRenderer = new StockBarYAxisRenderer(
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
        stockYAxisRenderer.setLabels(labelArr);
        barChart.setRendererLeftYAxis(stockYAxisRenderer);
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
                System.out.println(h.getX() + ":" + h.getDataSetIndex() + ":" + -1);
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
                System.out.println(h);
                barChart.highlightValue(h);
                lineChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
            }

            @Override
            public void onNothingSelected() {
                lineChart.highlightValues(null);
            }
        });
    }

    /**
     * 获取随机值
     *
     * @param min
     * @param max
     * @return
     */
    private float random(double min, double max) {
        return (float) (Math.random() * (max - min) + min);
    }

    private LineData getTestLineData() {
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
