package org.bokontep.wavesynth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.MotionEvent.AXIS_Y;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("wavesynth-lib");
    }
    private int[] scales=
            {
                    12, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11, // chromatic
                     7, 0, 2, 4, 5, 7, 9,11, 0, 0, 0, 0, 0, // major
                     7, 0, 2, 3, 5, 7, 8,10, 0, 0, 0, 0, 0, // natural minor
                     7, 0, 2, 3, 5, 7, 8,11, 0, 0, 0, 0, 0, // harmonic minor
                     7, 0, 2, 3, 5, 7, 9,11, 0, 0, 0, 0, 0, // ascending melodic minor
                     8, 0, 2, 4, 6, 7, 9,10,11, 0, 0, 0, 0, // acoustic
                    10, 0, 1, 2, 4, 5, 7, 8, 9,10,11, 0, 0, // major2
                     8, 0, 1, 4, 5, 7, 8,10,11, 0, 0, 0, 0, // minor2
                     5, 0, 3, 5, 7,10, 0, 0, 0, 0, 0, 0, 0, // pentatonic
                     6, 0, 3, 4, 5, 7,10, 0, 0, 0, 0, 0, 0, // blues
                     5, 0, 2, 5, 7, 9, 0, 0, 0, 0, 0, 0, 0, // chinese pentatonic
                     6, 0, 2, 4, 6, 8,10, 0, 0, 0, 0, 0, 0, // whole tone
                     8, 0, 2, 3, 5, 6, 8, 9,11, 0, 0, 0, 0, // whole half
                     8, 0, 1, 3, 4, 6, 7, 9,10, 0, 0, 0, 0  // half whole



            };
    private int updateInterval = 20;
    private Handler mHandler;
    private Runnable screenUpdater;
    private Scope scope;
    private Spinner scaleSpinner;
    private Spinner rootNoteSpinner;
    private SeekBar osc1AttackSeekBar;
    private SeekBar osc1DecaySeekBar;
    private SeekBar osc1SustainSeekBar;
    private SeekBar osc1ReleaseSeekBar;
    private SeekBar osc2AttackSeekBar;
    private SeekBar osc2DecaySeekBar;
    private SeekBar osc2SustainSeekBar;
    private SeekBar osc2ReleaseSeekBar;
    private SeekBar maxSpreadSeekBar;
    private SeekBar osc1WaveSeekBar;
    private SeekBar osc1WaveControlSeekBar;
    private SeekBar osc2WaveSeekBar;
    private SeekBar osc2WaveControlSeekBar;
    private SeekBar gridSizeSeekBar;
    private TextView osc1AttackTextView;
    private TextView osc1DecayTextView;
    private TextView osc1SustainTextView;
    private TextView osc1ReleaseTextView;
    private TextView osc2AttackTextView;
    private TextView osc2DecayTextView;
    private TextView osc2SustainTextView;
    private TextView osc2ReleaseTextView;
    private TextView oscSpreadTextView;
    private WaveDisplay osc1WaveDisplay;
    private WaveDisplay osc2WaveDisplay;

    private TextView osc1WaveTextView;
    private TextView osc2WaveTextView;
    private TextView osc1WaveControlTextView;
    private TextView osc2WaveControlTextView;
    private int rootNote=36;
    private int xNoteScale = 160;
    private int currentScale = 0;
    private HashMap<Integer, Integer> notemap = new HashMap<>();
    private AppPreferences prefs;
    private String[] rootNotes =
            {
                    "C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
                    "C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
                    "C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
                    "C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
                    "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
                    "C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
                    "C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6"

            };
    private String[] scaleNames =
            {
                    "chromatic",
                    "major",
                    "natural minor",
                    "harmonic minor",
                    "ascending melodic minor",
                    "acoustic",
                    "major2",
                    "minor2",
                    "pentatonic",
                    "blues",
                    "chinese pentatonic",
                    "whole tone",
                    "whole half",
                    "half whole"
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        prefs = new AppPreferences(this);
        initAudio();
        osc1Volume = prefs.readInt("osc1Volume",127);
        osc2Volume = prefs.readInt("osc2Volume",127);
        osc1Attack = prefs.readInt("osc1Attack",10);
        osc1Decay = prefs.readInt("osc1Decay",0);
        osc1Sustain = prefs.readInt("osc1Sustain",127);
        osc1Release = prefs.readInt("osc1Release",0);
        osc2Attack = prefs.readInt("osc2Attack",10);
        osc2Decay = prefs.readInt("osc2Decay",0);
        osc2Sustain = prefs.readInt("osc2Sustain",127);
        osc2Release = prefs.readInt("osc2Release",0);
        rootNote=prefs.readInt("rootNote",35);
        xNoteScale = prefs.readInt("xNoteScale",160);
        currentScale = prefs.readInt("currentScale",0);
        maxSpread = prefs.readInt("maxSpread",0);
        osc1Wave = prefs.readInt("osc1Wave",0);
        osc2Wave = prefs.readInt("osc2Wave", 0);
        osc1WaveControl = prefs.readInt("osc1WaveControl",255);
        osc2WaveControl = prefs.readInt("osc2WaveControl",255);
        rootNoteStr = this.midiNoteToString(rootNote);

        setContentView(R.layout.activity_main);
        paint = new Paint();
        paint.setColor(0xffff0000);
        mHandler = new Handler();
        scope = findViewById(R.id.mscope);
        scope.setText(rootNoteStr+" "+scaleNames[currentScale]);
        scope.setXNoteScale(xNoteScale);
        rootNoteSpinner = (Spinner)findViewById(R.id.rootNoteSpinner);
        final ArrayAdapter<String> rootNoteAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,rootNotes);
        rootNoteSpinner.setAdapter(rootNoteAdapter);
        rootNoteSpinner.setSelection(rootNote);
        rootNoteSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position>=0 && position<rootNoteAdapter.getCount())
                        {
                            applySettings();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        final ArrayAdapter<String> scaleAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, scaleNames);
        scaleSpinner = (Spinner)findViewById(R.id.scaleSpinner);
        scaleSpinner.setAdapter(scaleAdapter);

        scaleSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener()
                {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position>=0 && position<scaleAdapter.getCount())
                        {
                            currentScale = position;
                            prefs.writeInt("currentScale",currentScale);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        return;
                    }
                });
        scaleSpinner.setSelection(currentScale);

        this.osc1AttackSeekBar = (SeekBar)findViewById(R.id.osc1AttackSeekBar);
        this.osc1AttackSeekBar.setProgress(this.osc1Attack);
        this.osc1DecaySeekBar = (SeekBar)findViewById(R.id.osc1DecaySeekBar);
        this.osc1DecaySeekBar.setProgress(this.osc1Decay);
        this.osc1SustainSeekBar = (SeekBar)findViewById(R.id.osc1SustainSeekBar);
        this.osc1SustainSeekBar.setProgress(this.osc1Sustain);
        this.osc1ReleaseSeekBar = (SeekBar)findViewById(R.id.osc1ReleaseSeekBar);
        this.osc1ReleaseSeekBar.setProgress(this.osc1Release);
        this.osc2AttackSeekBar = (SeekBar)findViewById(R.id.osc2AttackSeekBar);
        this.osc2AttackSeekBar.setProgress(this.osc2Attack);
        this.osc2DecaySeekBar = (SeekBar)findViewById(R.id.osc2DecaySeekBar);
        this.osc2DecaySeekBar.setProgress(this.osc2Decay);
        this.osc2SustainSeekBar = (SeekBar)findViewById(R.id.osc2SustainSeekBar);
        this.osc2SustainSeekBar.setProgress(this.osc2Sustain);
        this.osc2ReleaseSeekBar = (SeekBar)findViewById(R.id.osc2ReleaseSeekBar);
        this.osc2ReleaseSeekBar.setProgress(this.osc2Release);
        this.maxSpreadSeekBar = (SeekBar)findViewById(R.id.maxSpreadSeekBar);
        this.maxSpreadSeekBar.setProgress(this.maxSpread);
        this.gridSizeSeekBar = (SeekBar)findViewById(R.id.gridSizeSeekBar);
        this.gridSizeSeekBar.setProgress(this.xNoteScale);
        this.osc1WaveSeekBar = (SeekBar)findViewById(R.id.osc1WaveSeekBar);
        this.osc1WaveSeekBar.setProgress(osc1Wave);
        this.osc2WaveSeekBar = (SeekBar)findViewById(R.id.osc2WaveSeekBar);
        this.osc2WaveSeekBar.setProgress(osc2Wave);
        this.osc1WaveControlSeekBar = (SeekBar)findViewById(R.id.osc1WaveControlSeekBar);
        this.osc1WaveControlSeekBar.setProgress(osc1WaveControl);
        this.osc2WaveControlSeekBar = (SeekBar)findViewById(R.id.osc2WaveControlSeekBar);
        this.osc2WaveControlSeekBar.setProgress(osc2WaveControl);
        this.osc1AttackTextView = (TextView)findViewById(R.id.osc1AttackText);
        this.osc1AttackTextView.setText("osc1Attack:"+osc1Attack);
        this.osc1DecayTextView = (TextView)findViewById(R.id.osc1DecayText);
        this.osc1DecayTextView.setText("osc1Decay:"+osc1Decay);
        this.osc1SustainTextView = (TextView)findViewById(R.id.osc1SustainText);
        this.osc1SustainTextView.setText("osc1Sustain:"+osc1Sustain);
        this.osc1ReleaseTextView = (TextView)findViewById(R.id.osc1ReleaseText);
        this.osc1ReleaseTextView.setText("osc1Release:"+osc1Release);
        this.osc2AttackTextView = (TextView)findViewById(R.id.osc2AttackText);
        this.osc2AttackTextView.setText("osc2Attack:"+osc2Attack);
        this.osc2DecayTextView = (TextView)findViewById(R.id.osc2DecayText);
        this.osc2DecayTextView.setText("osc2Decay:"+osc2Decay);
        this.osc2SustainTextView = (TextView)findViewById(R.id.osc2SustainText);
        this.osc2SustainTextView.setText("osc2Sustain:"+osc2Sustain);
        this.osc2ReleaseTextView = (TextView)findViewById(R.id.osc2ReleaseText);
        this.osc2ReleaseTextView.setText("osc2Release:"+osc2Release);

        this.oscSpreadTextView = (TextView)findViewById(R.id.oscSpreadText);
        this.oscSpreadTextView.setText("oscSpread:"+maxSpread);
        this.osc1WaveTextView = (TextView)findViewById(R.id.osc1WaveText);
        this.osc1WaveTextView.setText("osc1Wave:"+osc1Wave);
        this.osc2WaveTextView = (TextView)findViewById(R.id.osc2WaveText);
        this.osc2WaveTextView.setText("osc2Wave:"+osc2Wave);
        this.osc1WaveControlTextView = (TextView)findViewById(R.id.osc1WaveControlText);
        this.osc1WaveControlTextView.setText("osc1WaveControl:"+osc1WaveControl);
        this.osc2WaveControlTextView = (TextView)findViewById(R.id.osc2WaveControlText);
        this.osc2WaveControlTextView.setText("osc2WaveControl:"+osc2WaveControl);
        this.osc1WaveDisplay = (WaveDisplay)findViewById(R.id.osc1WaveDisplay);
        this.osc1WaveDisplay.setData(this.getWavetable(osc1Wave));
        this.osc1WaveDisplay.invalidate();
        this.osc2WaveDisplay = (WaveDisplay)findViewById(R.id.osc2WaveDisplay);
        this.osc2WaveDisplay.setData(this.getWavetable(osc2Wave));
        this.osc2WaveDisplay.invalidate();
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!fromUser)
                    return;
                switch (seekBar.getId())
                {
                    case R.id.osc1AttackSeekBar:
                        osc1Attack = progress;
                        osc1AttackTextView.setText("osc1Attack:"+osc1Attack);
                        prefs.writeInt("osc1Attack",osc1Attack);
                        sendMidiCC(0,18,osc1Attack);
                        break;
                    case R.id.osc1DecaySeekBar:
                        osc1Decay = progress;
                        osc1DecayTextView.setText("osc1Decay:"+osc1Decay);
                        prefs.writeInt("osc1Decay",osc1Decay);
                        sendMidiCC(0,19,osc1Decay);
                        break;
                    case R.id.osc1SustainSeekBar:
                        osc1Sustain = progress;
                        osc1SustainTextView.setText("osc1Sustain:"+osc1Sustain);
                        prefs.writeInt("osc1Sustain",osc1Sustain);
                        sendMidiCC(0,20,osc1Sustain);
                        break;
                    case R.id.osc1ReleaseSeekBar:
                        osc1Release = progress;
                        osc1ReleaseTextView.setText("osc1Release:"+osc1Release);
                        prefs.writeInt("osc1Release",osc1Release);
                        sendMidiCC(0,21,osc1Release);
                        break;
                    case R.id.osc2AttackSeekBar:
                        osc2Attack = progress;
                        osc2AttackTextView.setText("osc2Attack:"+osc2Attack);
                        prefs.writeInt("osc2Attack",osc2Attack);
                        sendMidiCC(0,22,osc2Attack);
                        break;
                    case R.id.osc2DecaySeekBar:
                        osc2Decay = progress;
                        osc2DecayTextView.setText("osc2Decay:"+osc2Decay);
                        prefs.writeInt("osc2Decay",osc2Decay);

                        sendMidiCC(0,23,osc2Decay);
                        break;
                    case R.id.osc2SustainSeekBar:
                        osc2Sustain = progress;
                        osc2SustainTextView.setText("osc2Sustain:"+osc2Sustain);
                        prefs.writeInt("osc2Sustain",osc2Sustain);


                        sendMidiCC(0,24,osc2Sustain);
                        break;
                    case R.id.osc2ReleaseSeekBar:
                        osc2Release = progress;
                        osc2ReleaseTextView.setText("osc2Release:"+osc2Release);
                        prefs.writeInt("osc2Release",osc2Release);

                        sendMidiCC(0,25,osc2Release);
                        break;
                    case R.id.maxSpreadSeekBar:
                        maxSpread = progress;
                        oscSpreadTextView.setText("oscSpread:"+maxSpread);
                        prefs.writeInt("maxSpread",maxSpread);
                        break;

                    case R.id.osc1WaveSeekBar:
                        osc1Wave = progress;
                        osc1WaveDisplay.setData(getWavetable(osc1Wave));

                        osc1WaveTextView.setText("osc1Wave:"+osc1Wave);
                        osc1WaveDisplay.invalidate();
                        prefs.writeInt("osc1Wave",osc1Wave);

                        break;
                    case R.id.osc2WaveSeekBar:
                        osc2Wave = progress;
                        osc2WaveDisplay.setData(getWavetable(osc2Wave));

                        osc2WaveTextView.setText("osc2Wave:"+osc2Wave);
                        osc2WaveDisplay.invalidate();
                        prefs.writeInt("osc2Wave",osc2Wave);
                        break;
                    case R.id.osc1WaveControlSeekBar:
                        osc1WaveControl = progress;
                        osc1WaveControlTextView.setText("osc1WaveControl:"+osc1WaveControl);

                        prefs.writeInt("osc1WaveControl",osc1WaveControl);
                        break;
                    case R.id.osc2WaveControlSeekBar:
                        osc2WaveControl = progress;
                        osc2WaveControlTextView.setText("osc2WaveControl:"+osc2WaveControl);

                        prefs.writeInt("osc2WaveControl",osc2WaveControl);
                    break;
                    case R.id.gridSizeSeekBar:
                        xNoteScale = progress;
                        prefs.writeInt("xNoteScale",xNoteScale);
                        scope.setXNoteScale(xNoteScale);
                        scope.invalidate();
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        this.osc1AttackSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc1DecaySeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc1SustainSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc1ReleaseSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc2AttackSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc2DecaySeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc2SustainSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc2ReleaseSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.maxSpreadSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.gridSizeSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);                                                                                                                                                                                                                                                                                      ;
        this.osc1WaveSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc2WaveSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc1WaveControlSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        this.osc2WaveControlSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        findViewById(R.id.closeSettingsButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //applySettings();
                        findViewById(R.id.optionsScrollView).setVisibility(View.GONE);
                    }
                }

        );

        screenUpdater = new Runnable() {
            @Override
            public void run() {
                scope.setData(getWaveform());
                scope.invalidate();
                mHandler.postDelayed(screenUpdater,updateInterval);
            }
        };



        mHandler.postDelayed(screenUpdater,updateInterval);

    }
    public void initAudio()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            AudioManager myAudioMgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int defaultSampleRate = Integer.parseInt(sampleRateStr);
            String framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr);

            setVAEngineDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst);
            initVAEngine(defaultSampleRate);

            sendMidiCC(0,18,osc1Attack);
            sendMidiCC(0,19,osc1Decay);
            sendMidiCC(0,20,osc1Sustain);
            sendMidiCC(0,21,osc1Release);
            sendMidiCC(0,22,osc2Attack);
            sendMidiCC(0,23,osc2Decay);
            sendMidiCC(0,24,osc2Sustain);
            sendMidiCC(0,25,osc2Release);


        }
    }
    public String midiNoteToString(int note)
    {
        note = note%12;
        switch(note)
        {
            case 0:
                return "C";

            case 1:
                return "C#";
            case 2:
                return "D";
            case 3:
                return "D#";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "F#";
            case 7:
                return "G";
            case 8:
                return "G#";
            case 9:
                return "A";
            case 10:
                return "A#";
            case 11:
                return "B";
        }
        return "";
    }
    public int transformNote(int noteIn)
    {

        int notesInScale = scales[currentScale*13];
        int relnote = noteIn-rootNote;
        int octave = relnote/notesInScale;
        int noteInScale = relnote%notesInScale;
        int index = currentScale*13+1+noteInScale;
        int noteOut = -1;
        if(scales==null)
        {
            return 0;
        }
        if(index<scales.length && index>=0)
        {
            noteOut = this.rootNote+scales[index]+octave*12;
        }
        else
        {
            while(index>=scales.length)
            {
                index = index - 12;
            }
            if(index<0)
            {
                index = 0;
            }
            noteOut = this.rootNote+scales[index]+octave*12;
        }

        return noteOut%128;
    }
    public void applySettings()
    {
        String rootNote = (String)rootNoteSpinner.getSelectedItem();
        int note = 0;
        if(rootNote!=null)
        {
            if(rootNote.length()>0)
            {
                if(rootNote.indexOf("C#")>=0)
                {
                    note = 1;
                }
                else if(rootNote.indexOf("C")>=0)
                {
                    note=0;
                }
                else if(rootNote.indexOf("D#")>=0)
                {
                    note = 3;
                }
                else if(rootNote.indexOf("D")>=0)
                {
                    note = 2;
                }
                else if(rootNote.indexOf("E")>=0)
                {
                    note=4;
                }
                else if(rootNote.indexOf("F#")>=0)
                {
                    note = 6;
                }
                else if(rootNote.indexOf("F")>=0)
                {
                    note = 5;
                }
                else if(rootNote.indexOf("G#")>=0)
                {
                    note = 8;
                }
                else if(rootNote.indexOf("G")>=0)
                {
                    note = 7;
                }
                else if(rootNote.indexOf("A#")>=0)
                {
                    note = 10;
                }
                else if(rootNote.indexOf("G")>=0)
                {
                    note = 9;
                }
                else if(rootNote.indexOf("B")>=0)
                {
                    note = 11;
                }


            }
            int octave = Integer.parseInt(rootNote.substring(rootNote.length()-1));

            this.rootNote = 12*octave+note;
            rootNoteStr = midiNoteToString(this.rootNote);
            prefs.writeInt("rootNote",this.rootNote);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int index = event.getActionIndex();
        //int id = event.getPointerId(index);
        //float x=-1;
        //float y=-1;
        float height = event.getDevice().getMotionRange(AXIS_Y).getRange();

        int activepointers = event.getPointerCount()%10; // do not track more than 10
        float[] x = new float[10];
        float[] y = new float[10];
        for(int i=0;i<10;i++)
        {
            if(i<activepointers)
            {
                x[i]=event.getX(i);
                y[i]=event.getY(i);
            }
            else
            {
                x[i]=-1;
                y[i]=-1;
            }
        }

        if(activepointers==1)
        {
            if(event.getAction()==ACTION_DOWN) {
                if (x[0] < 100 && (y[0] < 100) && enterSettings == 0) {
                    enterSettings = enterSettings + new Date().getTime();
                }
                else
                {
                    enterSettings = 0;
                }
            }

        }

        String scopetext = rootNoteStr+" "+scaleNames[currentScale]+" notes:";
        int offset = 0;
        //Log.d("TEST","x="+x+" y="+y);
        lastnote=-1;
        for(int i=0;i<activepointers;i++) {

            if (y[i] > 2.0 * height / 3.0f) {
                offset = 0;
            } else if (y[i] < height / 3.0f) {
                offset = 24;
            } else {
                offset = 12;
            }
            int id = event.getPointerId(i);
            int oscdist =  -((int)(((xNoteScale-x[i]%xNoteScale )/xNoteScale)*127)-63);
            scopetext = scopetext+"["+oscdist+"]";
            int midinote = (rootNote  + ((int) x[i] / xNoteScale)) % 128;
            midinote = (transformNote(midinote)+offset)%128;
            int factor = (int) height/3;
            int wi = (int)y[i]%factor;

            int waveform1 =  (this.osc1Wave + (wi*this.osc1WaveControl)/factor)%256;
            int waveform2 =  (this.osc2Wave + (wi*this.osc2WaveControl)/factor)%256;
            selectWaveform(0,0,midinote,waveform1);
            selectWaveform(0,1,midinote,waveform2);
            /*
            int waveform = ((int) (y[i] * 1.1f)) % 256;

            sendMidiCC(0, 16, waveform);
            sendMidiCC(0, 17, waveform);

             */
            int tmp=((int)(127.0*event.getPressure(i)*4));
            vel=tmp>127?127:tmp;
            scopetext=scopetext+" "+midinote;
            if(vel>=0)
            {
                scopetext = scopetext+"("+vel+")";
            }
            scope.setMarker("" + id, x[i], y[i]);
            Integer last  = notemap.get(id);
            if(last!=null)
            {
                lastnote = last.intValue();
            }
            else
            {
                lastnote = -1;
            }
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {

                if (lastnote >= 0) {

                    sendMidiNoteOff(0, lastnote, 0);
                    notemap.remove(id);
                }

                sendMidiNoteOn(0, midinote, vel);
                selectWaveform(0,0,midinote,waveform1);
                selectWaveform(0,1,midinote,waveform2);
                lastnote = midinote;
                notemap.put(id, lastnote);

            }


            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                if (lastnote >= 0) {
                    sendMidiNoteOff(0, lastnote, 0);
                }
                scope.unsetMarker("" + id);
                lastnote = -1;
                notemap.remove(id);
            }
            if (action == MotionEvent.ACTION_MOVE) {

                if (midinote != lastnote) {
                    sendMidiNoteOff(0, lastnote, 0);
                    notemap.remove(id);
                    int offset1 = 64-oscdist;
                    int offset2 = 64+oscdist;
                    //scopetext = scopetext+"["+oscdist+"]";
                    if(offset1<0 || offset1>127)
                    {
                        offset1 = 0;
                    }
                    if(offset2>127 || offset2<0)
                    {
                        offset2 = 127;
                    }

                    sendMidiNoteOn(0, midinote, vel);
                    selectWaveform(0,0,midinote,waveform1);
                    selectWaveform(0,1,midinote,waveform2);
                    lastnote = midinote;
                    notemap.put(id, lastnote);
                }
                else
                {
                    float spreadFactor = (float) (maxSpread/127.0);
                    oscdist = (int)(spreadFactor*oscdist);
                    sendMidiNoteSpread(0,midinote,63+oscdist);
                }


            }
        }
        scope.setText(scopetext);
        long now = new Date().getTime();
        if(enterSettings>0)
        {
            float p = (float)((double)(now-enterSettings)/(double)settingsPressTime);
            scope.setSettingsPercentage(p);
            if(p>1.0)
            {
                enterSettings=0;
                findViewById(R.id.optionsScrollView).setVisibility(View.VISIBLE);
            }
        }
        else
        {
            scope.setSettingsPercentage(0);

        }

        return super.onTouchEvent(event);
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private Paint paint;
    private int lastnote=-1;
    private int vel =-1;
    public native String stringFromJNI();
    public native int initVAEngine(int sampleRate);
    public native int setVAEngineDefaultStreamValues(int sampleRate, int framesPerBurst);
    public native int sendMidiCC(int channel, int cc, int data);
    public native int sendMidiNoteOn(int channel, int note, int velocity );
    public native int sendMidiNoteOff(int channel, int note, int velocity );
    public native int sendMidiNoteSpread(int channel, int note, int spread);
    public native int selectWaveform(int channel, int osc, int note,  int wave);
    public native float[] getWaveform();
    public native float[] getWavetable(int index);
    public native int setWavetable(int index, float[] wavetable);
    public long enterSettings;
    private int osc1Volume = 127;
    private int osc2Volume = 127;
    private int osc1Attack = 10;
    private int osc1Decay = 0;
    private int osc1Sustain = 127;

    private int osc1Release = 0;
    private int osc2Attack = 10;
    private int osc2Decay = 0;
    private int osc2Sustain = 127;
    private int osc2Release = 0;
    private int maxSpread = 0;
    private int osc1Wave = 0;
    private int osc1WaveControl = 0;
    private int osc2Wave = 0;
    private int osc2WaveControl = 0;

    private long settingsPressTime=5000;
    private String rootNoteStr = "";
}
