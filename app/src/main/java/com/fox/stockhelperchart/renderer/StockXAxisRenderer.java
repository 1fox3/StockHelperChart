package com.fox.stockhelperchart.renderer;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.TreeMap;

/**
 * @author lusongsong
 * @date 2021/3/15 17:04
 */
public class StockXAxisRenderer extends XAxisRenderer {
    private TreeMap<Integer, String> XGridLine = new TreeMap<Integer, String>() {
        {
            put(0, "9:30");
            put(61, "10:30");
            put(121, "11:30/13:00");
            put(181, "14:00");
            put(241, "15:00");
        }
    };

    public StockXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    /**
     * Computes the axis values.
     *
     * @param min - the minimum value in the data object for this axis
     * @param max - the maximum value in the data object for this axis
     */
    public void computeAxis(float min, float max, boolean inverted) {
        mAxis.mEntries = new float[XGridLine.keySet().size()];
        for (int i = 0; i < XGridLine.keySet().size(); i++) {
            mAxis.mEntries[i] = Float.valueOf(XGridLine.keySet().toArray()[i].toString()).floatValue();
        }
        mAxis.mCenteredEntries = new float[]{};
        mAxis.mEntryCount = mAxis.mEntries.length;
    }
}
