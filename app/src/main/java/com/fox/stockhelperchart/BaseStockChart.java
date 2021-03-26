package com.fox.stockhelperchart;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

/**
 * @author lusongsong
 * @date 2021/2/26 16:08
 */
public class BaseStockChart extends LinearLayout {
    /**
     * X轴数据量
     */
    public static int X_NODE_COUNT = 241;
    /**
     * Y轴数据量
     */
    public static int Y_NODE_COUNT = 130;
    /**
     * X轴默认显示的刻度数
     */
    public static int X_LABEL_COUNT = 5;
    /**
     * 线图Y轴默认显示的刻度数
     */
    public static int LINE_Y_LABEL_COUNT = 5;
    /**
     * 柱图Y轴默认显示的刻度数
     */
    public static int BAR_Y_LABEL_COUNT = 3;
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
