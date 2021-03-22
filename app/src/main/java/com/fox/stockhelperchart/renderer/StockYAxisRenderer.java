package com.fox.stockhelperchart.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class StockYAxisRenderer extends YAxisRenderer {
    protected boolean isLabelValueInside = true;
    protected float flatValue;
    protected int[] labelColorArr;
    protected int labelStep = 2;

    public StockYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    public void setLabelValueInside(boolean enabled) {
        isLabelValueInside = enabled;
    }

    public void setLabelColorArr(int[] colorArr) {
        labelColorArr = colorArr;
    }

    public void setFlatValue(float value) {
        flatValue = value;
    }

    public int getLabelColor(int pos) {
        float labelValue = mYAxis.mEntries[pos];
        int colorPos = 2;
        if (labelValue > flatValue) {
            colorPos = 0;
        } else if (labelValue < flatValue) {
            colorPos = 1;
        } else {
            colorPos = 2;
        }

        return colorPos < labelColorArr.length ? labelColorArr[colorPos] : 0;
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {

        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        float xOffset = mYAxis.getLabelXOffset();
        float yOffset;
        int oriLabelColor = mAxisLabelPaint.getColor();
        int labelColor;
        // draw
        for (int i = from; i < to; i += labelStep) {
            String text = mYAxis.getFormattedLabel(i);
            if (i == 0) {
                yOffset = -offset;
            } else if (i == to - 1) {
                yOffset = 4 * offset;
            } else {
                yOffset = offset;
            }
            labelColor = getLabelColor(i);
            labelColor = 0 == labelColor ? oriLabelColor : labelColor;
            mAxisLabelPaint.setColor(labelColor);
            c.drawText(text,
                    fixedPosition + xOffset,
                    positions[i * 2 + 1] + yOffset,
                    mAxisLabelPaint);
        }
    }
}
