package org.bokontep.wavesynth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

public class Scope extends View {
    private final int blackColor = Color.GREEN;
    private final int whiteColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaintBlack;
    private Paint drawPaintWhite;
    private Paint markerPaint;
    private float[] data;
    private String text;
    private float xstep = 1.0f;
    private float ystep = 30.0f;
    private float xstart = 0.0f;
    private float ystart = 0.0f;
    private float w = 0f;
    private float h = 0f;
    private float settingsPercentage;
    String [] markers = new String[10];
    float[] x_vals = new float[10];
    float[] y_vals = new float[10];
    public static final int STATE_PLAY=0;
    public static final int STATE_SETTINGS=1;
    private int xNoteScale = 160;
    public Scope(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }
    public void setXNoteScale(int xNoteScale)
    {
        this.xNoteScale = xNoteScale;
    }
    private void setupPaint()
    {
        markerPaint = new Paint();
        markerPaint.setColor(blackColor);
        markerPaint.setStrokeWidth(8);
        markerPaint.setStyle(Paint.Style.STROKE);

        drawPaintBlack = new Paint();
        drawPaintBlack.setColor(blackColor);
        drawPaintBlack.setAntiAlias(true);
        drawPaintBlack.setStrokeWidth(4);
        drawPaintBlack.setStyle(Paint.Style.FILL_AND_STROKE);

        drawPaintBlack.setTextSize(44);

        drawPaintWhite = new Paint();
        drawPaintWhite.setColor(whiteColor);
        drawPaintWhite.setAntiAlias(true);
        drawPaintWhite.setStrokeWidth(4);
        drawPaintWhite.setStyle(Paint.Style.FILL_AND_STROKE);

        drawPaintWhite.setTextSize(44);
    }
    public void setData(float[] newData)
    {

        data = newData;
    }
    public void setText(String text)
    {
        this.text = text;
    }
    public void setMarker(String text, float x, float y)
    {
        int n = Integer.parseInt(text);
        if(n>=0 && n<10) {
            markers[n] = text;
            x_vals[n] = x;
            y_vals[n] = y;
        }

    }
    public void setSettingsPercentage(float percentage)
    {
        this.settingsPercentage = percentage;
    }
    public void unsetMarker(String text)
    {
        int n = Integer.parseInt(text);
        if(n>=0 && n<10) {
            markers[n]="";
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int count = getWidth()/xNoteScale+1;
        if(data==null)
        {
            return;
        }
        for(int i=0;i<10;i++)
        {
            if(markers[i]!=null)
            {
                if(markers[i].length()>0)
                {

                    canvas.drawCircle(x_vals[i],y_vals[i],130,markerPaint);
                    canvas.drawText(markers[i],x_vals[i],y_vals[i],drawPaintBlack);
                }
            }
        }
        canvas.drawLine(0,0,w,0,drawPaintBlack);
        canvas.drawLine(0,h,w,h,drawPaintBlack);
        canvas.drawLine(0,0,0,h,drawPaintBlack);
        canvas.drawLine(w,0,w,h,drawPaintBlack);
        for(int i=0;i<count;i++)
        {
            canvas.drawLine(i*xNoteScale,0,i*xNoteScale,h,drawPaintBlack);
        }
        for(int i=0;i<data.length-1;i++)
        {

            canvas.drawLine(xstart + i*xstep,ystart-ystep*data[i],xstart+((i+1)*xstep),ystart-ystep*data[i+1],drawPaintBlack);
        }
        canvas.drawArc(0f,500f,100f,600f,0,settingsPercentage*360,true,drawPaintBlack);
        if(text!=null) {
            canvas.drawText(text, 30, 65, drawPaintBlack);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        if(data!=null) {
            xstep = (int)(w / data.length);
            ystep = (int)(h/4.0f);
            xstart = (int)0;
            ystart = (int)(h/2.0f);
        }
        else
        {
            xstep = (int)(w / 256.0f);
            ystep = (int)(h/4.0f);
            xstart = (int)0f;
            ystart = (int)(h/2.0f);
        }
        //Log.d("INFO","ViewSize=("+w+","+h+") xstep="+xstep+" ystep="+ystep+" xstart="+xstart+"ystart="+ystart);
    }

}
