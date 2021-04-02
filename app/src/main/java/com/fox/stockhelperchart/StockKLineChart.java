package com.fox.stockhelperchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.fox.stockhelperchart.chart.StockKLineBarCombinedChart;
import com.fox.stockhelperchart.chart.StockKLineLineCombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockKLineChart extends BaseStockChart {

    @BindView(R.id.stockKLineLineCombinedChart)
    StockKLineLineCombinedChart lineChart;

    @BindView(R.id.stockKLineBarCombinedChart)
    StockKLineBarCombinedChart barChart;

    public StockKLineChart(Context context) {
        super(context);
    }

    public StockKLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StockKLineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StockKLineChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
                R.layout.stock_kline_chart, this, true
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
        lineChart.setData(getTestLineCombinedData());
    }

    /**
     * 初始化线图X轴
     */
    private void initLineXAxis() {
        lineX = lineChart.getXAxis();
        //设置默认值显示的刻度数量
        lineX.setLabelCount(X_LABEL_COUNT, true);
    }

    /**
     * 初始化线图左Y轴
     */
    private void initLineLeftYAxis() {
        lineLeftY = lineChart.getAxisLeft();
        //设置默认值显示的刻度数量
        lineLeftY.setLabelCount(LINE_Y_LABEL_COUNT, true);
    }

    /**
     * 初始化线图右Y轴
     */
    private void initLineRightYAxis() {}

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
        barChart.setData(getTestBarCombinedData());
        //设置与上边无间隔
        ViewPortHandler viewPortHandler = barChart.getViewPortHandler();
        barChart.setViewPortOffsets(
                viewPortHandler.offsetLeft(),
                0,
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
    }

    /**
     * 初始化柱图左Y轴
     */
    private void initBarLeftYAxis() {
        barLeftY = barChart.getAxisLeft();
        //设置默认值显示的刻度数量
        barLeftY.setLabelCount(BAR_Y_LABEL_COUNT, true);
    }

    /**
     * 初始化柱图右Y轴
     */
    private void initBarRightYAxis() {}

    private CombinedData getTestLineCombinedData() {
        CombinedData lineCombinedData = new CombinedData();
        lineCombinedData.setData(getTestLineData());
        return lineCombinedData;
    }
    private CombinedData getTestBarCombinedData() {
        CombinedData barCombinedData = new CombinedData();
        barCombinedData.setData(getTestBarData());
        return barCombinedData;
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
