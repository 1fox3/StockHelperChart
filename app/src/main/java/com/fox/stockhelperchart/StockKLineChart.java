package com.fox.stockhelperchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fox.spider.stock.constant.StockConst;
import com.fox.stockhelperchart.adapter.StockKLineBarTypeAdapter;
import com.fox.stockhelperchart.chart.StockKLineBarCombinedChart;
import com.fox.stockhelperchart.chart.StockKLineLineCombinedChart;
import com.fox.stockhelperchart.listener.StockKLineOnChartGestureListener;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockKLineChart extends BaseStockChart {
    public static final int KLINE_BAR_TYPE_DEAL_NUM = 0;
    public static final int KLINE_BAR_TYPE_DEAL_MONEY = 1;
    public static final int KLINE_BAR_TYPE_MACD = 2;
    public static final int KLINE_BAR_TYPE_KDJ = 3;
    public static final int KLINE_BAR_TYPE_RSI = 4;
    public static final int KLINE_BAR_TYPE_BOLL = 5;
    /**
     * 日期类型
     */
    List<Integer> kLineSupportDateType = Arrays.asList(
            StockConst.DT_DAY,
            StockConst.DT_MONTH,
            StockConst.DT_MONTH
    );
    /**
     * 复权类型
     */
    Map<String, Integer> kLineFqTypeMap = new TreeMap<String, Integer>() {{
        put("除权", StockConst.SFQ_AFTER);
        put("前复权", StockConst.SFQ_BEFORE);
    }};

    /**
     * 柱状图类型
     */
    Map<String, Integer> kLineBarTypeMap = new LinkedHashMap<String, Integer>() {{
        put("成交量", KLINE_BAR_TYPE_DEAL_NUM);
        put("成交金额", KLINE_BAR_TYPE_DEAL_MONEY);
        put("MACD", KLINE_BAR_TYPE_MACD);
        put("KDJ", KLINE_BAR_TYPE_KDJ);
        put("RSI", KLINE_BAR_TYPE_RSI);
        put("BOLL", KLINE_BAR_TYPE_BOLL);
    }};

    @BindView(R.id.stockKLineLineCombinedChart)
    StockKLineLineCombinedChart lineChart;

    @BindView(R.id.stockKLineBarCombinedChart)
    StockKLineBarCombinedChart barChart;

    @BindView(R.id.stockKLineBarTypeLV)
    ListView stockKLineBarTypeLV;

    StockKLineBarTypeAdapter stockKLineBarTypeAdapter;

    @BindView(R.id.stockFQTypeNoTV)
    TextView stockFQTypeNoTV;

    @BindView(R.id.stockFQTypeBeforeTV)
    TextView stockFQTypeBeforeTV;

    int dateType = StockConst.DT_DAY;
    int fqType = StockConst.SFQ_BEFORE;
    int barType = 0;

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
        //设置数据选择监听器
        setValueSelectedListener();
        //设置操作同步
        setOnChartGestureListener();
    }

    /**
     * 绑定布局文件
     */
    private void bindLayout() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.stock_kline_chart, this, true
        );
        ButterKnife.bind(this, view);
        stockKLineBarTypeAdapter = new StockKLineBarTypeAdapter(getContext(), R.layout.stock_kline_bar_type_item);
        stockKLineBarTypeAdapter.setStockLineBarTypeList(new ArrayList<>(kLineBarTypeMap.keySet()));
        stockKLineBarTypeAdapter.setSelectColor(upColor);
        stockKLineBarTypeLV.setAdapter(stockKLineBarTypeAdapter);
        stockKLineBarTypeLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        stockKLineBarTypeLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.kLineBarTypeItemTV);
                setBarType(textView.getText().toString());
                stockKLineBarTypeAdapter.setSelectedPosition(position);
                stockKLineBarTypeAdapter.notifyDataSetChanged();
            }
        });
        stockFQTypeNoTV.setOnClickListener(getFQTypeOnClickListener());
        stockFQTypeBeforeTV.setOnClickListener(getFQTypeOnClickListener());
        stockFQTypeNoTV.callOnClick();
    }

    /**
     * 设置日期类型
     *
     * @param dtType
     */
    public void setDateType(int dtType) {
        dateType = kLineSupportDateType.contains(dtType) ? dtType : dateType;
    }

    /**
     * 设置柱状图类型
     *
     * @param barTypeStr
     */
    public void setBarType(String barTypeStr) {
        barType = kLineBarTypeMap.containsKey(barTypeStr) ? kLineBarTypeMap.get(barTypeStr) : barType;
    }

    /**
     * 设置复权类型
     *
     * @param fqTypeStr
     */
    public void setFqType(String fqTypeStr) {
        fqType = kLineFqTypeMap.containsKey(fqTypeStr) ?
                kLineFqTypeMap.get(fqTypeStr) : fqType;
        stockFQTypeNoTV.setTextColor(Color.BLACK);
        stockFQTypeBeforeTV.setTextColor(Color.BLACK);
        if (fqType == StockConst.SFQ_AFTER) {
            stockFQTypeNoTV.setTextColor(upColor);
        }
        if (fqType == StockConst.SFQ_BEFORE) {
            stockFQTypeBeforeTV.setTextColor(upColor);
        }
    }

    private OnClickListener getFQTypeOnClickListener() {
        return new OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                setFqType((((TextView) v)).getText().toString());
            }
        };
    }

    /**
     * 设置数值选择监听器
     */
    private void setValueSelectedListener() {
        //移动十字标数据监听
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineChart.highlightValue(h);
                if (barChart.getData().getBarData().getDataSets().size() != 0) {
                    Highlight highlight = new Highlight(h.getX(), h.getDataSetIndex(), h.getStackIndex());
                    highlight.setDataIndex(h.getDataIndex());
                    barChart.highlightValues(new Highlight[]{highlight});
                } else {
                    Highlight highlight = new Highlight(h.getX(), 2, h.getStackIndex());
                    highlight.setDataIndex(0);
                    barChart.highlightValues(new Highlight[]{highlight});
                }
            }

            @Override
            public void onNothingSelected() {
                barChart.highlightValues(null);
            }
        });
        //移动十字标数据监听
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart.highlightValue(h);
                Highlight highlight = new Highlight(h.getX(), 0, h.getStackIndex());
                highlight.setDataIndex(1);
                lineChart.highlightValues(new Highlight[]{highlight});
            }

            @Override
            public void onNothingSelected() {
                lineChart.highlightValues(null);
            }
        });
    }

    /**
     * 设置操作同步
     */
    private void setOnChartGestureListener() {
        lineChart.setOnChartGestureListener(
                new StockKLineOnChartGestureListener(lineChart, new Chart[]{barChart})
        );
        barChart.setOnChartGestureListener(
                new StockKLineOnChartGestureListener(barChart, new Chart[]{lineChart})
        );
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
        CombinedData combinedData = getTestLineCombinedData();
        lineChart.setData(combinedData);
        //计算缩放比例,以便可以左右滑动
        lineChart.zoom(getStockScaleX(), 0, 0, 0);
        //设置X轴的坐标范围，以便显示全部图表
        lineChart.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
        lineChart.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);
        lineChart.notifyDataSetChanged();
        //滑动到尾部
        lineChart.moveViewToX(getStockDataList().size() - 1);
    }

    private float getStockScaleX() {
        return getStockDataList().size() / 70f;
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
    private void initLineRightYAxis() {
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
        CombinedData combinedData = getTestBarCombinedData();
        barChart.setData(combinedData);
        //计算缩放比例,以便可以左右滑动
        barChart.zoom(getStockScaleX(), 0, 0, 0);
        //设置X轴的坐标范围，以便显示全部图表
        barChart.getXAxis().setAxisMinimum(combinedData.getXMin() - 0.5f);
        barChart.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);
        barChart.notifyDataSetChanged();
        //滑动到尾部
        barChart.moveViewToX(getStockDataList().size() - 1);
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
    private void initBarRightYAxis() {
    }

    private CombinedData getTestLineCombinedData() {
        CombinedData lineCombinedData = new CombinedData();
        CandleDataSet candleDataSet = new CandleDataSet(getCandleEntryList(), "蜡烛线");
        candleDataSet.setDrawHorizontalHighlightIndicator(true);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(ContextCompat.getColor(getContext(), R.color.stockUp));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(getContext(), R.color.stockDown));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(getContext(), R.color.stockUp));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(getContext(), R.color.stockFlat));
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setValueTextSize(10);
        candleDataSet.setDrawValues(false);
        lineCombinedData.setData(new CandleData(candleDataSet));
        lineCombinedData.setData(getTestLineData());
        return lineCombinedData;
    }

    private ArrayList<CandleEntry> getCandleEntryList() {
        ArrayList<CandleEntry> candleEntryList = new ArrayList<>();
        List<List<String>> stockDataList = getStockDataList();
        for (int i = 0; i < stockDataList.size(); i++) {
            List<String> stockData = stockDataList.get(i);
            candleEntryList.add(
                    new CandleEntry(
                            i,
                            Float.valueOf(stockData.get(2)),
                            Float.valueOf(stockData.get(3)),
                            Float.valueOf(stockData.get(1)),
                            Float.valueOf(stockData.get(4))
                    )
            );
        }
        return candleEntryList;
    }

    private LineData getTestLineData() {
        List<ILineDataSet> lineDataMA = new ArrayList<>();
        ArrayList<Entry> line5Entries = new ArrayList<>();
        ArrayList<Entry> line10Entries = new ArrayList<>();
        ArrayList<Entry> line20Entries = new ArrayList<>();
        List<List<String>> stockDataList = getStockDataList();
        for (int i = 0; i < stockDataList.size(); i++) {
            List<String> stockData = stockDataList.get(i);
            line5Entries.add(new Entry(i, Float.valueOf(stockData.get(8))));
            line10Entries.add(new Entry(i, Float.valueOf(stockData.get(9))));
            line20Entries.add(new Entry(i, Float.valueOf(stockData.get(10))));
        }
        lineDataMA.add(getTestLineMAData(line5Entries, "MA5", ma5Color));
        lineDataMA.add(getTestLineMAData(line10Entries, "MA10", ma10Color));
        lineDataMA.add(getTestLineMAData(line20Entries, "MA20", ma20Color));
        LineData lineData = new LineData(lineDataMA);
        return lineData;
    }

    private LineDataSet getTestLineMAData(List<Entry> entryList, String label, int color) {
        LineDataSet lineDataSet = new LineDataSet(entryList, label);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(0.6f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSet;
    }

    private CombinedData getTestBarCombinedData() {
//        return getTestBarVolumeData();
//        return getTestBarMACDData();
//        return getTestBarKDJData();
//        return getTestBarBOLLData();
        return getTestBarRSIData();
    }

    private CombinedData getTestBarVolumeData() {
        List<List<String>> stockDataList = getStockDataList();
        ArrayList<BarEntry> barEntries = new ArrayList<>(stockDataList.size());
        int[] colors = new int[stockDataList.size()];
        for (int i = 0; i < stockDataList.size(); i++) {
            List<String> stockData = stockDataList.get(i);
            float openPrice = Float.parseFloat(stockData.get(1));
            float closePrice = Float.parseFloat(stockData.get(4));
            int colorIdx = openPrice == closePrice ? 2 : openPrice > closePrice ? 1 : 0;
            barEntries.add(
                    new BarEntry(
                            Float.valueOf(i), Float.valueOf(stockData.get(8))
                    )
            );
            colors[i] = colorArr[colorIdx];
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "柱图");
        barDataSet.setColors(colors);
        barDataSet.setDrawValues(false);
        //设置数值选择是的颜色
        barDataSet.setHighLightColor(ContextCompat.getColor(getContext(), R.color.stockUp));
        barDataSet.setHighlightEnabled(true);
        BarData barData = new BarData(barDataSet);
        CombinedData barCombinedData = new CombinedData();
        barCombinedData.setData(barData);
        barCombinedData.setData(new LineData());
        barCombinedData.setData(new CandleData());
        return barCombinedData;
    }

    private CombinedData getTestBarMACDData() {
        int mNum = 9;
        int shortNum = 12;
        int longNum = 26;
        List<List<String>> stockDataList = getStockDataList();
        ArrayList<BarEntry> madcBarEntries = new ArrayList<>(stockDataList.size());
        ArrayList<Entry> deaBarEntries = new ArrayList<>(stockDataList.size());
        ArrayList<Entry> difBarEntries = new ArrayList<>(stockDataList.size());

        List<Float> dEAs = new ArrayList<Float>();
        List<Float> dIFs = new ArrayList<Float>();
        List<Float> mACDs = new ArrayList<Float>();

        float eMAShort = 0.0f;
        float eMALong = 0.0f;
        float closePrice = 0f;
        float dIF = 0.0f;
        float dEA = 0.0f;
        float mACD = 0.0f;
        for (int i = 0; i < stockDataList.size(); i++) {
            List<String> stockData = stockDataList.get(i);
            closePrice = Float.parseFloat(stockData.get(4));
            if (i == 0) {
                eMAShort = closePrice;
                eMALong = closePrice;
            } else {
                eMAShort = eMAShort * (1 - 2.0f / (shortNum + 1)) + closePrice * 2.0f / (shortNum + 1);
                eMALong = eMALong * (1 - 2.0f / (longNum + 1)) + closePrice * 2.0f / (longNum + 1);
            }
            dIF = eMAShort - eMALong;
            dEA = dEA * (1 - 2.0f / (mNum + 1)) + dIF * 2.0f / (mNum + 1);
            mACD = dIF - dEA;
            dEAs.add(dEA);
            dIFs.add(dIF);
            mACDs.add(mACD);
        }

        int[] colors = new int[stockDataList.size()];
        for (int i = 0; i < dEAs.size(); i++) {
            int colorIdx = mACDs.get(i) > 0 ? 0 : 1;
            colors[i] = colorArr[colorIdx];
            deaBarEntries.add(new Entry(i, dEAs.get(i)));
            difBarEntries.add(new Entry(i, dIFs.get(i)));
            madcBarEntries.add(new BarEntry(i, mACDs.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(madcBarEntries, "柱图");
        barDataSet.setDrawValues(false);
        //设置数值选择是的颜色
        barDataSet.setHighLightColor(ContextCompat.getColor(getContext(), R.color.stockUp));
        barDataSet.setHighlightEnabled(true);
        barDataSet.setColors(colors);
        BarData barData = new BarData(barDataSet);
        CombinedData barCombinedData = new CombinedData();
        barCombinedData.setData(barData);

        List<ILineDataSet> lineDataMA = new ArrayList<>();
        lineDataMA.add(getTestLineMAData(deaBarEntries, "dea", ma5Color));
        lineDataMA.add(getTestLineMAData(difBarEntries, "dif", ma10Color));
        LineData lineData = new LineData(lineDataMA);

        barCombinedData.setData(lineData);
        barCombinedData.setData(new CandleData());
        return barCombinedData;
    }

    private CombinedData getTestBarKDJData() {
        int n = 9;
        int m1 = 3;
        int m2 = 3;
        List<List<String>> stockDataList = getStockDataList();
        ArrayList<Entry> kEntries = new ArrayList<>(stockDataList.size());
        ArrayList<Entry> dEntries = new ArrayList<>(stockDataList.size());
        ArrayList<Entry> jEntries = new ArrayList<>(stockDataList.size());

        List<Float> kValues = new ArrayList<Float>();
        List<Float> dValues = new ArrayList<Float>();
        List<Float> jValues = new ArrayList<Float>();

        float k = 50.0f;
        float d = 50.0f;
        float j = 0.0f;
        float rSV = 0.0f;

        if (stockDataList != null && stockDataList.size() > 0) {
            float highPrice = 0f, lowPrice = 0f, currentHighPrice = 0f, currentLowPrice = 0f, closePrice = 0f;
            for (int i = 0; i < stockDataList.size(); i++) {
                List<String> stockData = stockDataList.get(i);
                currentHighPrice = Float.parseFloat(stockData.get(2));
                currentLowPrice = Float.parseFloat(stockData.get(3));
                closePrice = Float.parseFloat(stockData.get(4));

                if (0 == i) {
                    highPrice = currentHighPrice;
                    lowPrice = currentLowPrice;
                }
                if (i > 0) {
                    if (n == 0) {
                        highPrice = highPrice > currentHighPrice ? highPrice : currentHighPrice;
                        lowPrice = lowPrice < currentLowPrice ? lowPrice : currentLowPrice;
                    } else {
                        int t = i - n + 1;
                        Float[] wrs = getHighAndLowByK(t, i);
                        highPrice = wrs[0];
                        lowPrice = wrs[1];
                    }
                }
                if (highPrice != lowPrice) {
                    rSV = (float) ((closePrice - lowPrice) / (highPrice - lowPrice) * 100);
                } else {
                    rSV = 0;
                }
                k = k * (m1 - 1.0f) / m1 + rSV / m1;
                d = d * (m2 - 1.0f) / m2 + k / m2;
                j = (3 * k) - (2 * d);

                //其他软件没有大于100小于0的值，但是我算出来确实有，其它软件在0和100的时候出现直线，怀疑也是做了处理
                j = j < 0 ? 0 : j;
                j = j > 100 ? 100 : j;

                kValues.add(k);
                dValues.add(d);
                jValues.add(j);
            }
            for (int i = 0; i < kValues.size(); i++) {
                kEntries.add(new Entry(i, kValues.get(i)));
                dEntries.add(new Entry(i, dValues.get(i)));
                jEntries.add(new Entry(i, jValues.get(i)));
            }
        }

        List<ILineDataSet> lineDataMA = new ArrayList<>();
        LineDataSet kILineDataSet = getTestLineMAData(kEntries, "K", ma5Color);
        kILineDataSet.setHighlightEnabled(true);
        kILineDataSet.setHighLightColor(colorArr[0]);
        kILineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataMA.add(kILineDataSet);
        lineDataMA.add(getTestLineMAData(dEntries, "D", ma10Color));
        lineDataMA.add(getTestLineMAData(jEntries, "J", ma20Color));
        LineData lineData = new LineData(lineDataMA);
        CombinedData barCombinedData = new CombinedData();
        barCombinedData.setData(new BarData());
        barCombinedData.setData(lineData);
        barCombinedData.setData(new CandleData());
        return barCombinedData;
    }

    /**
     * 得到某区间内最高价和最低价
     *
     * @param a 开始位置 可以为0
     * @param b 结束位置
     * @return
     */
    private Float[] getHighAndLowByK(Integer a, Integer b) {
        if (a < 0) {
            a = 0;
        }
        List<List<String>> stockDataList = getStockDataList();
        List<String> stockData = stockDataList.get(a);
        float high = Float.parseFloat(stockData.get(2));
        float low = Float.parseFloat(stockData.get(3));
        Float[] wrs = new Float[2];
        for (int i = a; i <= b; i++) {
            stockData = stockDataList.get(i);
            float currentHighPrice = Float.parseFloat(stockData.get(2));
            float currentLowPrice = Float.parseFloat(stockData.get(3));
            high = high > currentHighPrice ? high : currentHighPrice;
            low = low < currentLowPrice ? low : currentLowPrice;
        }

        wrs[0] = high;
        wrs[1] = low;
        return wrs;
    }

    private CombinedData getTestBarBOLLData() {
        CandleDataSet candleDataSet = new CandleDataSet(getCandleEntryList(), "蜡烛线");
        candleDataSet.setDrawHorizontalHighlightIndicator(true);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(ContextCompat.getColor(getContext(), R.color.stockUp));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(getContext(), R.color.stockDown));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(getContext(), R.color.stockUp));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(getContext(), R.color.stockFlat));
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setValueTextSize(10);
        candleDataSet.setDrawValues(false);

        List<List<String>> stockDataList = getStockDataList();
        float ma = 0.0f;
        float md = 0.0f;
        float mb = 0.0f;
        float up = 0.0f;
        float dn = 0.0f;
        int boolNum = 26;
        float defult = 0;

        ArrayList<Float> ups = new ArrayList<>(stockDataList.size());
        ArrayList<Float> mbs = new ArrayList<>(stockDataList.size());
        ArrayList<Float> dns = new ArrayList<>(stockDataList.size());

        if (stockDataList != null && stockDataList.size() > 0) {
            float closeSum = 0.0f;
            float sum = 0.0f;
            int index = 0;
            int index2 = boolNum - 1;
            for (int i = 0; i < stockDataList.size(); i++) {
                float closePrice = Float.parseFloat(stockDataList.get(i).get(4));
                int k = i - boolNum + 1;
                if (i >= boolNum) {
                    index = boolNum;
                } else {
                    index = i + 1;
                }
                closeSum = getSumClose(k, i);
                ma = closeSum / index;
                sum = getSum(k, i, ma);
                md = (float) Math.sqrt(sum / index);
                mb = ((closeSum - closePrice) / (index - 1));
                up = mb + (2 * md);
                dn = mb - (2 * md);

                if (i < index2) {
                    mb = defult;
                    up = defult;
                    dn = defult;
                }
                ups.add(up);
                mbs.add(mb);
                dns.add(dn);
            }
        }

        ArrayList<Entry> upEntries = new ArrayList<>(stockDataList.size());
        ArrayList<Entry> mbEntries = new ArrayList<>(stockDataList.size());
        ArrayList<Entry> dnEntries = new ArrayList<>(stockDataList.size());

        for (int i = 0; i < ups.size(); i++) {
            upEntries.add(new Entry(i, ups.get(i)));
            mbEntries.add(new Entry(i, mbs.get(i)));
            dnEntries.add(new Entry(i, dns.get(i)));
        }

        List<ILineDataSet> lineDataMA = new ArrayList<>();
        LineDataSet kILineDataSet = getTestLineMAData(dnEntries, "K", ma20Color);
        kILineDataSet.setHighlightEnabled(true);
        kILineDataSet.setHighLightColor(colorArr[0]);
        kILineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataMA.add(kILineDataSet);
        lineDataMA.add(getTestLineMAData(upEntries, "up", ma5Color));
        lineDataMA.add(getTestLineMAData(mbEntries, "mb", ma10Color));
        LineData lineData = new LineData(lineDataMA);

        CombinedData barCombinedData = new CombinedData();
        barCombinedData.setData(new BarData());
        barCombinedData.setData(lineData);
        barCombinedData.setData(new CandleData(candleDataSet));
        return barCombinedData;
    }

    private Float getSum(Integer a, Integer b, Float ma) {
        if (a < 0) {
            a = 0;
        }
        List<List<String>> stockDataList = getStockDataList();
        float sum = 0.0f;
        for (int i = a; i <= b; i++) {
            float closePrice = Float.parseFloat(stockDataList.get(i).get(4));
            sum += ((closePrice - ma) * (closePrice - ma));
        }
        return sum;
    }

    private Float getSumClose(Integer a, Integer b) {
        if (a < 0) {
            a = 0;
        }
        List<List<String>> stockDataList = getStockDataList();
        float close = 0.0f;
        for (int i = a; i <= b; i++) {
            float closePrice = Float.parseFloat(stockDataList.get(i).get(4));
            close += closePrice;
        }

        return close;
    }

    private CombinedData getTestBarRSIData() {
        ArrayList<Entry> firstRSIEntries = getRSIEntryList(6, 0f);
        ArrayList<Entry> secondRSIEntries = getRSIEntryList(12, 0f);
        ArrayList<Entry> thirdRSIEntries = getRSIEntryList(24, 0f);

        List<ILineDataSet> lineDataMA = new ArrayList<>();
        lineDataMA.add(getTestLineMAData(firstRSIEntries, "D", ma5Color));
        lineDataMA.add(getTestLineMAData(secondRSIEntries, "J", ma10Color));
        LineDataSet kILineDataSet = getTestLineMAData(thirdRSIEntries, "K", ma20Color);
        kILineDataSet.setHighlightEnabled(true);
        kILineDataSet.setHighLightColor(colorArr[0]);
        kILineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataMA.add(kILineDataSet);
        LineData lineData = new LineData(lineDataMA);
        CombinedData barCombinedData = new CombinedData();
        barCombinedData.setData(new BarData());
        barCombinedData.setData(lineData);
        barCombinedData.setData(new CandleData());
        return barCombinedData;
    }

    private ArrayList<Entry> getRSIEntryList(int n, float defaultValue) {
        List<List<String>> stockDataList = getStockDataList();
        ArrayList<Entry> rsiEntries = new ArrayList<>(stockDataList.size());
        float sum = 0.0f;
        float dif = 0.0f;
        float rs = 0.0f;
        float rsi = 0.0f;
        int index = n - 1;
        if (stockDataList != null && stockDataList.size() > 0) {
            for (int i = 0; i < stockDataList.size(); i++) {
                if (n == 0) {
                    sum = 0.0f;
                    dif = 0.0f;
                } else {
                    int k = i - n + 1;
                    Float[] wrs = getAAndB(k, i);
                    sum = wrs[0];
                    dif = wrs[1];
                }
                if (dif != 0) {
                    float h = sum + dif;
                    rsi = sum / h * 100;
                } else {
                    rsi = 100;
                }

                if (i < index) {
                    rsi = defaultValue;
                }
                rsiEntries.add(new Entry(i, rsi));
            }
        }
        return rsiEntries;
    }

    private Float[] getAAndB(Integer a, Integer b) {
        if (a < 0) {
            a = 0;
        }
        float sum = 0.0f;
        float dif = 0.0f;
        float closeT, closeY;
        Float[] abs = new Float[2];
        List<List<String>> stockDataList = getStockDataList();
        for (int i = a; i <= b; i++) {
            if (i > a) {
                closeT = Float.parseFloat(stockDataList.get(i).get(4));
                closeY = Float.parseFloat(stockDataList.get(i - 1).get(4));

                float c = closeT - closeY;
                if (c > 0) {
                    sum = sum + c;
                } else {
                    dif = sum + c;
                }

                dif = Math.abs(dif);
            }
        }

        abs[0] = sum;
        abs[1] = dif;
        return abs;
    }

    private List<List<String>> getStockDataList() {
        List<List<String>> dataList = new ArrayList<>();
        dataList.add(Arrays.asList("1482681600000", "3095.579", "3122.881", "3068.415", "3122.569", "1.52571881E8", "1.70555610887E11", "3122.5174", "3126.6898", "3177.2412", "3194.8828666667", "3144.6370833333", "3110.154"));
        dataList.add(Arrays.asList("1482768000000", "3117.387", "3127.883", "3113.745", "3114.664", "1.41528939E8", "1.6219390854E11", "3124.875", "3122.6525", "3168.8282", "3191.8054666667", "3146.5786166667", "3122.569"));
        dataList.add(Arrays.asList("1482854400000", "3113.767", "3118.782", "3094.549", "3102.236", "1.35727087E8", "1.54329653046E11", "3117.8362", "3118.823", "3161.4383", "3188.3781", "3148.4849166667", "3114.664"));
        dataList.add(Arrays.asList("1482940800000", "3095.845", "3111.799", "3087.344", "3096.097", "1.32623292E8", "1.499191828E11", "3109.144", "3116.665", "3152.5777", "3184.6329333333", "3150.1118166667", "3102.236"));
        dataList.add(Arrays.asList("1483027200000", "3097.345", "3108.839", "3089.99", "3103.637", "1.3326713E8", "1.51732781752E11", "3107.8406", "3114.7306", "3145.5674", "3181.6589666667", "3151.7607166667", "3096.097"));
        dataList.add(Arrays.asList("1483372800000", "3105.309", "3136.456", "3105.309", "3135.921", "1.41567187E8", "1.59887139471E11", "3110.511", "3116.5142", "3142.128", "3178.9180666667", "3153.2236833333", "3103.637"));
        dataList.add(Arrays.asList("1483459200000", "3133.787", "3160.103", "3130.115", "3158.794", "1.6786085E8", "1.95914292648E11", "3119.337", "3122.106", "3140.08535", "3175.9328", "3154.78275", "3135.921"));
        dataList.add(Arrays.asList("1483545600000", "3157.906", "3168.502", "3154.281", "3165.411", "1.74727645E8", "1.99692026733E11", "3131.972", "3124.9041", "3137.24385", "3173.4086", "3156.5646333333", "3158.794"));
        dataList.add(Arrays.asList("1483632000000", "3163.776", "3172.035", "3153.025", "3154.321", "1.83708966E8", "2.07296038444E11", "3143.6168", "3126.3804", "3134.1916", "3170.4947666667", "3158.1142166667", "3165.411"));
        dataList.add(Arrays.asList("1483891200000", "3148.532", "3173.136", "3147.735", "3171.236", "1.71714075E8", "1.92110579317E11", "3157.1366", "3132.4886", "3131.10925", "3167.4713666667", "3159.9046666667", "3154.321"));
        dataList.add(Arrays.asList("1483977600000", "3167.57", "3174.578", "3157.332", "3161.671", "1.79759216E8", "1.9496321658E11", "3162.2866", "3136.3988", "3131.5443", "3163.6270666667", "3161.9130833333", "3171.236"));
        dataList.add(Arrays.asList("1484064000000", "3156.686", "3167.029", "3136.267", "3136.753", "1.78362221E8", "1.89186459361E11", "3157.8784", "3138.6077", "3130.6301", "3158.7547", "3162.7943833333", "3161.671"));
        dataList.add(Arrays.asList("1484150400000", "3133.602", "3144.97", "3115.979", "3119.289", "1.4888924E8", "1.61922296471E11", "3148.654", "3140.313", "3129.568", "3154.3965333333", "3163.37055", "3136.753"));
        dataList.add(Arrays.asList("1484236800000", "3116.083", "3130.515", "3102.163", "3112.764", "1.56274214E8", "1.74438553998E11", "3140.3426", "3141.9797", "3129.32235", "3149.0450333333", "3163.8423166667", "3119.289"));
        dataList.add(Arrays.asList("1484496000000", "3104.492", "3105.142", "3044.291", "3103.428", "2.57885996E8", "2.628260418E11", "3126.781", "3141.9588", "3128.3447", "3144.3645333333", "3164.0504333333", "3112.764"));
        dataList.add(Arrays.asList("1484582400000", "3087.03", "3108.907", "3072.338", "3108.775", "1.36157861E8", "1.54757554735E11", "3116.2018", "3139.2442", "3127.8792", "3141.1667333333", "3163.7259", "3103.428"));
        dataList.add(Arrays.asList("1484668800000", "3104.766", "3123.72", "3098.586", "3113.012", "1.31780128E8", "1.44546719277E11", "3111.4536", "3134.666", "3128.386", "3138.2789", "3163.4104666667", "3108.775"));
        dataList.add(Arrays.asList("1484755200000", "3104.971", "3115.778", "3094.006", "3101.299", "1.2385142E8", "1.39280461467E11", "3107.8556", "3128.2548", "3126.57945", "3134.2475", "3163.16025", "3113.012"));
        dataList.add(Arrays.asList("1484841600000", "3095.819", "3125.66", "3095.215", "3123.139", "1.22366777E8", "1.4147181135E11", "3109.9306", "3125.1366", "3125.7585", "3131.1732666667", "3163.3400666667", "3101.299"));
        dataList.add(Arrays.asList("1485100800000", "3125.421", "3145.841", "3125.421", "3136.775", "1.32660392E8", "1.48145735877E11", "3116.6", "3121.6905", "3127.08955", "3127.9696666667", "3163.8818166667", "3123.139"));
        dataList.add(Arrays.asList("1485187200000", "3134.592", "3149.531", "3131.218", "3142.553", "1.25973881E8", "1.34553852177E11", "3123.3556", "3119.7787", "3128.08875", "3127.6224333333", "3164.5828333333", "3136.775"));
        dataList.add(Arrays.asList("1485273600000", "3137.646", "3151.472", "3133.191", "3149.555", "1.12073827E8", "1.26765829992E11", "3130.6642", "3121.0589", "3129.8333", "3127.4397", "3165.0348166667", "3142.553"));
        dataList.add(Arrays.asList("1485360000000", "3149.217", "3163.104", "3148.908", "3159.166", "1.13876772E8", "1.25013193942E11", "3142.2376", "3125.0466", "3132.6798", "3128.0608666667", "3165.9753666667", "3149.555"));
        dataList.add(Arrays.asList("1486051200000", "3160.082", "3162.677", "3136.013", "3140.17", "9.2224736E7", "1.08023659889E11", "3145.6438", "3127.7872", "3134.88345", "3128.8106333333", "3166.1626", "3159.166"));
        dataList.add(Arrays.asList("1486310400000", "3143.093", "3158.843", "3135.387", "3156.984", "1.27198283E8", "1.5006769472E11", "3149.6856", "3133.1428", "3137.5508", "3129.9440666667", "3166.6903833333", "3140.17"));
        dataList.add(Arrays.asList("1486396800000", "3154.405", "3159.544", "3140.036", "3153.088", "1.28337442E8", "1.52559086118E11", "3151.7926", "3137.5741", "3138.40915", "3131.1108333333", "3167.0196333333", "3156.984"));
        dataList.add(Arrays.asList("1486483200000", "3148.086", "3167.447", "3132.033", "3166.982", "1.44920187E8", "1.70854126256E11", "3155.278", "3142.9711", "3138.81855", "3133.2477", "3167.3378833333", "3153.088"));
        dataList.add(Arrays.asList("1486569600000", "3164.688", "3186.838", "3162.574", "3183.179", "1.91640327E8", "2.0972315864E11", "3160.0806", "3151.1591", "3139.70695", "3134.7726666667", "3168.2513666667", "3166.982"));
        dataList.add(Arrays.asList("1486656000000", "3183.007", "3205.049", "3182.802", "3196.699", "2.39348913E8", "2.45772163919E11", "3171.3864", "3158.5151", "3141.82585", "3136.6773666667", "3168.6749833333", "3183.179"));
        dataList.add(Arrays.asList("1486915200000", "3198.995", "3219.407", "3198.995", "3216.839", "2.20191508E8", "2.36158766819E11", "3183.3574", "3166.5215", "3144.106", "3140.2335333333", "3169.0215666667", "3196.699"));
        dataList.add(Arrays.asList("1487001600000", "3216.137", "3219.405", "3205.285", "3217.928", "1.88798088E8", "2.04253821854E11", "3196.3254", "3174.059", "3146.91885", "3143.4121666667", "3169.1475166667", "3216.839"));
        dataList.add(Arrays.asList("1487088000000", "3215.464", "3235.998", "3206.561", "3212.986", "2.41507706E8", "2.5796524446E11", "3205.5262", "3180.4021", "3150.7305", "3146.6895666667", "3169.2475166667", "3217.928"));
        dataList.add(Arrays.asList("1487174400000", "3210.357", "3230.275", "3207.785", "3229.618", "2.16955942E8", "2.26587611994E11", "3214.814", "3187.4473", "3156.24695", "3150.9356333333", "3169.6568666667", "3212.986"));
        dataList.add(Arrays.asList("1487260800000", "3227.707", "3238.396", "3199.425", "3202.076", "2.26228177E8", "2.48572751592E11", "3215.8894", "3193.6379", "3160.71255", "3154.4682666667", "3169.5506", "3229.618"));
        dataList.add(Arrays.asList("1487520000000", "3198.963", "3241.458", "3198.963", "3239.961", "2.32152096E8", "2.50276131814E11", "3220.5138", "3201.9356", "3167.5392", "3159.0124", "3170.3356833333", "3202.076"));
        dataList.add(Arrays.asList("1487606400000", "3242.223", "3254.335", "3239.877", "3253.326", "2.11673066E8", "2.33551176049E11", "3227.5934", "3211.9594", "3174.76675", "3162.9259", "3170.9219833333", "3239.961"));
        dataList.add(Arrays.asList("1487692800000", "3252.69", "3261.381", "3243.84", "3261.219", "2.07453776E8", "2.34815325823E11", "3237.24", "3221.3831", "3182.1771", "3166.3400666667", "3171.1364333333", "3253.326"));
        dataList.add(Arrays.asList("1487779200000", "3258.832", "3264.082", "3236.355", "3251.375", "2.091798E8", "2.36822582276E11", "3241.5914", "3228.2027", "3189.6809", "3169.2055333333", "3171.3070666667", "3261.219"));
        dataList.add(Arrays.asList("1487865600000", "3246.86", "3253.956", "3233.535", "3253.433", "1.86406362E8", "2.13839036866E11", "3251.8628", "3233.8761", "3196.1956", "3172.5092666667", "3171.5020166667", "3251.375"));
        dataList.add(Arrays.asList("1488124800000", "3249.195", "3251.654", "3224.088", "3228.66", "1.82581071E8", "2.11395715856E11", "3249.6026", "3235.0582", "3200.78985", "3174.4234", "3170.9473833333", "3253.433"));
        dataList.add(Arrays.asList("1488211200000", "3225.969", "3242.679", "3225.969", "3241.733", "1.51244318E8", "1.8601928881E11", "3247.284", "3237.4387", "3205.74885", "3177.0921333333", "3170.3596", "3228.66"));
        dataList.add(Arrays.asList("1488297600000", "3240.073", "3259.978", "3237.871", "3246.934", "1.9067755E8", "2.25940967941E11", "3244.427", "3240.8335", "3210.6178", "3180.7648333333", "3169.7597666667", "3241.733"));
        dataList.add(Arrays.asList("1488384000000", "3250.518", "3256.807", "3228.665", "3230.028", "1.81215076E8", "2.23043958766E11", "3240.1576", "3240.8745", "3214.1609", "3184.4561333333", "3169.4263333333", "3246.934"));
        dataList.add(Arrays.asList("1488470400000", "3219.202", "3221.155", "3206.613", "3218.312", "1.57082368E8", "1.92707923011E11", "3233.1334", "3242.4981", "3218.068", "3187.9744", "3168.5097166667", "3230.028"));
        dataList.add(Arrays.asList("1488729600000", "3217.334", "3234.663", "3215.067", "3233.866", "1.56092158E8", "1.93769172395E11", "3234.1746", "3241.8886", "3221.9121", "3192.3223333333", "3168.3434333333", "3218.312"));
        dataList.add(Arrays.asList("1488816000000", "3233.094", "3242.659", "3226.822", "3242.406", "1.64064235E8", "2.09931208364E11", "3234.3092", "3240.7966", "3226.378", "3196.7767", "3168.9717166667", "3233.866"));
        dataList.add(Arrays.asList("1488902400000", "3240.532", "3245.304", "3230.608", "3240.665", "1.60731388E8", "1.98225782571E11", "3233.0554", "3238.7412", "3230.06215", "3201.0318", "3169.65535", "3242.406"));
        dataList.add(Arrays.asList("1488988800000", "3233.701", "3233.875", "3205.279", "3216.746", "1.67371108E8", "1.99217646811E11", "3230.399", "3235.2783", "3231.7405", "3204.8800333333", "3169.5637666667", "3240.665"));
        dataList.add(Arrays.asList("1489075200000", "3213.729", "3222.319", "3208.447", "3212.76", "1.36672743E8", "1.73663170234E11", "3229.2886", "3231.211", "3232.54355", "3207.8674", "3169.5203333333", "3216.746"));
        dataList.add(Arrays.asList("1489334400000", "3209.449", "3237.121", "3193.156", "3237.024", "1.6367376E8", "2.06919741904E11", "3229.9202", "3232.0474", "3233.5528", "3211.2090333333", "3169.58935", "3212.76"));
        dataList.add(Arrays.asList("1489420800000", "3235.251", "3246.329", "3231.521", "3239.328", "1.46946022E8", "1.9153512669E11", "3229.3046", "3231.8069", "3234.6228", "3214.4348666667", "3171.02865", "3237.024"));
        dataList.add(Arrays.asList("1489507200000", "3235.403", "3243.713", "3227.738", "3241.76", "1.44055682E8", "1.85727250363E11", "3229.5236", "3231.2895", "3236.0615", "3217.5083666667", "3172.4740333333", "3239.328"));
        dataList.add(Arrays.asList("1489593600000", "3247.163", "3269.77", "3247.163", "3268.935", "1.89416002E8", "2.44508344465E11", "3239.9614", "3235.1802", "3238.02735", "3221.1673333333", "3174.6141", "3241.76"));
        dataList.add(Arrays.asList("1489680000000", "3271.866", "3274.19", "3232.281", "3237.447", "2.00583223E8", "2.62184046475E11", "3244.8988", "3237.0937", "3239.7959", "3224.4099", "3176.6102666667", "3268.935"));
        dataList.add(Arrays.asList("1489939200000", "3241.11", "3251.127", "3228.118", "3250.808", "1.7054843E8", "2.13951759501E11", "3247.6556", "3238.7879", "3240.33825", "3227.5373666667", "3178.7407166667", "3237.447"));
        dataList.add(Arrays.asList("1490025600000", "3250.247", "3262.221", "3246.696", "3261.611", "1.62719306E8", "2.19121269781E11", "3252.1122", "3240.7084", "3240.7525", "3231.1548", "3181.1328166667", "3250.808"));
        dataList.add(Arrays.asList("1490112000000", "3246.223", "3255.778", "3229.128", "3245.22", "1.89731649E8", "2.4454545904E11", "3252.8042", "3241.1639", "3239.95255", "3233.7627333333", "3183.5052166667", "3261.611"));
        dataList.add(Arrays.asList("1490198400000", "3245.808", "3262.091", "3221.934", "3248.55", "1.93029144E8", "2.58246691E11", "3248.7272", "3244.3443", "3239.8113", "3235.9417666667", "3185.3572166667", "3245.22"));
        dataList.add(Arrays.asList("1490284800000", "3247.35", "3275.207", "3241.123", "3269.445", "2.19777914E8", "2.67094164315E11", "3255.1268", "3250.0128", "3240.6119", "3238.3666333333", "3187.522", "3248.55"));
        dataList.add(Arrays.asList("1490544000000", "3268.924", "3283.239", "3262.118", "3266.955", "2.01852675E8", "2.49185540341E11", "3258.3562", "3253.0059", "3242.52665", "3240.0371666667", "3190.13535", "3269.445"));
        dataList.add(Arrays.asList("1490630400000", "3265.634", "3265.634", "3246.086", "3252.948", "1.61710013E8", "2.03452481772E11", "3256.6236", "3254.3679", "3243.0874", "3241.2045", "3192.3083333333", "3266.955"));
        dataList.add(Arrays.asList("1490716800000", "3252.865", "3262.098", "3233.28", "3241.314", "2.16105575E8", "2.4537185262E11", "3255.8424", "3254.3233", "3242.8064", "3242.1487666667", "3194.4191666667", "3252.948"));
        dataList.add(Arrays.asList("1490803200000", "3235.137", "3240.017", "3195.852", "3210.237", "2.47135479E8", "2.65694871643E11", "3248.1798", "3248.4535", "3241.81685", "3241.5027333333", "3196.2191833333", "3241.314"));
        dataList.add(Arrays.asList("1490889600000", "3206.253", "3226.248", "3205.537", "3222.514", "1.96442922E8", "2.14036159962E11", "3238.7936", "3246.9602", "3242.02695", "3242.184", "3198.3261333333", "3210.237"));
        dataList.add(Arrays.asList("1491321600000", "3235.66", "3270.645", "3233.237", "3270.305", "2.48320202E8", "2.73201155674E11", "3239.4636", "3248.9099", "3243.8489", "3243.1954666667", "3201.1039333333", "3222.514"));
        dataList.add(Arrays.asList("1491408000000", "3272.193", "3286.674", "3265.765", "3281.005", "2.45287999E8", "2.62335587585E11", "3245.075", "3250.8493", "3245.77885", "3244.1181", "3203.522", "3270.305"));
        dataList.add(Arrays.asList("1491494400000", "3280.624", "3295.187", "3275.051", "3286.616", "2.36108942E8", "2.66738322876E11", "3254.1354", "3254.9889", "3248.0764", "3244.9646666667", "3205.6523666667", "3281.005"));
        dataList.add(Arrays.asList("1491753600000", "3285.459", "3285.459", "3265.012", "3269.393", "2.32694616E8", "2.79415195386E11", "3265.9666", "3257.0732", "3250.70875", "3245.5652666667", "3207.3854", "3286.616"));
        dataList.add(Arrays.asList("1491840000000", "3266.222", "3290.389", "3244.405", "3288.966", "2.81281248E8", "3.266224069E11", "3279.257", "3259.0253", "3254.51905", "3246.7497", "3209.6294833333", "3269.393"));
        dataList.add(Arrays.asList("1491926400000", "3283.837", "3284.934", "3262.277", "3273.83", "2.6938179E8", "3.11505523795E11", "3279.962", "3259.7128", "3256.35935", "3248.2553666667", "3211.3393833333", "3288.966"));
        dataList.add(Arrays.asList("1492012800000", "3265.218", "3281.137", "3261.49", "3275.96", "2.07346875E8", "2.2328721471E11", "3278.953", "3262.014", "3258.19095", "3249.3962666667", "3213.2442", "3273.83"));
        dataList.add(Arrays.asList("1492099200000", "3276.138", "3276.711", "3238.896", "3246.067", "2.14508558E8", "2.24106679468E11", "3270.8432", "3262.4893", "3258.4063", "3249.3673666667", "3215.0661", "3275.96"));
        dataList.add(Arrays.asList("1492358400000", "3229.949", "3229.949", "3199.912", "3222.167", "2.12737189E8", "2.27214681389E11", "3261.398", "3263.6823", "3256.0679", "3249.1053333333", "3216.7807333333", "3246.067"));
        dataList.add(Arrays.asList("1492444800000", "3215.396", "3225.055", "3196.488", "3196.713", "1.88661353E8", "2.11866767668E11", "3242.9474", "3261.1022", "3254.0312", "3248.3853666667", "3218.1798833333", "3222.167"));
        dataList.add(Arrays.asList("1492531200000", "3184.666", "3189.437", "3147.066", "3170.687", "2.13238075E8", "2.25806056521E11", "3222.3188", "3251.1404", "3250.02515", "3246.2794", "3219.3008666667", "3196.713"));
        dataList.add(Arrays.asList("1492617600000", "3165.665", "3178.183", "3148.184", "3172.1", "1.90873985E8", "2.20098232749E11", "3201.5468", "3240.2499", "3245.5496", "3243.9358666667", "3220.3562833333", "3170.687"));
        dataList.add(Arrays.asList("1492704000000", "3170.29", "3180.794", "3158.629", "3173.151", "1.6476174E8", "1.841314407E11", "3186.9636", "3228.9034", "3241.94615", "3241.6854", "3221.3586", "3172.1"));
        dataList.add(Arrays.asList("1492963200000", "3164.248", "3164.248", "3111.215", "3129.531", "1.86277434E8", "1.97845253573E11", "3168.4364", "3214.9172", "3235.9952", "3238.7782333333", "3221.8291333333", "3173.151"));
        dataList.add(Arrays.asList("1493049600000", "3123.894", "3145.267", "3117.449", "3134.567", "1.53418307E8", "1.74129249619E11", "3156.0072", "3199.4773", "3229.2513", "3236.1718", "3222.0196", "3129.531"));
        dataList.add(Arrays.asList("1493136000000", "3132.918", "3152.953", "3131.418", "3140.847", "1.69878107E8", "1.97112873017E11", "3150.0392", "3186.179", "3222.9459", "3232.9659", "3222.0874666667", "3134.567"));
        dataList.add(Arrays.asList("1493222400000", "3131.35", "3155.003", "3097.333", "3152.187", "2.11793073E8", "2.35748319355E11", "3146.0566", "3173.8017", "3217.90785", "3230.0612", "3222.2480333333", "3140.847"));
        dataList.add(Arrays.asList("1493308800000", "3144.022", "3154.727", "3136.578", "3154.658", "1.62889899E8", "1.83195769806E11", "3142.358", "3164.6608", "3213.57505", "3227.1578", "3222.3330833333", "3152.187"));
        dataList.add(Arrays.asList("1493654400000", "3147.228", "3154.781", "3136.539", "3143.712", "1.54222962E8", "1.76389916688E11", "3145.1942", "3156.8153", "3210.2488", "3222.9837", "3222.0755166667", "3154.658"));
        dataList.add(Arrays.asList("1493740800000", "3138.307", "3148.286", "3123.751", "3135.346", "1.63763924E8", "1.9023660069E11", "3145.35", "3150.6786", "3205.8904", "3219.5803333333", "3221.9951166667", "3143.712"));
        dataList.add(Arrays.asList("1493827200000", "3127.106", "3143.818", "3111.391", "3127.369", "1.77967485E8", "2.00170851568E11", "3142.6544", "3146.3468", "3198.7436", "3215.4657", "3221.5015333333", "3135.346"));
        dataList.add(Arrays.asList("1493913600000", "3114.774", "3117.614", "3092.092", "3103.038", "1.76213641E8", "2.0027390817E11", "3132.8246", "3139.4406", "3189.84525", "3210.1799333333", "3220.6673666667", "3127.369"));
        dataList.add(Arrays.asList("1494172800000", "3090.065", "3093.452", "3067.694", "3078.613", "1.80526904E8", "1.98171689423E11", "3117.6156", "3129.9868", "3179.4451", "3204.6263666667", "3219.19455", "3103.038"));
        dataList.add(Arrays.asList("1494259200000", "3064.848", "3084.208", "3056.558", "3080.527", "1.35066674E8", "1.52795901608E11", "3104.9786", "3125.0864", "3170.0018", "3199.0256", "3217.4836833333", "3078.613"));
        dataList.add(Arrays.asList("1494345600000", "3078.173", "3090.819", "3051.59", "3052.785", "1.60794452E8", "1.81624151692E11", "3088.4664", "3116.9082", "3158.19275", "3191.8036", "3215.0851166667", "3080.527"));
        dataList.add(Arrays.asList("1494432000000", "3036.789", "3063.564", "3016.531", "3061.5", "1.91341901E8", "1.97604070142E11", "3075.2926", "3108.9735", "3147.57625", "3184.9551", "3212.4961333333", "3052.785"));
        dataList.add(Arrays.asList("1494518400000", "3054.112", "3090.491", "3051.871", "3083.513", "1.59684099E8", "1.75107245752E11", "3071.3876", "3102.1061", "3137.9539", "3179.3072666667", "3210.2558833333", "3061.5"));
        dataList.add(Arrays.asList("1494777600000", "3085.932", "3098.913", "3085.932", "3090.229", "1.35660728E8", "1.58269607703E11", "3073.7108", "3095.6632", "3130.162", "3174.2711", "3208.2099333333", "3083.513"));
        dataList.add(Arrays.asList("1494864000000", "3082.872", "3113.512", "3060.534", "3112.964", "1.73775564E8", "1.93292104557E11", "3080.1982", "3092.5884", "3124.70185", "3171.0286666667", "3206.2657", "3090.229"));
        dataList.add(Arrays.asList("1494950400000", "3107.802", "3119.582", "3101.295", "3104.441", "1.68674158E8", "1.945605941E11", "3090.5294", "3089.4979", "3120.08825", "3167.0929", "3204.63845", "3112.964"));
        dataList.add(Arrays.asList("1495036800000", "3082.329", "3103.441", "3077.963", "3090.139", "1.48620035E8", "1.68149514123E11", "3096.2572", "3085.7749", "3116.06085", "3161.0873666667", "3202.1414166667", "3104.442"));
        dataList.add(Arrays.asList("1495123200000", "3086.705", "3095.483", "3081.278", "3090.631", "1.2963523E8", "1.50164226218E11", "3097.6808", "3084.5342", "3111.9874", "3154.7415666667", "3199.4298333333", "3090.139"));
        dataList.add(Arrays.asList("1495382400000", "3087.171", "3103.938", "3063.153", "3075.676", "1.5368373E8", "1.7034098265E11", "3094.7702", "3084.2405", "3107.11365", "3147.7102333333", "3196.33745", "3090.631"));
        dataList.add(Arrays.asList("1495468800000", "3069.394", "3084.235", "3050.842", "3061.947", "1.78310599E8", "1.95073653676E11", "3084.5668", "3082.3825", "3103.73445", "3140.7953666667", "3193.1803166667", "3075.676"));
        dataList.add(Arrays.asList("1495555200000", "3047.568", "3064.813", "3022.303", "3064.076", "1.39813353E8", "1.52669201599E11", "3076.4938", "3083.5116", "3100.2099", "3133.2990333333", "3190.0243666667", "3061.947"));
        dataList.add(Arrays.asList("1495641600000", "3055.343", "3114.658", "3052.831", "3107.831", "1.91391841E8", "2.01910125033E11", "3080.0322", "3088.1447", "3098.5591", "3127.7657333333", "3188.01055", "3064.076"));
        dataList.add(Arrays.asList("1495728000000", "3101.286", "3120.663", "3100.388", "3110.059", "1.55923486E8", "1.71327384644E11", "3083.9178", "3090.7993", "3096.4527", "3122.2357", "3185.8159833333", "3107.831"));
        dataList.add(Arrays.asList("1496160000000", "3125.325", "3143.279", "3111.556", "3117.178", "1.5295043E8", "1.77913309936E11", "3092.2182", "3093.4942", "3094.5787", "3117.9394", "3183.6533833333", "3110.059"));
        dataList.add(Arrays.asList("1496246400000", "3108.421", "3113.521", "3097.679", "3102.623", "1.63015719E8", "1.80782624777E11", "3100.3534", "3092.4601", "3092.52425", "3113.9546", "3181.5299666667", "3117.178"));
        dataList.add(Arrays.asList("1496332800000", "3094.225", "3110.389", "3081.849", "3105.54", "1.42504925E8", "1.54454347995E11", "3108.6462", "3092.57", "3091.03395", "3110.9155", "3179.6504333333", "3102.623"));
        dataList.add(Arrays.asList("1496592000000", "3102.11", "3105.506", "3084.83", "3091.656", "1.325703E8", "1.43429802002E11", "3105.4112", "3092.7217", "3089.2483", "3108.2811333333", "3177.2802666667", "3105.54"));
        dataList.add(Arrays.asList("1496678400000", "3084.54", "3102.863", "3078.787", "3102.126", "1.13439963E8", "1.27804624246E11", "3103.8246", "3093.8712", "3089.2027", "3105.9486666667", "3174.9422666667", "3091.656"));
        dataList.add(Arrays.asList("1496764800000", "3101.761", "3140.774", "3098.951", "3140.325", "1.73229014E8", "1.94287945514E11", "3108.454", "3100.3361", "3092.2883", "3104.8544666667", "3173.2699333333", "3102.126"));
        dataList.add(Arrays.asList("1496851200000", "3136.471", "3153.264", "3132.828", "3150.334", "1.52277659E8", "1.78894298541E11", "3117.9962", "3109.1748", "3095.77865", "3105.5479", "3172.1630666667", "3140.325"));
        dataList.add(Arrays.asList("1496937600000", "3147.453", "3165.92", "3146.108", "3158.4", "1.60136334E8", "1.8158488479E11", "3128.5682", "3118.6072", "3101.0594", "3106.3423333333", "3171.2570666667", "3150.334"));
        dataList.add(Arrays.asList("1497196800000", "3149.527", "3164.95", "3135.314", "3139.877", "1.46729425E8", "1.70223276943E11", "3138.2124", "3121.8118", "3104.97825", "3106.31", "3169.63795", "3158.4"));
        dataList.add(Arrays.asList("1497283200000", "3134.009", "3155.99", "3131.043", "3153.743", "1.28318279E8", "1.48457870014E11", "3148.5358", "3126.1802", "3108.48975", "3106.3618666667", "3168.2115333333", "3139.877"));
        dataList.add(Arrays.asList("1497369600000", "3146.75", "3149.17", "3125.35", "3130.67", "1.38318279E8", "1.48457870014E11", "3146.6048", "3127.5294", "3110.5118", "3105.5622666667", "3166.3600333333", "3153.74"));
        dataList.add(Arrays.asList("1497456000000", "3125.59", "3137.59", "3117.08", "3132.49", "1.47318279E8", "1.71157870014E11", "3143.036", "3130.5161", "3111.4881", "3105.1882", "3164.08595", "3130.67"));
        dataList.add(Arrays.asList("1497542400000", "3126.373", "3134.251", "3117.857", "3123.166", "1.29652758E8", "1.49215477636E11", "3135.9892", "3132.2787", "3112.42435", "3104.7822", "3162.1812666667", "3132.486"));
        dataList.add(Arrays.asList("1497801600000", "3122.157", "3146.77", "3121.778", "3144.373", "1.3489176E8", "1.51741792256E11", "3136.8884", "3137.5504", "3115.13605", "3105.349", "3160.40735", "3123.166"));
        dataList.add(Arrays.asList("1497888000000", "3148.018", "3150.464", "3134.61", "3140.013", "1.41191808E8", "1.62498019328E11", "3134.1424", "3141.3391", "3117.60515", "3106.5815", "3158.3807166667", "3144.373"));
        dataList.add(Arrays.asList("1497974400000", "3148.986", "3157.027", "3132.617", "3156.211", "1.36688272E8", "1.67048216576E11", "3139.2506", "3142.9277", "3121.6319", "3109.1681", "3156.8972333333", "3140.013"));
        dataList.add(Arrays.asList("1498060800000", "3152.2424", "3186.9823", "3146.6432", "3147.1249", "1.91344006E8", "2.204384395119E11", "3142.17758", "3142.60679", "3125.890795", "3111.38803", "3155.206815", "3156.2118"));
        dataList.add(Arrays.asList("1498147200000", "3138.444", "3158.047", "3118.094", "3157.873", "1.54843648E8", "1.75568519168E11", "3149.11898", "3142.55409", "3130.580645", "3114.8909633333", "3153.3472816667", "3147.453"));
        dataList.add(Arrays.asList("1498406400000", "3157.002", "3187.889", "3156.976", "3185.443", "1.73579216E8", "2.00419295232E11", "3157.33298", "3147.11069", "3134.461245", "3119.0223966667", "3151.9887483333", "3157.873"));
        dataList.add(Arrays.asList("1498492800000", "3183.419", "3193.461", "3172.462", "3191.197", "1.482012E8", "1.67523794944E11", "3167.56978", "3150.85609", "3138.518145", "3122.6118633333", "3150.959565", "3185.443"));
        dataList.add(Arrays.asList("1498579200000", "3183.633", "3193.439", "3170.785", "3173.201", "1.4651672E8", "1.6208109568E11", "3170.96778", "3155.10919", "3141.319295", "3125.3775966667", "3149.8243483333", "3191.197"));
        dataList.add(Arrays.asList("1498665600000", "3174.981", "3188.774", "3174.283", "3188.062", "1.28755472E8", "1.47249790976E11", "3179.1552", "3160.66639", "3145.591245", "3127.8808633333", "3149.454765", "3173.201"));
        dataList.add(Arrays.asList("1498752000000", "3176.948", "3193.241", "3171.57", "3192.427", "1.21464544E8", "1.43195602944E11", "3186.066", "3167.59249", "3149.935595", "3130.81373", "3148.953315", "3188.062"));
        dataList.add(Arrays.asList("1499011200000", "3191.998", "3196.286", "3177.024", "3195.911", "1.40305712E8", "1.56575776768E11", "3188.1596", "3172.74629", "3155.148345", "3134.3394633333", "3147.713415", "3192.427"));
        dataList.add(Arrays.asList("1499097600000", "3192.888", "3193.064", "3174.314", "3182.804", "1.4111496E8", "1.61454555136E11", "3186.481", "3177.02539", "3159.182245", "3137.4118966667", "3146.0767316667", "3195.911"));
        dataList.add(Arrays.asList("1499184000000", "3179.216", "3207.309", "3174.708", "3207.134", "1.4829648E8", "1.73554008064E11", "3193.2676", "3182.11769", "3162.522695", "3141.79383", "3144.7520316667", "3182.804"));
        dataList.add(Arrays.asList("1499270400000", "3203.863", "3215.95", "3188.773", "3212.444", "1.7580928E8", "2.02209624064E11", "3198.144", "3188.6496", "3165.628195", "3146.8103966667", "3143.8028816667", "3207.134"));
        dataList.add(Arrays.asList("1499356800000", "3203.822", "3219.523", "3195.29", "3217.956", "1.76715424E8", "2.01938321408E11", "3203.2498", "3194.6579", "3168.605995", "3151.93973", "3142.6193816667", "3212.444"));
        dataList.add(Arrays.asList("1499616000000", "3208.462", "3223.34", "3203.209", "3212.631", "1.98928016E8", "2.22828314624E11", "3206.5938", "3197.3767", "3172.243695", "3155.4330633333", "3141.5993983333", "3217.956"));
        dataList.add(Arrays.asList("1499702400000", "3201.516", "3226.908", "3199.223", "3203.037", "1.87837264E8", "2.0549337088E11", "3210.6404", "3198.5607", "3174.708395", "3158.53233", "3140.384015", "3212.631"));
        dataList.add(Arrays.asList("1499788800000", "3201.929", "3215.196", "3177.934", "3197.543", "1.8690088E8", "2.01799614464E11", "3208.7222", "3200.9949", "3178.052045", "3161.2111633333", "3139.5752816667", "3203.037"));
        dataList.add(Arrays.asList("1499875200000", "3192.3615", "3219.2683", "3190.3393", "3219.0373", "1.95303611E8", "2.10820896339E11", "3210.04086", "3204.09243", "3182.37941", "3165.09164", "3139.52312", "3197.5439"));
        dataList.add(Arrays.asList("1499961600000", "3212.0316", "3222.9784", "3204.8529", "3222.3105", "1.60126773E8", "1.743121381912E11", "3210.91176", "3207.08078", "3187.336635", "3168.98399", "3139.949745", "3218.1632"));
        dataList.add(Arrays.asList("1500220800000", "3219.7914", "3230.354", "3139.5035", "3176.4981", "2.66205274E8", "2.746532500979E11", "3203.68518", "3205.13949", "3188.94289", "3171.81206", "3140.0465966667", "3222.4168"));
        dataList.add(Arrays.asList("1500307200000", "3159.7318", "3187.671", "3150.1284", "3186.9316", "1.9062306E8", "1.964523615189E11", "3200.4641", "3205.55225", "3191.28882", "3174.6389133333", "3140.29379", "3176.4648"));
        dataList.add(Arrays.asList("1500393600000", "3181.4015", "3232.9408", "3179.73", "3232.8667", "2.72420726E8", "2.697710195871E11", "3207.52884", "3208.12552", "3195.121605", "3177.7236366667", "3141.2890516667", "3187.5672"));
        dataList.add(Arrays.asList("1500480000000", "3227.5056", "3246.236", "3225.4328", "3245.3339", "2.32108392E8", "2.458254219334E11", "3212.78816", "3211.41451", "3200.032055", "3180.8903", "3143.2191", "3230.9762"));
        dataList.add(Arrays.asList("1500566400000", "3236.5881", "3247.7122", "3231.9556", "3238.1581", "2.06003251E8", "2.219569099095E11", "3215.95768", "3213.43472", "3204.04631", "3183.5489033333", "3144.9456183333", "3244.8647"));
        dataList.add(Arrays.asList("1500825600000", "3230.898", "3261.1046", "3230.0705", "3250.4916", "2.33056399E8", "2.506311942632E11", "3230.75638", "3217.22078", "3207.29874", "3187.2360566667", "3146.7730283333", "3237.9817"));
        dataList.add(Arrays.asList("1500912000000", "3249.1376", "3261.6454", "3233.1376", "3243.7657", "2.05573873E8", "2.134054989751E11", "3242.1232", "3221.29365", "3209.927175", "3190.2368133333", "3148.29934", "3250.5989"));
        dataList.add(Arrays.asList("1500998400000", "3244.4608", "3264.8483", "3228.0389", "3247.5834", "2.13542005E8", "2.284605185169E11", "3245.06654", "3226.29769", "3213.646295", "3194.1339266667", "3149.8480966667", "3243.6894"));
        dataList.add(Arrays.asList("1501084800000", "3243.765", "3251.9261", "3220.6366", "3249.2927", "2.28485945E8", "2.425720814598E11", "3245.8583", "3229.32323", "3216.70783", "3198.02735", "3151.607775", "3247.6748"));
        dataList.add(Arrays.asList("1501171200000", "3240.1728", "3256.3706", "3232.9634", "3254.1252", "1.8222688E8", "1.984285840911E11", "3249.05172", "3232.5047", "3219.79274", "3202.3926566667", "3153.5874283333", "3249.7814"));
        dataList.add(Arrays.asList("1501430400000", "3252.7519", "3276.9461", "3251.1941", "3274.1328", "2.4603944E8", "2.535259208023E11", "3253.77996", "3242.26817", "3223.70383", "3206.7179833333", "3156.0334916667", "3253.2404"));
        dataList.add(Arrays.asList("1501516800000", "3274.3685", "3292.5009", "3273.5038", "3292.2847", "2.37194594E8", "2.564199422057E11", "3263.48376", "3252.80348", "3229.177865", "3211.7937066667", "3159.1876033333", "3273.0283"));
        dataList.add(Arrays.asList("1501603200000", "3288.5183", "3305.4313", "3282.0377", "3286.0154", "2.66730628E8", "2.793523417938E11", "3271.17016", "3258.11835", "3233.121935", "3216.12052", "3162.64431", "3292.6383"));
        dataList.add(Arrays.asList("1501689600000", "3279.9864", "3293.3686", "3262.1554", "3273.4088", "2.33277004E8", "2.435495427149E11", "3275.99338", "3260.92584", "3236.170175", "3220.3299833333", "3165.8590066667", "3285.0568"));
        dataList.add(Arrays.asList("1501776000000", "3269.3182", "3287.1929", "3261.3066", "3261.317", "2.7590634E8", "2.856347603814E11", "3277.43174", "3263.24173", "3238.338225", "3223.7781166667", "3169.33454", "3272.9286"));
        dataList.add(Arrays.asList("1502035200000", "3257.67", "3280.1035", "3243.7153", "3279.5441", "2.31173379E8", "2.31874371766E11", "3278.514", "3266.14698", "3241.68388", "3226.91482", "3172.9686083333", "3262.0809"));
        dataList.add(Arrays.asList("1502121600000", "3277.1887", "3285.4833", "3269.6582", "3282.9466", "2.52043537E8", "2.557701844564E11", "3276.64638", "3270.06507", "3245.67936", "3229.97314", "3176.2925016667", "3279.4566"));
        dataList.add(Arrays.asList("1502208000000", "3277.8083", "3277.9433", "3263.8468", "3276.6818", "2.35597907E8", "2.37815125492E11", "3274.77966", "3272.97491", "3249.6363", "3233.4225", "3179.4000483333", "3281.8728"));
        dataList.add(Arrays.asList("1502294400000", "3269.7347", "3282.519", "3236.1829", "3261.8019", "2.40684622E8", "2.518540396919E11", "3272.45828", "3274.22583", "3251.77453", "3235.8804966667", "3181.88068", "3275.573"));
        dataList.add(Arrays.asList("1502380800000", "3237.9222", "3245.1163", "3200.7481", "3209.8047", "2.62962995E8", "2.669982623804E11", "3262.15582", "3269.79378", "3251.14924", "3236.4597533333", "3183.6367416667", "3261.7494"));
        dataList.add(Arrays.asList("1502640000000", "3206.0436", "3240.0517", "3206.0436", "3236.93", "1.90346796E8", "2.090960787479E11", "3253.633", "3266.0735", "3254.170835", "3237.8270533333", "3186.0832583333", "3208.5413"));
        dataList.add(Arrays.asList("1502726400000", "3235.2298", "3263.5892", "3235.1013", "3251.6402", "1.82297997E8", "2.045958586157E11", "3247.37172", "3262.00905", "3257.406265", "3240.1215933333", "3188.766745", "3237.3602"));
        dataList.add(Arrays.asList("1502812800000", "3247.8525", "3248.785", "3228.8705", "3246.6245", "1.76852051E8", "2.008325337035E11", "3241.36026", "3258.06996", "3258.094155", "3241.4379433333", "3191.6158866667", "3251.2617"));
        dataList.add(Arrays.asList("1502899200000", "3253.8455", "3269.1389", "3251.4593", "3268.6182", "2.03622551E8", "2.2837694421E11", "3242.72352", "3257.5909", "3259.25837", "3243.3104166667", "3195.0604066667", "3246.4512"));
        dataList.add(Arrays.asList("1502985600000", "3253.2434", "3275.0763", "3248.0833", "3269.2415", "1.91122483E8", "2.184377407453E11", "3254.61088", "3258.38335", "3260.81254", "3245.0199333333", "3198.4798316667", "3268.4298"));
        dataList.add(Arrays.asList("1503244800000", "3274.5805", "3287.5186", "3270.4753", "3287.322", "1.86122483E8", "2.097185928839E11", "3264.68928", "3259.16114", "3262.65406", "3247.5096333333", "3201.4713483333", "3268.7243"));
        dataList.add(Arrays.asList("1503331200000", "3287.6147", "3293.476", "3274.941", "3291.0254", "1.86537991E8", "2.229274914862E11", "3272.56632", "3259.96902", "3265.017045", "3250.44258", "3204.487455", "3286.9055"));
        dataList.add(Arrays.asList("1503417600000", "3283.7966", "3299.4572", "3274.4404", "3287.6796", "1.79832208E8", "2.04330421446E11", "3280.77734", "3261.0688", "3267.021855", "3253.4471333333", "3207.3291483333", "3290.2257"));
        dataList.add(Arrays.asList("1503504000000", "3287.9594", "3297.9886", "3266.3589", "3271.9925", "1.63468937E8", "1.862450565655E11", "3281.4522", "3262.08786", "3268.156845", "3255.2123066667", "3210.1519733333", "3287.7049"));
        dataList.add(Arrays.asList("1503590400000", "3271.4608", "3331.9146", "3271.4608", "3331.6641", "2.05839482E8", "2.271410293776E11", "3293.93672", "3274.2738", "3272.03379", "3258.8574266667", "3213.9207083333", "3271.5117"));
        dataList.add(Arrays.asList("1503849600000", "3336.1264", "3375.0339", "3336.1264", "3362.8151", "2.57461438E8", "3.053198830948E11", "3309.03534", "3286.86231", "3276.467905", "3265.0679933333", "3218.4400266667", "3331.5221"));
        dataList.add(Arrays.asList("1503936000000", "3362.0604", "3374.5947", "3354.4627", "3365.6279", "2.19504535E8", "2.689160948468E11", "3323.95584", "3298.26108", "3280.135065", "3271.0245366667", "3222.831725", "3362.6514"));
        dataList.add(Arrays.asList("1504022400000", "3361.8207", "3376.6481", "3357.0803", "3362.9947", "2.46863312E8", "2.796407966221E11", "3339.01886", "3309.8981", "3283.98403", "3275.3621366667", "3226.5428866667", "3365.2261"));
        dataList.add(Arrays.asList("1504108800000", "3361.4621", "3367.3581", "3340.6865", "3360.9959", "2.34419781E8", "2.691331077932E11", "3356.81954", "3319.13587", "3288.363385", "3279.2175366667", "3230.0539183333", "3363.6266"));
        dataList.add(Arrays.asList("1504195200000", "3365.9913", "3381.9252", "3358.4724", "3367.3004", "2.82497584E8", "3.13722092738E11", "3363.9468", "3328.94176", "3293.662555", "3283.52228", "3233.5355916667", "3360.8103"));
        dataList.add(Arrays.asList("1504454400000", "3369.7185", "3381.4027", "3359.1309", "3379.8364", "2.67427849E8", "2.963484322514E11", "3367.35106", "3338.1932", "3298.67717", "3287.8337733333", "3237.534915", "3367.1194"));
        dataList.add(Arrays.asList("1504540800000", "3377.1968", "3390.8233", "3371.5706", "3385.1293", "2.16552946E8", "2.455966082764E11", "3371.25134", "3347.60359", "3303.786305", "3292.5458933333", "3241.3913533333", "3379.583"));
        dataList.add(Arrays.asList("1504627200000", "3372.4277", "3391.0105", "3364.7645", "3385.8765", "2.29090785E8", "2.620346534911E11", "3375.8277", "3357.42328", "3309.24604", "3297.1556633333", "3245.644795", "3384.317"));
        dataList.add(Arrays.asList("1504713600000", "3383.6281", "3387.7956", "3363.1765", "3366.4324", "2.21118685E8", "2.64037023512E11", "3376.915", "3366.86727", "3314.477565", "3301.06032", "3249.543835", "3385.3888"));
        dataList.add(Arrays.asList("1504800000000", "3364.4275", "3380.8898", "3353.6876", "3365.4406", "1.98405184E8", "2.351602273555E11", "3376.54304", "3370.24492", "3322.25936", "3304.7708333333", "3253.581745", "3365.4974"));
        dataList.add(Arrays.asList("1505059200000", "3365.3506", "3384.81", "3360.0462", "3377.67", "2.19011019E8", "2.568888105474E11", "3376.10976", "3371.73041", "3329.29636", "3308.2220733333", "3257.4700283333", "3365.2426"));
        dataList.add(Arrays.asList("1505145600000", "3381.487", "3391.0694", "3370.8519", "3380.2892", "2.72910319E8", "3.250249519825E11", "3375.14174", "3373.19654", "3335.72881", "3311.1555566667", "3261.4746316667", "3376.4188"));
        dataList.add(Arrays.asList("1505232000000", "3374.7185", "3387.1397", "3366.5412", "3385.5375", "1.94550715E8", "2.307334088669E11", "3375.07394", "3375.45082", "3342.67446", "3314.47296", "3265.29674", "3379.488"));
        dataList.add(Arrays.asList("1505318400000", "3383.47", "3391.6435", "3361.3335", "3371.3534", "2.21306487E8", "2.567444212743E11", "3376.05814", "3376.48657", "3347.81122", "3317.73778", "3269.0338816667", "3384.147"));
        dataList.add(Arrays.asList("1505404800000", "3365.1454", "3365.5277", "3345.3283", "3353.6686", "2.19765816E8", "2.529106877982E11", "3373.70374", "3375.12339", "3352.032575", "3320.8161666667", "3272.2971416667", "3371.4256"));
        dataList.add(Arrays.asList("1505664000000", "3352.5134", "3371.7486", "3352.5134", "3363.2192", "1.90319676E8", "2.279386353954E11", "3370.81358", "3373.46167", "3355.827435", "3323.6053366667", "3275.2600783333", "3353.6192"));
        dataList.add(Arrays.asList("1505750400000", "3365.5315", "3370.4009", "3344.7054", "3356.6546", "1.91129691E8", "2.207287300187E11", "3366.08666", "3370.6142", "3359.108895", "3326.06227", "3278.017705", "3362.8587"));
        dataList.add(Arrays.asList("1505836800000", "3352.1848", "3370.0978", "3346.5356", "3366.3662", "1.92196661E8", "2.258390693028E11", "3362.2524", "3368.66317", "3363.043225", "3329.05175", "3281.237125", "3356.8446"));
        dataList.add(Arrays.asList("1505923200000", "3364.6977", "3377.8844", "3356.8754", "3358.1919", "1.97448695E8", "2.319461244308E11", "3359.6201", "3367.83912", "3367.353195", "3332.26475", "3284.0726233333", "3365.9959"));
        dataList.add(Arrays.asList("1506009600000", "3347.1569", "3356.4514", "3334.9846", "3352.8717", "1.79234007E8", "2.06583469029E11", "3359.46072", "3366.58223", "3368.413575", "3337.03365", "3286.7467016667", "3357.8123"));
        dataList.add(Arrays.asList("1506268800000", "3344.5886", "3350.9612", "3334.9438", "3340.8091", "1.69621293E8", "2.024016905474E11", "3354.9787", "3362.89614", "3367.313275", "3340.4962866667", "3289.16167", "3352.5294"));
        dataList.add(Arrays.asList("1506355200000", "3336.3497", "3347.1629", "3332.5985", "3343.8274", "1.32628595E8", "1.590396340383E11", "3352.41326", "3359.24996", "3366.22325", "3343.5691933333", "3291.8453933333", "3341.5487"));
        dataList.add(Arrays.asList("1506441600000", "3340.8219", "3349.6949", "3340.2989", "3345.4633", "1.43086945E8", "1.689813376886E11", "3348.23268", "3355.24254", "3365.34668", "3346.86382", "3294.1508816667", "3343.5826"));
        dataList.add(Arrays.asList("1506528000000", "3343.8446", "3344.6005", "3336.1784", "3340.1155", "1.494443E8", "1.819556274065E11", "3344.6174", "3352.11875", "3364.30266", "3349.2470633333", "3296.27874", "3345.2717"));
        dataList.add(Arrays.asList("1506614400000", "3340.3109", "3357.0154", "3340.3109", "3349.2164", "1.44862443E8", "1.758928229519E11", "3343.88634", "3351.67353", "3363.39846", "3351.9128933333", "3298.4664133333", "3339.6421"));
        dataList.add(Arrays.asList("1507478400000", "3403.2458", "3410.1704", "3366.965", "3374.8741", "1.91736057E8", "2.274405938745E11", "3350.69934", "3352.83902", "3363.150345", "3354.8312966667", "3301.170465", "3348.9431"));
        dataList.add(Arrays.asList("1507564800000", "3373.3446", "3384.0262", "3358.7953", "3383.5401", "1.79423841E8", "2.31148615058E11", "3358.64188", "3355.52757", "3363.070885", "3357.91512", "3304.17885", "3374.3781"));
        dataList.add(Arrays.asList("1507651200000", "3381.488", "3395.7794", "3379.1634", "3389.0492", "1.81476704E8", "2.311545059491E11", "3367.35906", "3357.79587", "3363.22952", "3361.2941066667", "3307.37062", "3382.9879"));
        dataList.add(Arrays.asList("1507737600000", "3385.5329", "3390.2036", "3372.5331", "3387.6463", "1.6180938E8", "2.016678640579E11", "3376.86522", "3360.74131", "3364.290215", "3365.1492333333", "3310.18077", "3388.2838"));
        dataList.add(Arrays.asList("1508169600000", "3373.2342", "3382.4072", "3365.5646", "3373.4368", "1.25381725E8", "1.629108281637E11", "3381.7093", "3362.79782", "3364.690025", "3366.5416566667", "3312.6995416667", "3378.4704"));
        dataList.add(Arrays.asList("1508256000000", "3373.5281", "3383.2323", "3371.9249", "3381.3691", "1.57228791E8", "1.942987666893E11", "3383.0083", "3366.85382", "3364.87498", "3367.1601233333", "3316.1140583333", "3372.0407"));
        dataList.add(Arrays.asList("1508342400000", "3374.6444", "3378.7359", "3359.6284", "3370.0953", "1.58476495E8", "1.879212923101E11", "3380.31934", "3369.48061", "3364.365285", "3367.3090366667", "3319.1667866667", "3381.7937"));
        dataList.add(Arrays.asList("1508428800000", "3363.5138", "3379.7652", "3360.1001", "3379.499", "1.27172851E8", "1.550650938619E11", "3378.4093", "3372.88418", "3364.06336", "3367.85918", "3321.6106583333", "3370.1721"));
        dataList.add(Arrays.asList("1508688000000", "3382.28", "3385.2853", "3374.705", "3382.2729", "1.30846289E8", "1.643694418212E11", "3377.33462", "3377.09992", "3364.609335", "3368.5684133333", "3323.892975", "3378.6481"));
        dataList.add(Arrays.asList("1508774400000", "3376.5989", "3388.6886", "3374.1246", "3388.6194", "1.39897511E8", "1.785528788563E11", "3380.37114", "3381.04022", "3366.356875", "3369.2790466667", "3326.4006633333", "3380.699"));
        dataList.add(Arrays.asList("1508860800000", "3384.8579", "3398.3041", "3382.0338", "3398.3041", "1.23131222E8", "1.600606966675E11", "3383.75814", "3383.38322", "3368.11112", "3369.8946366667", "3328.864205", "3388.2477"));
        dataList.add(Arrays.asList("1508947200000", "3397.519", "3414.2415", "3391.4549", "3408.2449", "1.83768625E8", "2.385961268218E11", "3391.38806", "3385.8537", "3370.690635", "3370.6651566667", "3331.605525", "3396.8975"));
        dataList.add(Arrays.asList("1509033600000", "3404.4978", "3421.1026", "3402.1141", "3416.4192", "1.70257173E8", "2.123771879762E11", "3398.7721", "3388.5907", "3373.193285", "3371.6832466667", "3334.419455", "3407.5671"));
        dataList.add(Arrays.asList("1509292800000", "3413.8679", "3419.7315", "3357.2762", "3390.5887", "2.08349286E8", "2.577766528538E11", "3400.43526", "3388.88494", "3374.813125", "3372.4884566667", "3336.7743883333", "3416.8124"));
        dataList.add(Arrays.asList("1509379200000", "3380.999", "3397.0988", "3376.1238", "3394.5033", "1.53498206E8", "1.999295844732E11", "3401.61204", "3390.99159", "3376.894705", "3373.4572133333", "3339.1140233333", "3390.3371"));
        dataList.add(Arrays.asList("1509465600000", "3393.9678", "3410.3519", "3388.5978", "3396.0737", "1.80566127E8", "2.319783781969E11", "3401.16596", "3392.46205", "3379.657935", "3374.07067", "3341.1463716667", "3393.3417"));
        dataList.add(Arrays.asList("1509552000000", "3391.652", "3391.652", "3372.2131", "3382.9136", "1.58056838E8", "1.987537326973E11", "3396.0997", "3393.74388", "3381.612245", "3374.15815", "3342.6568533333", "3395.9125"));
        dataList.add(Arrays.asList("1509638400000", "3377.7356", "3380.57", "3347.3603", "3371.2101", "1.7271479E8", "2.074379462434E11", "3387.05788", "3392.91499", "3382.899585", "3373.68057", "3344.076765", "3383.3095"));
        dataList.add(Arrays.asList("1509897600000", "3369.685", "3389.3826", "3356.5337", "3389.1171", "1.54636468E8", "2.04311225533E11", "3386.76356", "3393.59941", "3385.349665", "3374.2726933333", "3346.0052366667", "3371.7441"));
        dataList.add(Arrays.asList("1509984000000", "3389.4721", "3415.1482", "3387.9459", "3415.1419", "1.90571746E8", "2.425959494007E11", "3390.89128", "3396.25166", "3388.64594", "3376.3218033333", "3348.568985", "3388.1742"));
        dataList.add(Arrays.asList("1510070400000", "3409.1474", "3434.4918", "3404.8811", "3414.908", "1.8543647E8", "2.420255940775E11", "3394.65814", "3397.91205", "3390.647635", "3378.0447633333", "3350.82505", "3413.5748"));
        dataList.add(Arrays.asList("1510156800000", "3410.6723", "3428.7704", "3408.6186", "3428.4285", "1.58650426E8", "2.054336722509E11", "3403.76112", "3399.93041", "3392.892055", "3380.4372266667", "3353.2497483333", "3415.4602"));
        dataList.add(Arrays.asList("1510243200000", "3423.1846", "3438.7924", "3414.3286", "3433.3537", "1.89276503E8", "2.557721099986E11", "3416.18984", "3401.62386", "3395.10728", "3382.6701433333", "3355.8609466667", "3427.7946"));
        dataList.add(Arrays.asList("1510502400000", "3435.1839", "3449.1638", "3435.0849", "3448.6807", "2.05389054E8", "2.66738129757E11", "3428.10256", "3407.43306", "3398.159", "3385.6864366667", "3358.9755933333", "3432.6731"));
        dataList.add(Arrays.asList("1510588800000", "3446.5453", "3450.4949", "3419.6919", "3429.9692", "1.96472674E8", "2.674354233017E11", "3431.06802", "3410.97965", "3400.98562", "3388.2563533333", "3362.6450016667", "3447.8358"));
        dataList.add(Arrays.asList("1510675200000", "3416.2112", "3423.7495", "3396.381", "3402.5415", "1.68792076E8", "2.397341890588E11", "3428.59472", "3411.62643", "3402.04424", "3390.3141", "3365.4051933333", "3429.5482"));
        dataList.add(Arrays.asList("1510761600000", "3393.1937", "3409.6532", "3390.5888", "3399.8615", "1.56684332E8", "2.208624629545E11", "3422.88132", "3413.32122", "3403.53255", "3392.1819033333", "3367.8755483333", "3402.5245"));
        dataList.add(Arrays.asList("1510848000000", "3392.6834", "3403.2855", "3373.2956", "3382.3361", "2.49458153E8", "3.137503560718E11", "3412.6778", "3414.43382", "3403.674405", "3393.4109966667", "3370.1374083333", "3399.2503"));
        dataList.add(Arrays.asList("1511107200000", "3361.3563", "3393.1063", "3337.116", "3393.0225", "1.76524683E8", "2.283081367625E11", "3401.54616", "3414.82436", "3404.211885", "3395.1745633333", "3372.2108133333", "3382.9075"));
        dataList.add(Arrays.asList("1511193600000", "3382.3595", "3419.802", "3377.5989", "3411.086", "1.96871803E8", "2.653038498598E11", "3397.76952", "3414.41877", "3405.335215", "3397.2368833333", "3374.5748883333", "3392.3988"));
        dataList.add(Arrays.asList("1511280000000", "3417.3313", "3442.1777", "3404.2865", "3430.5479", "2.13567093E8", "2.736747334269E11", "3403.3708", "3415.98276", "3406.947405", "3399.0926766667", "3376.9619866667", "3410.4977"));
        dataList.add(Arrays.asList("1511366400000", "3425.0093", "3429.4237", "3342.3324", "3352.9888", "2.15265393E8", "2.704690239755E11", "3393.99626", "3408.43879", "3404.1846", "3398.0743", "3377.99471", "3430.4643"));
        dataList.add(Arrays.asList("1511452800000", "3340.3842", "3360.7459", "3328.3339", "3354.0006", "1.59569663E8", "2.076238848889E11", "3388.32916", "3400.50348", "3401.06367", "3396.9060133333", "3379.10006", "3351.9182"));
        dataList.add(Arrays.asList("1511712000000", "3346.6567", "3347.0506", "3315.2642", "3322.8308", "1.6643917E8", "2.058033985479E11", "3374.29082", "3387.91849", "3397.675775", "3394.7454966667", "3379.947365", "3353.8207"));
        dataList.add(Arrays.asList("1511798400000", "3311.2322", "3333.7998", "3300.7808", "3333.408", "1.38247799E8", "1.670629333557E11", "3358.75522", "3378.26237", "3394.62101", "3393.4112033333", "3379.97643", "3322.2298"));
        dataList.add(Arrays.asList("1511884800000", "3335.5671", "3343.0624", "3305.5721", "3338.0047", "1.83805932E8", "2.177694380923E11", "3340.24658", "3371.80869", "3391.71756", "3391.9657233333", "3379.5629233333", "3333.657"));
        dataList.add(Arrays.asList("1511971200000", "3328.6427", "3340.9201", "3306.2832", "3317.577", "1.56595851E8", "1.920767850696E11", "3333.16422", "3363.58024", "3388.45073", "3390.2151133333", "3378.762075", "3337.862"));
        dataList.add(Arrays.asList("1512057600000", "3315.1051", "3324.5161", "3302.4398", "3317.8092", "1.391983E8", "1.774902579728E11", "3325.92594", "3357.12755", "3385.780685", "3388.1587866667", "3378.0089833333", "3317.1884"));
        dataList.add(Arrays.asList("1512316800000", "3310.3814", "3323.9961", "3304.1034", "3310.3694", "1.48053288E8", "1.85025359645E11", "3323.43366", "3348.86224", "3381.8433", "3385.7620033333", "3377.1652083333", "3317.6174"));
        dataList.add(Arrays.asList("1512403200000", "3301.6906", "3315.7373", "3300.5117", "3303.0417", "2.08278862E8", "2.434896000699E11", "3317.3604", "3338.05781", "3376.23829", "3382.9094133333", "3376.09423", "3309.6183"));
        dataList.add(Arrays.asList("1512489600000", "3291.3128", "3296.2013", "3254.6108", "3294.1262", "1.51604452E8", "1.791743789115E11", "3308.5847", "3324.41564", "3370.1992", "3379.4368166667", "3374.6657266667", "3303.6751"));
        dataList.add(Arrays.asList("1512576000000", "3283.2791", "3291.2817", "3259.1637", "3272.0068", "1.321059E8", "1.617645889056E11", "3299.47066", "3316.31744", "3362.378115", "3374.8955466667", "3372.7803516667", "3293.9648"));
        dataList.add(Arrays.asList("1512662400000", "3264.4776", "3297.1304", "3258.7593", "3290.1683", "1.33209314E8", "1.657616497653E11", "3293.94248", "3309.93421", "3355.218845", "3370.6871833333", "3371.185215", "3272.0542"));
        dataList.add(Arrays.asList("1512921600000", "3290.4881", "3322.6736", "3288.2949", "3322.2402", "1.31965984E8", "1.729864314469E11", "3296.31664", "3309.87515", "3348.89682", "3368.4089", "3370.4486783333", "3289.9924"));
        dataList.add(Arrays.asList("1513008000000", "3320.3103", "3320.3103", "3280.3291", "3281.0104", "1.24604827E8", "1.609786151755E11", "3291.91038", "3304.63539", "3341.44888", "3364.6258033333", "3369.0415083333", "3322.1956"));
        dataList.add(Arrays.asList("1513094400000", "3278.3968", "3304.0101", "3273.3248", "3303.6608", "1.11998647E8", "1.452953120408E11", "3293.8173", "3301.201", "3336.504845", "3361.5453733333", "3367.8080216667", "3280.8136"));
        dataList.add(Arrays.asList("1513180800000", "3302.9322", "3309.5295", "3282.5732", "3293.5797", "1.20544235E8", "1.513460877237E11", "3298.13188", "3298.80127", "3331.190755", "3358.5675766667", "3366.3628633333", "3303.0373"));
        dataList.add(Arrays.asList("1513267200000", "3287.5292", "3287.5292", "3282.7899", "3282.7899", "4047004.0", "5.3123043232E9", "3296.6562", "3295.29934", "3326.213445", "3355.6202366667", "3364.6504033333", "3292.4385"));
        dataList.add(Arrays.asList("1513526400000", "3268.0335", "3280.5438", "3254.1775", "3268.3296", "1.20700389E8", "1.497382240536E11", "3285.87408", "3291.09536", "3319.9788", "3351.5939866667", "3362.93334", "3266.1371"));
        dataList.add(Arrays.asList("1513612800000", "3266.0191", "3296.9398", "3266.0191", "3296.6811", "1.15140134E8", "1.507865827107E11", "3289.00822", "3290.4593", "3314.258555", "3347.6452933333", "3361.9835483333", "3267.9224"));
        dataList.add(Arrays.asList("1513699200000", "3296.7403", "3300.2124", "3276.1201", "3288.019", "1.37745118E8", "1.679618479228E11", "3285.87986", "3289.84858", "3307.13211", "3343.41566", "3360.7302116667", "3296.5384"));
        dataList.add(Arrays.asList("1513785600000", "3281.1179", "3309.2233", "3267.4042", "3300.6818", "1.42127927E8", "1.737400955815E11", "3287.30028", "3292.71608", "3304.51676", "3339.1574366667", "3359.7973316667", "3287.6057"));
        dataList.add(Arrays.asList("1513872000000", "3297.6852", "3307.3276", "3293.4415", "3297.356", "1.24047326E8", "1.520994439066E11", "3290.2135", "3293.43485", "3301.68453", "3334.62418", "3358.6471616667", "3300.0593"));
        dataList.add(Arrays.asList("1514131200000", "3296.2106", "3312.2998", "3270.4407", "3280.8392", "1.46893635E8", "1.772935898835E11", "3292.71542", "3289.29475", "3299.58495", "3329.0294633333", "3357.35795", "3297.063"));
        dataList.add(Arrays.asList("1514217600000", "3277.8372", "3307.2994", "3274.3274", "3305.8916", "1.42434501E8", "1.746792638362E11", "3294.55752", "3291.78287", "3298.20913", "3324.8935433333", "3356.5749483333", "3280.461"));
        dataList.add(Arrays.asList("1514304000000", "3302.4612", "3307.0798", "3270.349", "3275.3966", "1.6267489E8", "1.982646751557E11", "3292.03304", "3288.95645", "3295.078725", "3320.65538", "3355.48474", "3306.1246"));
        dataList.add(Arrays.asList("1514390400000", "3272.2913", "3304.0962", "3263.7282", "3297.2059", "1.7537167E8", "2.080198596144E11", "3291.33786", "3289.31907", "3294.06017", "3317.2335266667", "3354.707715", "3275.7828"));
        dataList.add(Arrays.asList("1514476800000", "3295.2461", "3308.2249", "3292.7699", "3307.974", "1.41586836E8", "1.703567551293E11", "3293.46146", "3291.83748", "3293.56841", "3314.75479", "3354.0828933333", "3296.3847"));
        dataList.add(Arrays.asList("1514822400000", "3314.0307", "3349.053", "3314.0307", "3349.052", "2.0227886E8", "2.277884611129E11", "3307.10402", "3299.90972", "3295.50254", "3313.2891066667", "3354.231835", "3307.1721"));
        dataList.add(Arrays.asList("1514908800000", "3347.7428", "3379.9152", "3345.2887", "3370.0997", "2.13836149E8", "2.583665232348E11", "3319.94564", "3307.25158", "3298.85544", "3311.9228966667", "3354.57989", "3348.3259"));
        dataList.add(Arrays.asList("1514995200000", "3371.0", "3392.8264", "3365.2954", "3386.5043", "2.06955288E8", "2.430907686939E11", "3342.16718", "3317.10011", "3303.474345", "3310.4547766667", "3354.7737266667", "3369.1084"));
        dataList.add(Arrays.asList("1515081600000", "3386.464", "3402.0694", "3380.245", "3392.3555", "2.13060681E8", "2.481878405423E11", "3361.1971", "3326.26748", "3309.49178", "3311.767", "3354.92065", "3385.7102"));
        dataList.add(Arrays.asList("1515340800000", "3391.5528", "3412.7307", "3384.5591", "3410.0005", "2.36165106E8", "2.862132190945E11", "3381.6024", "3337.53193", "3315.48339", "3313.6336633333", "3355.2698383333", "3391.7501"));
        dataList.add(Arrays.asList("1515427200000", "3406.1116", "3417.2278", "3403.5869", "3414.8339", "1.91488551E8", "2.382499750697E11", "3394.75878", "3350.9314", "3320.113075", "3316.7004333333", "3355.722965", "3409.4795"));
        dataList.add(Arrays.asList("1515513600000", "3414.1128", "3430.2139", "3398.8423", "3422.1417", "2.09094997E8", "2.54515441261E11", "3405.16718", "3362.55641", "3327.16964", "3319.6582233333", "3356.5347133333", "3413.8996"));
        dataList.add(Arrays.asList("1515600000000", "3415.585", "3426.4829", "3405.639", "3425.5735", "1.73812133E8", "2.184141341294E11", "3412.98102", "3377.5741", "3333.265275", "3322.5771833333", "3357.2714533333", "3421.8343"));
        dataList.add(Arrays.asList("1515686400000", "3423.8793", "3435.4239", "3417.9802", "3429.3152", "1.74063404E8", "2.159614557477E11", "3420.37296", "3390.78503", "3340.05205", "3326.30179", "3358.2584516667", "3425.3449"));
        dataList.add(Arrays.asList("1515945600000", "3428.9508", "3442.5008", "3402.312", "3409.9907", "2.32009283E8", "2.863627329191E11", "3420.371", "3400.9867", "3346.41209", "3329.3745066667", "3358.7666466667", "3428.9407"));
        dataList.add(Arrays.asList("1516032000000", "3403.4694", "3437.5827", "3401.9606", "3437.4848", "2.11475468E8", "2.665788151688E11", "3424.90118", "3409.82998", "3354.86985", "3333.6116866667", "3359.686845", "3410.4882"));
        dataList.add(Arrays.asList("1516204800000", "3449.881", "3476.546", "3448.7885", "3475.9148", "2.20039555E8", "2.676926405358E11", "3435.6558", "3420.41149", "3363.831535", "3339.3741233333", "3361.1417683333", "3444.6713"));
        dataList.add(Arrays.asList("1516550400000", "3476.9939", "3503.3857", "3475.673", "3503.3857", "2.17487534E8", "2.612957140882E11", "3451.21824", "3432.09963", "3374.59987", "3346.34944", "3362.8931283333", "3487.864"));
        dataList.add(Arrays.asList("1516636800000", "3504.3439", "3547.2222", "3504.3439", "3546.9816", "2.38705904E8", "2.820918893459E11", "3474.75152", "3447.56224", "3386.91486", "3355.5152666667", "3365.2054066667", "3501.3622"));
        dataList.add(Arrays.asList("1516723200000", "3553.4787", "3569.4891", "3527.1119", "3560.7329", "2.52954496E8", "3.076292170629E11", "3504.89996", "3462.63548", "3400.083705", "3364.5340866667", "3367.610635", "3546.5048"));
        dataList.add(Arrays.asList("1516809600000", "3555.1677", "3571.4805", "3528.0349", "3548.3044", "2.43413422E8", "2.879895349563E11", "3527.06388", "3475.98253", "3413.456965", "3372.06956", "3370.23923", "3559.4653"));
        dataList.add(Arrays.asList("1516896000000", "3535.4931", "3574.9048", "3534.1952", "3559.0875", "2.22698295E8", "2.581495146071E11", "3543.69842", "3489.67711", "3426.11676", "3381.3387966667", "3372.9823", "3548.307"));
        dataList.add(Arrays.asList("1517155200000", "3563.64", "3587.0323", "3510.2689", "3523.5009", "2.36026965E8", "2.854170044405E11", "3547.72146", "3499.46985", "3438.521975", "3388.6668", "3375.1060866667", "3558.1288"));
        dataList.add(Arrays.asList("1517241600000", "3511.5005", "3523.0513", "3484.6556", "3488.1888", "1.86369975E8", "2.246816925375E11", "3535.9629", "3505.35721", "3448.07112", "3395.15377", "3376.8606733333", "3523.0007"));
        dataList.add(Arrays.asList("1517328000000", "3470.5089", "3495.4533", "3454.7259", "3481.5094", "2.07253403E8", "2.426579586276E11", "3520.1182", "3512.50908", "3456.74789", "3401.7777533333", "3378.698995", "3488.009"));
        dataList.add(Arrays.asList("1517414400000", "3478.6701", "3495.0933", "3424.4188", "3446.2424", "2.60504707E8", "2.92882256282E11", "3499.7058", "3513.38484", "3461.60741", "3407.70818", "3379.6510833333", "3480.8334"));
        dataList.add(Arrays.asList("1517500800000", "3419.2249", "3463.1639", "3388.8597", "3462.9372", "2.08120551E8", "2.362787556193E11", "3480.47574", "3512.08708", "3466.249285", "3413.25005", "3380.4476716667", "3446.9799"));
        dataList.add(Arrays.asList("1517760000000", "3411.6698", "3487.7211", "3406.2405", "3487.3847", "2.17673754E8", "2.52198769276E11", "3473.2525", "3510.48698", "3471.293305", "3419.8955733333", "3381.6556166667", "3462.0808"));
        dataList.add(Arrays.asList("1517846400000", "3418.01", "3440.124", "3364.2163", "3369.712", "2.80555477E8", "3.188185417624E11", "3449.55714", "3492.76002", "3470.16113", "3422.19658", "3380.6770083333", "3487.497"));
        dataList.add(Arrays.asList("1517932800000", "3412.7441", "3425.5377", "3304.007", "3309.5844", "2.60939714E8", "2.952644298852E11", "3415.17214", "3467.64517", "3465.140325", "3422.6041933333", "3378.6141866667", "3370.652"));
        dataList.add(Arrays.asList("1518019200000", "3281.0458", "3307.1623", "3225.7117", "3262.1481", "2.01262499E8", "2.223486852871E11", "3378.35328", "3439.02954", "3457.506035", "3421.9811566667", "3375.50531", "3309.2598"));
        dataList.add(Arrays.asList("1518105600000", "3172.8509", "3180.11", "3062.7426", "3130.9348", "2.56389176E8", "2.720950868391E11", "3311.9528", "3396.21427", "3442.94569", "3416.1492633333", "3370.5214033333", "3262.0504"));
        dataList.add(Arrays.asList("1518364800000", "3128.3709", "3168.1258", "3113.6054", "3153.5612", "1.53310831E8", "1.710031822303E11", "3245.1881", "3359.2203", "3429.345075", "3412.0880833333", "3366.3717316667", "3129.8508"));
        dataList.add(Arrays.asList("1518451200000", "3176.1066", "3219.2169", "3176.1066", "3185.5964", "1.51527597E8", "1.720452070059E11", "3208.36498", "3328.96106", "3417.159135", "3408.3677666667", "3362.8006466667", "3154.1254"));
        dataList.add(Arrays.asList("1518537600000", "3188.2475", "3203.4967", "3171.3836", "3199.4757", "1.00322827E8", "1.145522029593E11", "3186.34324", "3300.75769", "3406.633385", "3404.7511566667", "3359.7529733333", "3184.9587"));
        dataList.add(Arrays.asList("1519228800000", "3237.5692", "3269.9156", "3234.1152", "3268.7305", "1.38730445E8", "1.573038824511E11", "3187.65972", "3283.0065", "3398.19567", "3402.0737733333", "3357.68144", "3199.1589"));
        dataList.add(Arrays.asList("1519315200000", "3275.429", "3294.1338", "3258.4878", "3289.2403", "1.45790368E8", "1.59502514787E11", "3219.32082", "3265.63681", "3388.861945", "3399.37846", "3355.6506783333", "3268.5589"));
        dataList.add(Arrays.asList("1519574400000", "3307.295", "3335.9856", "3281.615", "3330.2506", "1.88568602E8", "2.133330348904E11", "3254.6587", "3249.9234", "3380.20519", "3397.5033366667", "3353.9790566667", "3289.0241"));
        dataList.add(Arrays.asList("1519660800000", "3328.6719", "3328.6719", "3284.6293", "3291.5253", "1.74219368E8", "2.043943814638E11", "3275.84448", "3242.10473", "3367.432375", "3394.14233", "3352.954665", "3329.5737"));
        dataList.add(Arrays.asList("1519747200000", "3264.0585", "3277.8324", "3239.8438", "3259.4959", "1.51043536E8", "1.802338152261E11", "3287.84852", "3237.09588", "3352.370525", "3389.12551", "3351.3795866667", "3292.0679"));
        dataList.add(Arrays.asList("1519833600000", "3235.0887", "3280.1491", "3228.5848", "3273.9445", "1.5909832E8", "1.852777819886E11", "3288.89132", "3238.27552", "3338.65253", "3384.4291966667", "3350.564815", "3259.408"));
        dataList.add(Arrays.asList("1519920000000", "3248.4464", "3269.9377", "3242.2669", "3254.5758", "1.50861063E8", "1.786029918652E11", "3281.95842", "3250.63962", "3323.426945", "3378.8436666667", "3349.250945", "3273.7549"));
        dataList.add(Arrays.asList("1520179200000", "3255.8713", "3269.3999", "3236.7172", "3257.5263", "1.44835988E8", "1.741486345035E11", "3267.41356", "3261.03613", "3310.128215", "3373.2420933333", "3347.9096383333", "3254.5283"));
        dataList.add(Arrays.asList("1520265600000", "3266.4868", "3290.2534", "3243.654", "3290.1747", "1.95358046E8", "2.243817894818E11", "3267.14344", "3271.49396", "3300.22751", "3368.6040766667", "3347.4529333333", "3256.9263"));
        dataList.add(Arrays.asList("1520352000000", "3288.8645", "3308.4063", "3264.7639", "3271.4626", "1.68665019E8", "1.934893353581E11", "3269.53678", "3278.69265", "3289.72517", "3363.9864733333", "3346.68049", "3289.6419"));
        dataList.add(Arrays.asList("1520438400000", "3268.3468", "3289.4974", "3261.5524", "3289.2943", "1.49827519E8", "1.758953912166E11", "3272.60674", "3280.74903", "3281.877765", "3359.04679", "3346.3292383333", "3271.6683"));
        dataList.add(Arrays.asList("1520524800000", "3291.4258", "3309.7153", "3283.5591", "3307.638", "1.68424509E8", "2.023491819157E11", "3283.21918", "3282.5888", "3274.112805", "3353.4375633333", "3346.4058433333", "3288.4055"));
        dataList.add(Arrays.asList("1520784000000", "3319.2089", "3333.5611", "3313.5552", "3326.3304", "2.06532436E8", "2.478941762082E11", "3296.98", "3282.19678", "3266.06009", "3347.53572", "3346.94258", "3307.1656"));
        dataList.add(Arrays.asList("1520870400000", "3324.1215", "3333.8754", "3307.3764", "3311.2755", "1.77114353E8", "2.023751169543E11", "3301.20016", "3284.1718", "3263.138265", "3339.67885", "3347.5970583333", "3326.6992"));
        dataList.add(Arrays.asList("1520956800000", "3298.6655", "3304.9787", "3287.3558", "3291.2586", "1.57491085E8", "1.834576304349E11", "3305.15936", "3287.34807", "3262.221975", "3330.6963733333", "3347.61523", "3310.2389"));
        dataList.add(Arrays.asList("1521043200000", "3277.5143", "3297.0981", "3273.2008", "3291.6142", "1.48573606E8", "1.795166180627E11", "3305.62334", "3289.11504", "3263.69528", "3322.1400333333", "3347.1047966667", "3291.3819"));
        dataList.add(Arrays.asList("1521129600000", "3290.209", "3300.5654", "3269.2826", "3270.3896", "1.41203647E8", "1.67761534889E11", "3298.17366", "3290.69642", "3270.66802", "3312.51677", "3346.9277833333", "3291.112"));
        dataList.add(Arrays.asList("1521388800000", "3264.9281", "3280.5673", "3251.0471", "3279.6045", "1.3795498E8", "1.697722069912E11", "3288.82848", "3292.90424", "3276.970185", "3304.38689", "3346.526845", "3269.8821"));
        dataList.add(Arrays.asList("1521475200000", "3257.2188", "3292.5742", "3252.4333", "3290.4577", "1.39881081E8", "1.669798813612E11", "3284.66492", "3292.93254", "3282.21325", "3297.7958533333", "3346.4748116667", "3279.2517"));
        dataList.add(Arrays.asList("1521561600000", "3299.731", "3314.209", "3268.8779", "3281.5934", "1.68398277E8", "2.086176866746E11", "3282.73188", "3293.94562", "3286.319135", "3291.1319866667", "3346.45487", "3290.6399"));
        dataList.add(Arrays.asList("1521648000000", "3281.265", "3288.7967", "3242.7619", "3263.8288", "1.50432977E8", "1.844717287778E11", "3277.1748", "3291.39907", "3286.07405", "3285.0515333333", "3346.3798566667", "3280.9521"));
        dataList.add(Arrays.asList("1521734400000", "3172.772", "3188.2351", "3110.6637", "3153.0866", "2.75532013E8", "2.934050660322E11", "3253.7142", "3275.94393", "3279.266365", "3274.72318", "3343.986615", "3263.4803"));
        dataList.add(Arrays.asList("1521993600000", "3117.3192", "3134.2759", "3091.4575", "3133.9238", "1.8636635E8", "2.159102590579E11", "3224.57806", "3256.70327", "3269.450025", "3262.94115", "3341.4183616667", "3152.7608"));
        dataList.add(Arrays.asList("1522080000000", "3164.7976", "3172.7777", "3143.5695", "3166.2921", "1.89073383E8", "2.270850873043E11", "3199.74494", "3242.20493", "3263.188365", "3256.1604866667", "3339.1785333333", "3133.7218"));
        dataList.add(Arrays.asList("1522166400000", "3130.5711", "3165.2126", "3117.5336", "3122.2218", "1.63500488E8", "2.086178516977E11", "3167.87062", "3225.30125", "3256.32466", "3249.9150666667", "3336.25963", "3166.6488"));
        dataList.add(Arrays.asList("1522252800000", "3127.2628", "3174.5089", "3098.2465", "3160.9267", "1.70312384E8", "2.109202233188E11", "3147.2902", "3212.2325", "3250.67377", "3246.54102", "3334.2610883333", "3122.2895"));
        dataList.add(Arrays.asList("1522339200000", "3161.7856", "3177.7208", "3152.8888", "3169.0159", "1.54646596E8", "1.960066969153E11", "3150.47606", "3202.09513", "3246.395775", "3247.81039", "3331.9798266667", "3160.5306"));
        dataList.add(Arrays.asList("1522598400000", "3169.7787", "3192.3403", "3159.986", "3163.8596", "1.77727777E8", "2.262535663884E11", "3156.46322", "3190.52064", "3241.71244", "3248.15367", "3330.1208766667", "3168.8966"));
        dataList.add(Arrays.asList("1522684800000", "3130.013", "3144.3321", "3119.1321", "3136.4415", "1.52216674E8", "1.896927151786E11", "3150.4931", "3175.11902", "3234.02578", "3246.5151733333", "3327.44147", "3163.179"));
        dataList.add(Arrays.asList("1522771200000", "3147.0491", "3163.3404", "3128.8668", "3131.8391", "1.46976682E8", "1.934928241907E11", "3152.41656", "3160.14359", "3227.044605", "3244.26062", "3324.5058883333", "3136.6332"));
        dataList.add(Arrays.asList("1523203200000", "3125.4415", "3146.0926", "3110.3025", "3139.3278", "1.39608621E8", "1.772369200787E11", "3148.09678", "3147.69349", "3219.54628", "3239.9471966667", "3321.010485", "3131.1114"));
        dataList.add(Arrays.asList("1523289600000", "3144.2568", "3190.6492", "3139.0807", "3190.6492", "1.68201359E8", "2.060874131468E11", "3152.42344", "3151.44975", "3213.69684", "3236.6608266667", "3318.0196433333", "3138.2936"));
        dataList.add(Arrays.asList("1523376000000", "3197.3719", "3220.8453", "3191.5865", "3208.315", "1.75867197E8", "2.114204239911E11", "3161.31452", "3158.88887", "3207.79607", "3232.5963066667", "3315.0498216667", "3190.3216"));
        dataList.add(Arrays.asList("1523462400000", "3203.2782", "3205.2522", "3177.0492", "3180.1987", "1.48231313E8", "1.850400545629E11", "3170.06596", "3160.27953", "3201.24223", "3228.88542", "3311.513875", "3208.0818"));
        dataList.add(Arrays.asList("1523548800000", "3192.0418", "3197.8959", "3155.5058", "3159.3885", "1.2755231E8", "1.636517506399E11", "3175.57584", "3163.9962", "3194.648725", "3225.5485066667", "3307.3370083333", "3180.1583"));
        dataList.add(Arrays.asList("1523808000000", "3152.8882", "3153.1063", "3096.1003", "3110.751", "1.54415695E8", "1.922333492495E11", "3169.86048", "3158.97863", "3185.605565", "3220.1087233333", "3302.26896", "3159.0521"));
        dataList.add(Arrays.asList("1523894400000", "3112.9747", "3118.7596", "3064.0281", "3067.5191", "1.47222846E8", "1.854280292547E11", "3145.23446", "3148.82895", "3175.46204", "3213.8735", "3296.3585833333", "3110.6489"));
        dataList.add(Arrays.asList("1523980800000", "3091.9095", "3096.889", "3041.625", "3091.3105", "1.59290503E8", "2.022389699704E11", "3121.83356", "3141.57404", "3166.04734", "3208.3329733333", "3290.7875333333", "3066.7967"));
        dataList.add(Arrays.asList("1524067200000", "3094.2738", "3127.4389", "3090.2867", "3117.551", "1.60056805E8", "1.961989846832E11", "3109.30402", "3139.68499", "3157.402005", "3202.57885", "3285.5914633333", "3091.3987"));
        dataList.add(Arrays.asList("1524153600000", "3105.4617", "3111.1707", "3065.9214", "3071.4747", "1.55147091E8", "1.851390691485E11", "3091.72126", "3133.64855", "3146.89607", "3195.9125866667", "3279.94953", "3117.376"));
        dataList.add(Arrays.asList("1524412800000", "3063.4427", "3085.0573", "3045.9369", "3068.7988", "1.3168634E8", "1.578547951025E11", "3083.33082", "3126.59565", "3137.14457", "3188.5627366667", "3273.8047633333", "3071.5425"));
        dataList.add(Arrays.asList("1524499200000", "3069.7455", "3136.0395", "3069.7455", "3128.6007", "1.62274806E8", "1.955450061216E11", "3095.54714", "3120.3908", "3135.920275", "3182.5948266667", "3268.016195", "3068.012"));
        dataList.add(Arrays.asList("1524585600000", "3112.3978", "3122.9082", "3107.0008", "3117.9974", "1.27311053E8", "1.612981416615E11", "3100.88452", "3111.35904", "3135.123955", "3175.6503933333", "3261.5930566667", "3128.9271"));
        dataList.add(Arrays.asList("1524672000000", "3119.4962", "3121.9334", "3067.9286", "3075.8506", "1.33972736E8", "1.637837746019E11", "3092.54444", "3100.92423", "3130.60188", "3167.8028966667", "3253.7408733333", "3117.9739"));
        dataList.add(Arrays.asList("1524758400000", "3082.4148", "3088.0347", "3049.9139", "3082.1789", "1.31225643E8", "1.795202600023E11", "3094.68528", "3093.20327", "3128.599735", "3160.8335733333", "3245.7649733333", "3075.0301"));
        dataList.add(Arrays.asList("1525190400000", "3087.4086", "3097.6039", "3064.763", "3082.0999", "1.34184566E8", "1.682461530045E11", "3097.3455", "3090.33816", "3124.658395", "3153.8497633333", "3237.9948983333", "3082.2316"));
        dataList.add(Arrays.asList("1525276800000", "3074.5165", "3105.6598", "3056.1573", "3101.1346", "1.3998562E8", "1.734449591489E11", "3091.85228", "3093.69971", "3121.26433", "3148.20793", "3230.36235", "3081.1773"));
        dataList.add(Arrays.asList("1525363200000", "3093.1169", "3104.0925", "3086.7845", "3090.7667", "1.18711265E8", "1.484363633541E11", "3086.40614", "3093.64533", "3117.609685", "3141.9133366667", "3223.1501133333", "3100.8586"));
        dataList.add(Arrays.asList("1525622400000", "3094.8989", "3136.8363", "3091.6579", "3136.6332", "1.38948186E8", "1.782438700034E11", "3098.56266", "3095.55355", "3117.61927", "3136.7858533333", "3217.2908533333", "3091.0334"));
        dataList.add(Arrays.asList("1525708800000", "3135.2957", "3169.7014", "3134.0621", "3161.5954", "1.46927061E8", "1.890116473802E11", "3114.44596", "3104.56562", "3119.107085", "3132.78592", "3211.9589533333", "3136.6448"));
        dataList.add(Arrays.asList("1525795200000", "3160.1382", "3165.3733", "3145.6574", "3158.8136", "1.22637268E8", "1.534564262161E11", "3129.7887", "3113.5671", "3120.081375", "3129.2854133333", "3207.1684733333", "3161.4976"));
        dataList.add(Arrays.asList("1525881600000", "3169.0498", "3176.1376", "3155.5331", "3175.1668", "1.33000756E8", "1.654403638777E11", "3144.59514", "3118.22371", "3119.307255", "3130.02142", "3202.3723", "3159.1502"));
        dataList.add(Arrays.asList("1525968000000", "3179.7967", "3180.7564", "3162.2121", "3162.8526", "1.30659749E8", "1.673642903662E11", "3159.01232", "3122.70923", "3117.034135", "3130.9857133333", "3196.9634316667", "3174.4127"));
        dataList.add(Arrays.asList("1526227200000", "3167.0418", "3183.8159", "3163.476", "3174.1361", "1.29327353E8", "1.724106910539E11", "3166.5129", "3132.53778", "3116.731005", "3131.24718", "3193.7038333333", "3163.2632"));
        dataList.add(Arrays.asList("1526313600000", "3180.4245", "3192.8069", "3164.5181", "3192.5805", "1.24549051E8", "1.629907900095E11", "3172.70992", "3143.57794", "3118.390605", "3133.59247", "3191.7537683333", "3174.032"));
        dataList.add(Arrays.asList("1526400000000", "3180.2259", "3191.9481", "3166.8113", "3169.7072", "1.30524968E8", "1.745909798339E11", "3174.88864", "3152.33867", "3121.338415", "3133.8851533333", "3190.2130866667", "3192.1183"));
        dataList.add(Arrays.asList("1526486400000", "3170.0064", "3172.7664", "3148.6213", "3154.2365", "1.13995567E8", "1.505988421852E11", "3170.70258", "3157.64886", "3125.674285", "3133.3925066667", "3190.6014483333", "3169.5652"));
        dataList.add(Arrays.asList("1526572800000", "3151.0818", "3193.4531", "3144.776", "3193.0475", "1.36516918E8", "1.680380574771E11", "3176.74156", "3167.87694", "3130.761135", "3134.3654366667", "3191.2595533333", "3154.2825"));
        dataList.add(Arrays.asList("1526832000000", "3206.1756", "3219.7398", "3203.3362", "3214.361", "1.64459413E8", "2.026634645153E11", "3184.78654", "3175.64972", "3135.601635", "3136.9627533333", "3191.7389633333", "3193.3034"));
        dataList.add(Arrays.asList("1526918400000", "3211.247", "3214.5888", "3192.2277", "3214.5324", "1.44292684E8", "1.857216677516E11", "3189.17692", "3180.94342", "3142.75452", "3139.7191966667", "3191.9899083333", "3213.8404"));
        dataList.add(Arrays.asList("1527004800000", "3205.437", "3205.437", "3169.0925", "3169.2393", "1.57807648E8", "1.993581010154E11", "3189.08334", "3181.98599", "3147.776545", "3140.7162466667", "3190.3317216667", "3214.3497"));
        dataList.add(Arrays.asList("1527091200000", "3167.9391", "3173.5309", "3152.0692", "3154.8936", "1.240858E8", "1.606581855021E11", "3189.21476", "3179.95867", "3149.09119", "3139.5243933333", "3188.09261", "3168.9642"));
        return dataList;
    }
}
