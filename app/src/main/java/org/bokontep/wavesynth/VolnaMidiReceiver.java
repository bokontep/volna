package org.bokontep.wavesynth;

import android.media.midi.MidiReceiver;
import android.util.Log;

import org.bokontep.midi.MidiConstants;

import java.io.IOException;

public class VolnaMidiReceiver extends MidiReceiver {
    public static final String TAG="VolnaMidiReceiver";
    private SynthEngine engine;

    public VolnaMidiReceiver(SynthEngine engine)
    {
        this.engine = engine;
    }

    @Override
    public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException {
        String s = "";

        for(int i=offset;i<offset+count;i++)
        {
            s = s + " data["+i+"]="+(int)((int)data[i]&0xFF);
        }

        int command = ((int)(data[offset]&0xFF)) &  MidiConstants.STATUS_COMMAND_MASK;
        Log.d(TAG,s);
        int channel =  ((int)data[offset]&0xFF) & MidiConstants.STATUS_CHANNEL_MASK;
        if (data.length>=3)
        {
            Log.d(TAG,"command:"+command+" channel:"+channel+ " data1:"+(data[offset+1]&0b1111111)+" data2:"+(data[offset+2]&0b1111111));
        }
        //parent.logMidi(data);
        switch (command) {
            case MidiConstants.STATUS_NOTE_OFF:
                Log.d(TAG,"noteOff:"+(data[offset+1]&0b1111111));
                engine.sendMidiNoteOff(channel, data[offset+1]&0b1111111, data[offset+2]&0b1111111);
                break;
            case MidiConstants.STATUS_NOTE_ON:
                Log.d(TAG,"noteOn :"+(data[offset+1]&0b1111111));
                engine.sendMidiNoteOn(channel, data[offset+1]&0b1111111, data[offset+2]&0b1111111);
                break;
            case MidiConstants.STATUS_PITCH_BEND:

                engine.sendMidiBend(channel,data[offset+1],data[offset+2]);
                //parent.sendMidiCC(channel, bend);
                break;
            case MidiConstants.STATUS_PROGRAM_CHANGE:
                //NOT implemented!
                break;
            case MidiConstants.STATUS_CONTROL_CHANGE:
                engine.sendMidiCC(channel, data[offset+1],data[offset+2]);
                break;
            default:
                //logMidiMessage(data, offset, count);
                break;
        }
    }
}
