package org.bokontep.wavesynth;

import android.content.Context;
import android.media.AudioManager;
import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;
import android.util.Log;

public class VolnaMidiDeviceService extends MidiDeviceService {
    private static final String TAG = MainActivity.TAG;
    private boolean mSynthStarted = false;
    private static VolnaMidiDeviceService mInstance;

    private static VolnaMidiReceiver midiReceiver;
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        Log.d(TAG,"VolnaMidiDeviceServiceCreation");
    }

    public static void setMidiReceiver(VolnaMidiReceiver receiver)
    {
        Log.d(TAG,"setting midi receiver");
        midiReceiver = receiver;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers()
    {
        Log.d(TAG,"onGetInputPortReceivers");
        return new MidiReceiver[] { midiReceiver  };
    }

    /**
     * This will get called when clients connect or disconnect.
     */
    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        if (status.isInputPortOpen(0) && !mSynthStarted) {

            mSynthStarted = true;
        } else if (!status.isInputPortOpen(0) && mSynthStarted){
            mSynthStarted = false;
        }
    }




}
