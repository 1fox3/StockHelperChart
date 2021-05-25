package com.fox.stockhelperchart.activity;

import android.os.Bundle;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.StockSingleDayMinuteChart;
import com.fox.stockhelpercommon.entity.stock.po.StockMinuteKLinePo;
import com.fox.stockhelpercommon.spider.out.StockSpiderRealtimeMinuteKLineApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.SneakyThrows;

/**
 * 分钟粒度K线图界面
 *
 * @author lusongsong
 * @date 2021/2/25 16:40
 */
public class SingleDayMinuteChartActivity extends StockChartBaseActivity {
    @BindView(R.id.stockSingleDayMinuteChart)
    StockSingleDayMinuteChart stockSingleDayMinuteChart;
    /**
     * 股票分钟数据
     */
    StockMinuteKLinePo stockMinuteKLinePo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minute_kline);
        ButterKnife.bind(SingleDayMinuteChartActivity.this);
        stockSingleDayMinuteChart.initChart();
        handleStockSingleDayMinuteKLine();
    }

    /**
     * 补充单天分钟线图数据
     */
    private void handleStockSingleDayMinuteKLine() {
        Runnable stockMinuteKLineRunnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                StockSpiderRealtimeMinuteKLineApi stockSpiderRealtimeMinuteKLineApi =
                        new StockSpiderRealtimeMinuteKLineApi();
                stockMinuteKLinePo =
                        stockSpiderRealtimeMinuteKLineApi
                                .realtimeMinuteKLine(SH_TEST_STOCK);
                stockSingleDayMinuteChart.setStockMinuteKLineData(stockMinuteKLinePo);
            }
        };
        Thread thread = new Thread(stockMinuteKLineRunnable);
        thread.start();
    }
}
