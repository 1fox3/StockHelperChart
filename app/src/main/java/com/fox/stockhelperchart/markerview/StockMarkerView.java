package com.fox.stockhelperchart.markerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fox.stockhelperchart.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockMarkerView extends MarkerView {
    @BindView(R.id.markerViewStrTV)
    TextView markerViewStrTV;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public StockMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        View view = LayoutInflater.from(getContext()).inflate(
                layoutResource, this, true
        );
        ButterKnife.bind(this, view);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        markerViewStrTV.setText(e.getX() + ":" + e.getY());
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
                (rectF.left + rectF.right)/2 - getWidth()/2,
                rectF.top
        );
    }


}
