package com.fox.stockhelperchart.activity;

import android.os.Bundle;

import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.StockMultiDayMinuteChart;
import com.fox.stockhelpercommon.entity.stock.po.StockMinuteKLinePo;
import com.fox.stockhelpercommon.spider.out.StockSpiderFiveDayMinuteKLineApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.SneakyThrows;


/**
 * 对天分钟线图
 *
 * @author lusongsong
 * @date 2021/2/25 16:41
 */
public class MultiDayMinuteChartActivity extends StockChartBaseActivity {
    @BindView(R.id.stockMultiDayMinuteChart)
    StockMultiDayMinuteChart stockMultiDayMinuteChart;
    List<StockMinuteKLinePo> stockMinuteKLinePoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day_kline);
        ButterKnife.bind(MultiDayMinuteChartActivity.this);
        stockMultiDayMinuteChart.initChart();
        handleStockMultiDayMinuteKLine();
    }

    /**
     * 补充多天天分钟线图数据
     */
    private void handleStockMultiDayMinuteKLine() {
        Runnable stockMinuteKLineRunnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                StockSpiderFiveDayMinuteKLineApi stockSpiderFiveDayMinuteKLineApi =
                        new StockSpiderFiveDayMinuteKLineApi();
                stockMinuteKLinePoList =
                        stockSpiderFiveDayMinuteKLineApi
                                .fiveDayMinuteKLine(SH_TEST_STOCK);
                stockMultiDayMinuteChart.setStockMinuteKLineData(stockMinuteKLinePoList);
            }
        };
        Thread thread = new Thread(stockMinuteKLineRunnable);
        thread.start();
    }
}
