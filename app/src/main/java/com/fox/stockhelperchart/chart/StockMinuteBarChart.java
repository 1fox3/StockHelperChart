package com.fox.stockhelperchart.chart;

import android.content.Context;
import android.util.AttributeSet;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.markerview.StockMinuteLineLeftYAxisMarker;
import com.fox.stockhelperchart.renderer.StockBarChartRenderer;
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
        initStockMinuteBarChart();
    }

    public StockMinuteBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStockMinuteBarChart();
    }

    public StockMinuteBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initStockMinuteBarChart();
    }

    protected void initStockMinuteBarChart() {
        //调用自己的渲染器
        mRenderer = new StockBarChartRenderer(this, mAnimator, mViewPortHandler);
        StockMinuteLineLeftYAxisMarker stockMinuteLineLeftYAxisMarker =
                new StockMinuteLineLeftYAxisMarker(getContext(), R.layout.markerview_str);
        stockMinuteLineLeftYAxisMarker.setChartView(this);
        setMarker(stockMinuteLineLeftYAxisMarker);
    }
}
