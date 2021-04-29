package com.fox.stockhelperchart.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.fox.spider.stock.api.nets.NetsRealtimeMinuteKLineApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.stockhelperchart.R;
import com.fox.stockhelperchart.StockSingleDayMinuteChart;
import com.fox.stockhelpercommon.entity.stock.po.StockMinuteKLinePo;
import com.fox.stockhelpercommon.spider.out.StockSpiderRealtimeMinuteKLineApi;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.SneakyThrows;

/**
 * 分钟粒度K线图界面
 *
 * @author lusongsong
 * @date 2021/2/25 16:40
 */
public class MinuteKLineActivity extends AppCompatActivity {
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
        ButterKnife.bind(MinuteKLineActivity.this);
        stockSingleDayMinuteChart.initChart();
        handleStockMinuteKLine();
    }

    /**
     * 刷新交易价格线图信息
     */
    private void handleStockMinuteKLine() {
        Runnable stockMinuteKLineRunnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                StockSpiderRealtimeMinuteKLineApi stockSpiderRealtimeMinuteKLineApi =
                        new StockSpiderRealtimeMinuteKLineApi();
                stockMinuteKLinePo =
                        stockSpiderRealtimeMinuteKLineApi
                                .realtimeMinuteKLine(new StockVo("603383", StockConst.SM_A));
                stockSingleDayMinuteChart.setStockMinuteKLineData(stockMinuteKLinePo);
            }
        };
        Thread thread = new Thread(stockMinuteKLineRunnable);
        thread.start();
    }
}
