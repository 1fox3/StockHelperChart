package com.fox.stockhelperchart;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * @author lusongsong
 * @date 2021/2/26 16:08
 */
public class BaseStockChart extends LinearLayout {
    /**
     * 无数据默认显示的字符串
     */
    public static final String NO_DATA_STR = "加载中...";
    /**
     * 颜色数组
     */
    protected int[] colorArr;
    /**
     * 图标边框颜色
     */
    protected int borderColor;
    /**
     * 价格线颜色
     */
    protected int priceLineColor;

    public BaseStockChart(Context context) {
        super(context);
    }

    public BaseStockChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseStockChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseStockChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void initChart()
    {
        Context context = getContext();
        colorArr = new int[] {
                ContextCompat.getColor(context, R.color.stockUp),
                ContextCompat.getColor(context, R.color.stockDown),
                ContextCompat.getColor(context, R.color.stockFlat),
        };
        borderColor = ContextCompat.getColor(context, R.color.chartBorder);
        priceLineColor = ContextCompat.getColor(context, R.color.priceLine);
    }
}
