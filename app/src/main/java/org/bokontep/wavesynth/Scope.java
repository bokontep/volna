package org.bokontep.wavesynth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Scope extends View {

    private int foregroundColor = Color.GREEN;
    private int backgroundColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaintForeground;
    private Paint drawPaintBackground;
    private Paint markerPaint;
    private float[] data;
    private String text;
    private float xstep = 1.0f;
    private float ystep = 30.0f;
    private float xstart = 0.0f;
    private float ystart = 0.0f;
    private float w = 0f;
    private float h = 0f;
    private String midiLog = "";
    private float settingsPercentage;
    String [] markers = new String[10];
    float[] x_vals = new float[10];
    float[] y_vals = new float[10];
    public static final int STATE_PLAY=0;
    public static final int STATE_SETTINGS=1;
    private int xNoteScale = 160;
    String[] lines;
    public Scope(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        data = new float[256];
        for(int i=0;i<data.length;i++)
        {
            data[i] = 0.0f;
        }
        lines = new String[14];
        setupPaint();
    }
    public void setXNoteScale(int xNoteScale)
    {
        this.xNoteScale = xNoteScale;
    }
    private void setupPaint()
    {
        markerPaint = new Paint();
        markerPaint.setColor(foregroundColor);
        markerPaint.setStrokeWidth(8);
        markerPaint.setStyle(Paint.Style.STROKE);

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
    public void setRed(boolean flag)
    {
        if(flag)
        {
            this.foregroundColor = Color.RED;

        }
        else
        {
            this.foregroundColor = Color.GREEN;
        }
        drawPaintForeground.setColor(this.foregroundColor);
        markerPaint.setColor(this.foregroundColor);
        this.invalidate();
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
                    canvas.drawText(markers[i],x_vals[i],y_vals[i], drawPaintForeground);
                }
            }
        }
        canvas.drawLine(0,0,w,0, drawPaintForeground);
        canvas.drawLine(0,h,w,h, drawPaintForeground);
        canvas.drawLine(0,0,0,h, drawPaintForeground);
        canvas.drawLine(w,0,w,h, drawPaintForeground);
        for(int i=0;i<count;i++)
        {
            canvas.drawLine(i*xNoteScale,0,i*xNoteScale,h, drawPaintForeground);
        }

        for(int i=0;i<data.length-1;i++)
        {

            canvas.drawLine(xstart + i*xstep,ystart-ystep*data[i],xstart+((i+1)*xstep),ystart-ystep*data[i+1], drawPaintForeground);
        }


        canvas.drawArc(0f,500f,100f,600f,0,settingsPercentage*360,true, drawPaintForeground);
        if(text!=null) {
            canvas.drawText(text, 30, 65, drawPaintForeground);
        }
        if(midiLog!=null)
        {
            canvas.drawText(this.midiLog,30, 130,drawPaintForeground);
        }
        if(lines!=null)
        {
            for(int i=0;i<lines.length;i++)
            {
                if(lines[i]!=null) {
                    canvas.drawText(lines[i], 30, 210 + i * 60, drawPaintForeground);
                }
            }
        }
    }
    int index = 0;
    void printLine(String str)
    {
        if(lines==null)
        {
            return;
        }
        if(index<lines.length) {
            lines[index++] = str;
        }
        else
        {
            for(int i=1;i<lines.length;i++)
            {
                lines[i-1] = lines[i];
            }
            lines[lines.length-1] = str;
        }
        invalidate();
    }
    public void setMidilog(String text)
    {
        this.midiLog = text;
    }
    public void setPageText(String[] pagetext)
    {

    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        if(data!=null) {
            xstep = (w / data.length+1);
            ystep = (h/2.0f);
            xstart = 0;
            ystart = (h/2.0f);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;//super.onTouchEvent(event);
    }
}
