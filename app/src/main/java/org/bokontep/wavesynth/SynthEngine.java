package org.bokontep.wavesynth;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

public class SynthEngine {
    static {
        System.loadLibrary("wavesynth-lib");
    }
    private int sampleRate;
    public SynthEngine(Context context,int sampleRate)
    {
        this.context = context;
        this.setSampleRate(sampleRate);
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public void initAudio()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int defaultSampleRate = Integer.parseInt(sampleRateStr);
            String framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr);

            setVAEngineDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst);
            initVAEngine(sampleRate);



        }
    }
    public void initSynthParameters()
    {
        sendMidiCC(0,18, getOsc1Attack());
        sendMidiCC(0,19, getOsc1Decay());
        sendMidiCC(0,20, getOsc1Sustain());
        sendMidiCC(0,21, getOsc1Release());
        sendMidiCC(0,22, getOsc2Attack());
        sendMidiCC(0,23, getOsc2Decay());
        sendMidiCC(0,24, getOsc2Sustain());
        sendMidiCC(0,25, getOsc2Release());
        sendMidiCC(0,79, getNoiseAttack());
        sendMidiCC(0,80, getNoiseDecay());
        sendMidiCC(0,81,getNoiseSustain());
        sendMidiCC(0,82,getNoiseRelease());

    }
    private Context context;
    private int osc1Volume = 127;
    private int osc1Attack = 10;
    private int osc1Decay = 0;
    private int osc1Sustain = 127;
    private int osc1Release = 0;
    private int osc2Volume = 127;
    private int osc2Attack = 10;
    private int osc2Decay = 0;
    private int osc2Sustain = 127;
    private int osc2Release =0;
    private int noiseVolume = 0;
    private int noiseAttack = 0;
    private int noiseDecay = 0;
    private int noiseSustain =0;
    private int noiseRelease = 0;
    public native int initVAEngine(int sampleRate);
    public native int setVAEngineDefaultStreamValues(int sampleRate, int framesPerBurst);
    public native int sendMidiCC(int channel, int cc, int data);
    public native int sendMidiNoteOn(int channel, int note, int velocity );
    public native int sendMidiNoteOff(int channel, int note, int velocity );
    public native int sendMidiBend(int channel, int low, int high);
    public native int sendMidiNoteSpread(int channel, int note, int spread);
    public native int sendMidiChangeNote(int channel, int oldnote, int newnote, int velocity);
    public native int selectWaveform(int channel, int osc, int note,  int wave);
    public native float[] getWaveform();
    public native float[] getWavetable(int index);
    public native int setWavetable(int index, float[] wavetable);
    public native int setTet(int newTet);
    public native int setTune(float newTune);
    public native int setOctaveFactor(float factor);
    public native int setRecord(boolean flag);
    public native int setPlay(boolean flag);
    public native int setDelayLevel(int level);
    public native int setDelayTime(int time);
    public native int setDelayFeedback(int feedback);
    public native int setOsc1Volume(int osc1Volume);
    public native int setOsc2Volume(int osc2Volume);
    public native int setNoiseVolume(int noiseVolume);
    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getOsc1Volume() {
        return osc1Volume;
    }



    public int getOsc1Attack() {
        return osc1Attack;
    }

    public void setOsc1Attack(int osc1Attack) {
        this.osc1Attack = osc1Attack;
    }

    public int getOsc1Decay() {
        return osc1Decay;
    }

    public void setOsc1Decay(int osc1Decay) {
        this.osc1Decay = osc1Decay;
    }

    public int getOsc1Sustain() {
        return osc1Sustain;
    }

    public void setOsc1Sustain(int osc1Sustain) {
        this.osc1Sustain = osc1Sustain;
    }

    public int getOsc1Release() {
        return osc1Release;
    }

    public void setOsc1Release(int osc1Release) {
        this.osc1Release = osc1Release;
    }

    public int getOsc2Volume() {
        return osc2Volume;
    }


    public int getOsc2Attack() {
        return osc2Attack;
    }

    public void setOsc2Attack(int osc2Attack) {
        this.osc2Attack = osc2Attack;
    }

    public int getOsc2Decay() {
        return osc2Decay;
    }

    public void setOsc2Decay(int osc2Decay) {
        this.osc2Decay = osc2Decay;
    }

    public int getOsc2Sustain() {
        return osc2Sustain;
    }

    public void setOsc2Sustain(int osc2Sustain) {
        this.osc2Sustain = osc2Sustain;
    }

    public int getOsc2Release() {
        return osc2Release;
    }

    public void setOsc2Release(int osc2Release) {
        this.osc2Release = osc2Release;
    }

    public void setNoiseAttack(int noiseAttack)
    {
        this.noiseAttack = noiseAttack;
    }
    public void setNoiseDecay(int noiseDecay)
    {
        this.noiseDecay = noiseDecay;
    }
    public void setNoiseSustain(int noiseSustain)
    {
        this.noiseSustain = noiseSustain;
    }
    public void setNoiseRelease(int noiseRelease)
    {
        this.noiseRelease = noiseRelease;
    }
    public int getNoiseAttack(){return noiseAttack;}
    public int getNoiseDecay(){return noiseDecay;}
    public int getNoiseSustain(){return noiseSustain;}
    public int getNoiseRelease(){return noiseRelease;}
}
