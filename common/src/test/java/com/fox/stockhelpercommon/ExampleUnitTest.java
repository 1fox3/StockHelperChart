package com.fox.stockhelpercommon;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.stockhelpercommon.entity.stock.po.StockMinuteKLinePo;
import com.fox.stockhelpercommon.spider.out.StockSpiderFiveDayMinuteKLineApi;
import com.fox.stockhelpercommon.spider.out.StockSpiderRealtimeMinuteKLineApi;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void spiderRealtimeMinuteKLineApiTest() {
        StockSpiderRealtimeMinuteKLineApi stockSpiderRealtimeMinuteKLineApi =
                new StockSpiderRealtimeMinuteKLineApi();
        StockMinuteKLinePo stockMinuteKLinePo =
                stockSpiderRealtimeMinuteKLineApi
                        .realtimeMinuteKLine(new StockVo("603383", StockConst.SM_A));
        System.out.println(stockMinuteKLinePo);
    }

    @Test
    public void spiderFiveDayMinuteKLineApiTest() {
        StockSpiderFiveDayMinuteKLineApi stockSpiderFiveDayMinuteKLineApi =
                new StockSpiderFiveDayMinuteKLineApi();
        List<StockMinuteKLinePo> stockMinuteKLinePoList =
                stockSpiderFiveDayMinuteKLineApi
                        .fiveDayMinuteKLine(new StockVo("603383", StockConst.SM_A));
        System.out.println(stockMinuteKLinePoList);
    }
}