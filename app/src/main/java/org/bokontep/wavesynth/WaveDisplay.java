package org.bokontep.wavesynth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveDisplay extends View {
    public WaveDisplay(Context context, AttributeSet attrs) {
        super(context,attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }
    public void setupPaint()
    {
        drawPaintForeground = new Paint();
        drawPaintForeground.setColor(foregroundColor);
        drawPaintForeground.setAntiAlias(true);
        drawPaintForeground.setStrokeWidth(4);
        drawPaintForeground.setStyle(Paint.Style.FILL_AND_STROKE);

        drawPaintForeground.setTextSize(44);

        drawPaintBackground = new Paint();
        drawPaintBackground.setColor(backgroundColor);
        drawPaintBackground.setAntiAlias(true);
        drawPaintBackground.setStrokeWidth(4);
        drawPaintBackground.setStyle(Paint.Style.FILL_AND_STROKE);

        drawPaintBackground.setTextSize(44);
    }
    public void setData(float[] newData)
    {
        data = newData;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0,0,w,h,this.drawPaintForeground);
        if(data == null)
        {
            return;
        }
        for(int i=0;i<data.length-1;i++)
        {

            canvas.drawLine(xstart + i*xstep,ystart-ystep*data[i],xstart+((i+1)*xstep),ystart-ystep*data[i+1], drawPaintForeground);
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        if(data!=null) {
            xstep = (int)(w / data.length);
            ystep = (int)(h/2.0f);
            xstart = (int)0;
            ystart = (int)(h/2.0f);
        }
        else
        {
            xstep = (int)(w / 256.0f);
            ystep = (int)(h/2.0f);
            xstart = (int)0f;
            ystart = (int)(h/2.0f);
        }
        //Log.d("INFO","ViewSize=("+w+","+h+") xstep="+xstep+" ystep="+ystep+" xstart="+xstart+"ystart="+ystart);
    }
    float[] data;
    int w;
    int h;
    int xstep;
    int ystep;
    int xstart;
    int ystart;
    private final int foregroundColor = Color.GREEN;
    private final int backgroundColor = Color.BLACK;
    private Paint drawPaintForeground;
    private Paint drawPaintBackground;
}
