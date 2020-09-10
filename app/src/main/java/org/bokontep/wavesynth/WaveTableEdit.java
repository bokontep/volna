package org.bokontep.wavesynth;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class WaveTableEdit extends View {
    private SynthEngine engine = null;
    private int w = 0;
    private int h = 0;
    private WaveDisplay[] waveDisplays = null;
    private WaveDisplay drawDisplay = null;
    int selectedIndex = -1;
    int displayWaveHeight = 0;
    int displayWaveItemWidth = 0;
    public WaveTableEdit(Context context, AttributeSet attrs,SynthEngine engine) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        waveDisplays = new WaveDisplay[16];
        setSynthEngine(engine);
        for(int i=0;i<waveDisplays.length;i++)
        {
            waveDisplays[i] = new WaveDisplay(this.getContext(),attrs);
            waveDisplays[i].setData(engine.getWavetable(i));
        }
    }
    public void setSynthEngine(SynthEngine engine)
    {
        this.engine = engine;

    }
    public SynthEngine getSynthEngine()
    {
        return this.engine;
    }
    @Override
    protected void onDraw(Canvas canvas) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;

        this.h = h;
        displayWaveHeight = h/4;
        displayWaveItemWidth = w/(waveDisplays.length+2);
        for(int i=0;i<waveDisplays.length;i++)
        {
            waveDisplays[i].setLayoutParams(new WindowManager.LayoutParams(displayWaveHeight,displayWaveItemWidth));
        }
    }

}
