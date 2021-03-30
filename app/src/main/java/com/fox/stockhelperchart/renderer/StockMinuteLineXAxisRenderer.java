package com.fox.stockhelperchart.renderer;

import android.graphics.Canvas;
import android.graphics.Path;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author lusongsong
 * @date 2021/3/15 17:04
 */
public class StockMinuteLineXAxisRenderer extends XAxisRenderer {
    /**
     * 网格线位置
     */
    private int[] gradLinePos = new int[]{0, 61, 121, 181, 241};
    /**
     * 坐标显示的位置
     */
    private int[] labelPos = new int[]{0, 61, 121, 181, 241};

    public StockMinuteLineXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    public void setGradLinePos(int[] pos) {
        gradLinePos = pos;
    }

    public void setLabelPos(int[] labelPosTree) {
        labelPos = labelPosTree;
    }

    /**
     * Computes the axis values.
     *
     * @param min - the minimum value in the data object for this axis
     * @param max - the maximum value in the data object for this axis
     */
    public void computeAxis(float min, float max, boolean inverted) {
        mXAxis.mEntries = new float[labelPos.length];
        for (int i = 0; i < labelPos.length; i++) {
            mXAxis.mEntries[i] = (float)labelPos[i];
        }
        mAxis.mCenteredEntries = new float[]{};
        mAxis.mEntryCount = mAxis.mEntries.length;
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        int clipRestoreCount = c.save();
        c.clipRect(getGridClippingRect());

        float[] positions = new float[gradLinePos.length * 2];

        for (int i = 0; i < positions.length; i += 2) {
            positions[i] = gradLinePos[i / 2];
            positions[i + 1] = gradLinePos[i / 2];
        }

        mTrans.pointValuesToPixel(positions);

        setupGridPaint();

        Path gridLinePath = mRenderGridLinesPath;
        gridLinePath.reset();

        for (int i = 0; i < positions.length; i += 2) {

            drawGridLine(c, positions[i], positions[i + 1], gridLinePath);
        }

        c.restoreToCount(clipRestoreCount);
    }
}
