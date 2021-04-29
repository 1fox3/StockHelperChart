package com.fox.stockhelperchart.markerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.widget.TextView;

import com.fox.stockhelperchart.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class StockMarkerView extends MarkerView {
    TextView markerViewStrTV;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public StockMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        markerViewStrTV = findViewById(R.id.markerViewStrTV);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        ChartData chartData = (ChartData) getChartView().getData();
        int setCount = chartData.getDataSetCount();
        String markerViewText = "";
        for (int i = 0; i < setCount; i++) {
            DataSet dataSet = (DataSet) chartData.getDataSetByIndex(i);
            markerViewText += dataSet.getLabel() + "" + dataSet.getEntryForIndex((int)e.getX()).getY();
        }
        markerViewStrTV.setText(markerViewText);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        MPPointF mpPointF = getOffset();

        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(mpPointF.x, mpPointF.y);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    @Override
    public MPPointF getOffset() {
        RectF rectF = getChartView().getViewPortHandler().getContentRect();
        return new MPPointF(
                (rectF.left + rectF.right)/2 - (int)(getWidth()/2),
                rectF.top
        );
    }
}
