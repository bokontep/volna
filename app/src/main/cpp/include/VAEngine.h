#ifndef VAEngine_h_
#define VAEngine_h_

#include <oboe/Oboe.h>
#include "SynthVoice.h"
#include "Delay.h"
template <int numvoices,int WAVEFORM_COUNT, int WTLEN> class VAEngine: public oboe::AudioStreamCallback
{
  public:
    VAEngine(float* newwaveforms)
    {
      this->waveforms = newwaveforms;

    }
    ~VAEngine()
    {
        if(recBuffer!=NULL)
        delete[] recBuffer;
        if(playBuffer!=NULL)
        delete[] playBuffer;
        if(delay)
        delete delay;
    }
    void init(float sampleRate)
    {
        delay = new Delay(sampleRate);
        delay->SetFeedback(0.5);

        delay->SetDelay(sampleRate);
        delay->Reset();
        SetDelayLevel(0.0);
        recBufferLen = ((int)sampleRate)*60;
        recBuffer = new float[recBufferLen]; // allocate buffers for one minute recording/playback
        playBufferLen = sampleRate*60;
        playBuffer = new float[playBufferLen];
        for(int i=0;i<numvoices;i++)
        {
            for(int j=0;j<WAVEFORM_COUNT;j++)
            {

              mSynthVoice[i].SetSampleRate(sampleRate);
              mSynthVoice[i].AddOsc1SharedWaveTable(WTLEN,&waveforms[j*WTLEN]);
              mSynthVoice[i].AddOsc2SharedWaveTable(WTLEN,&waveforms[j*WTLEN]);
            }
            voices_notes[i]=-1;
        }
        SetupRampUp(0.0,1.0,20);
        SetupRampDown(0.0,1.0,40);
        SetStartTrim(100);
        SetEndTrim(250);
        oboe::AudioStreamBuilder builder;
        // The builder set methods can be chained for convenience.
        builder.setSharingMode(oboe::SharingMode::Exclusive)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setChannelCount(kChannelCount)
                ->setSampleRate(sampleRate)
                ->setFormat(oboe::AudioFormat::Float)
                ->setCallback(this)
                ->openManagedStream(outStream);
        // Typically, start the stream after querying some stream information, as well as some input from the user
        outStream->requestStart();
    }

    void update(void)
    {
        /*
      audio_block_t *block;
      uint32_t i;

      block = allocate();
      
      for (i=0; i < AUDIO_BLOCK_SAMPLES; i++) {
        block->data[i] = Process()*8192;
      }
      transmit(block);
      release(block);
      */
    }
    void onErrorBeforeClose(oboe::AudioStream *oboeStream, oboe::Result error)
    {

    }
    void onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error)
    {
        init(sampleRate);
    }

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                            void* audioData,int32_t numFrames) override
    {
        float *floatData = (float *) audioData;
        for (int i = 0; i < numFrames; ++i) {
            float sampleValue = Process();
            if(play)
            {
                if(playIndex<playIndexEnd) {
                    if(playIndexStart-playIndex<upSamples)
                    {
                        sampleValue = sampleValue + playBuffer[playIndex]*rampUp(playIndex-playIndexStart);
                    }
                    else if(playIndexEnd-playIndex<downSamples)
                    {
                        sampleValue = sampleValue + playBuffer[playIndex]*rampDown(playIndexEnd-downSamples+playIndex);
                    } else
                    {
                        sampleValue = sampleValue + playBuffer[playIndex];
                    }

                    playIndex = (playIndex+1) % playBufferLen;
                    if(playIndex>=playIndexEnd && playIndexStart<playIndexEnd)
                    {
                        playIndex = playIndexStart;
                    }
                }
            }
            wavedata[index]=sampleValue;
            index = (index+1)%256;
            for (int j = 0; j < kChannelCount; j++) {
                floatData[i * kChannelCount + j] = sampleValue;
            }
            if (record)
            {
                if(recIndex<recBufferLen) {
                    recBuffer[recIndex] = sampleValue;
                    recIndex = (recIndex+1) % recBufferLen;
                    recIndexEnd = recIndexEnd+1;

                } else
                {
                    recIndex = 0;
                    recIndexEnd = 0;
                    recIndexStart = 0;
                }
            }
        }
        return oboe::DataCallbackResult::Continue;
    }

    float Process()
    {
      float s=0.0f;
      for (int i = 0; i < numvoices; i++)
      {
        s = s + (mSynthVoice[i].Process() );
      }
      s = s + delayLevel*delay->Process(s);
       return s/((float)numvoices);
	  //return s;
	  
    }
    void handleNoteSpread(int channel, int note, int spread)
    {
        for(int i=0;i<numvoices;i++)
        {
            if(voices_notes[i]==note)
            {
                mSynthVoice[i].noteSpread(note,spread);
            }
        }

    }

    void handleSelectWaveform(uint8_t channel, uint8_t osc, uint8_t note ,uint8_t wave)
    {
        for(int i=0;i<numvoices;i++)
        {
            if(voices_notes[i]==note)
            {
                if(osc==0)
                {
                    mSynthVoice[i].MidiOsc1Wave(wave%256);
                } else {
                    mSynthVoice[i].MidiOsc2Wave(wave%256);
                }
            }
        }
    }

	void handleNoteOn(int channel, int note, int velocity)
    {
      //bool found = false;
      int maxnote = -1;
      int maxnoteidx = -1;
      for (int i = 0; i < numvoices; i++)
      {
        if (voices_notes[i] == -1)
        {
          voices_notes[i] = note;
		  
          mSynthVoice[i].MidiNoteOn(note, velocity);
		  activenotes++;
		  //found = true;
          return;
        }
        if (voices_notes[i] > maxnote)
        {
          maxnote = voices_notes[i];
          maxnoteidx = i;
        }
      }
      voices_notes[maxnoteidx] = note;
      mSynthVoice[maxnoteidx].MidiNoteOn(note, velocity);
	  activenotes++;
    }

    void handleNoteTransition(int channel, int oldnote,int newNote, int velocity)
    {
        for(int i=0;i<numvoices;i++)
        {
            if(voices_notes[i] == oldnote)
            {
                voices_notes[i] = newNote;
                mSynthVoice[i].MidiChangeNote(newNote,velocity);


            }
        }
    }
    void handleNoteOff(int channel, int note, int velocity)
    {
      //digitalWrite(LED, LOW);
      for (int i = 0; i < numvoices; i++)
      {
        if (voices_notes[i] == note)
        {
          voices_notes[i] = -1;
          mSynthVoice[i].MidiNoteOff();
		  activenotes--;
          //break;
        }
      }
    }
    void handlePitchBend(int channel, uint8_t bendlsb, uint8_t bendmsb)
    {
      
      uint16_t bend = bendmsb<<7 | bendlsb;
      for(int i=0;i<numvoices;i++)
      {
        
        mSynthVoice[i].MidiBend(bend);
      }
        
    }
    
	void handleSetTet(int tet)
	{
        this->tet = tet;
		for (int i = 0; i < numvoices; i++)
		{
			mSynthVoice[i].SetTet(tet);
		}
	}
	void handleSetTune(float tune)
	{
		for (int i = 0; i < numvoices; i++)
		{
			mSynthVoice[i].SetTune(tune);
		}
	}
    void handlePitchBend(uint8_t channel, int bend)
    {
      
      for(int i=0;i<numvoices;i++)
      {
        if(mSynthVoice[i].IsPlaying())
        {
          mSynthVoice[i].MidiBend(bend);
        }
      }
      
      
    }

    float rampUp(int sample)
    {
        float ret = upMaxValue;
        if(sample<upSamples)
        {
            ret = upMinValue+sample*upfactor;
        }

        return ret;
    }
    float rampDown(int sample)
    {
        float ret = downMinValue;
        if(sample<downSamples)
        {
            ret = upMaxValue-sample*downfactor;
        }
        return ret;
    }
    void handleSetOctaveFactor(float factor)
    {
        for(int i=0;i<numvoices;i++)
        {
            mSynthVoice[i].SetOctaveFactor(factor);
        }
    }
	void setADSR(uint8_t a, uint8_t d, uint8_t s, uint8_t r)
	{
		for (int i = 0; i < numvoices; i++)
		{
			mSynthVoice[i].SetOsc1ADSR(a,d,s,r);
			mSynthVoice[i].SetOsc2ADSR(a,d,s,r);
		}
	}
	void setOsc1Wave(uint8_t w)
	{
		this->osc1Wave = w;
		for (int i = 0; i < numvoices; i++)
		{
			mSynthVoice[i].MidiOsc1Wave(w);
		}
	}
	uint8_t getOsc1Wave()
	{
		return this->osc1Wave;
	}
	void setOsc2Wave(uint8_t w)
	{
		for (int i = 0; i < numvoices; i++)
		{
			mSynthVoice[i].MidiOsc2Wave(w);
		}
	}
	uint8_t getOsc2Wave()
	{
		return this->osc2Wave;
	}
	void setPwm(uint8_t pwm)
	{
		for (int i = 0; i < numvoices; i++)
		{
			mSynthVoice[i].MidiPwm(pwm);
		}
	}
	
    void handleControlChange(uint8_t channel, uint8_t control, uint8_t value)
    {
      for(int i=0;i<numvoices;i++)
      {
        mSynthVoice[i].ControlChange(channel,control, value);
      }      
    }
    float* getWavedata()
    {
        return wavedata;
    }
    void SetPlay(bool flag)
    {

        if(flag)
        {

            int newStart = recIndexStart+trimStart;
            int newEnd = recIndexEnd-trimEnd;
            if(newStart>=recBufferLen-1)
            {
                newStart = 0;
            }
            if(newEnd<0)
            {
                newEnd = recIndexEnd;
                if(newEnd<0)
                {
                    newEnd = 0;
                }
            }
            int newLen = newEnd-newStart-1;
            memcpy(playBuffer,&recBuffer[newStart],sizeof(float)*(newLen));
            playIndexStart = 0;
            playIndexEnd = newEnd;
            playIndex = 0;
        }
        this->play = flag;
    }
    void SetupRampUp(float upMinValue, float upMaxValue, int ms)
    {
        upSamples = ((float)ms*sampleRate)/1000.0;
        upfactor = (upMaxValue-upMinValue)/upSamples;
        upTimeMs = ms;
    }
    void SetupRampDown(float downMinValue, float downMaxValue, int ms)
    {
        downSamples = ((float)ms*sampleRate/1000.0);
        downfactor = (downMaxValue-downMinValue)/downSamples;
        downTimeMs = ms;
    }
    void SetRecord(bool flag)
    {

        if(flag)
        {
            this->recIndexStart = 0;
            this->recIndexEnd = 0;
            this->recIndex = 0;
        } else
        {
            if(play)
            {
                int newStart = recIndexStart+trimStart;
                int newEnd = recIndexEnd-trimEnd;
                if(newStart>=recBufferLen)
                {
                    newStart = 0;
                }
                if(newEnd<0)
                {
                    newEnd = recBufferLen-1;
                    if(newEnd<0)
                    {
                        newEnd = 0;
                    }
                }
                int newLen = newEnd-newStart-1;
                memcpy(playBuffer,&recBuffer[newStart],sizeof(float)*(newLen));
                playIndexStart = 0;
                playIndexEnd = newEnd;
                playIndex = 0;
            }
        }
        this->record = flag;
    }
    int MillisecondsToSamples(int millis)
    {
        return (int)(((double)millis*sampleRate)/1000.0);
    }
    void SetStartTrim(int ms)
    {
        trimStart = MillisecondsToSamples(ms);
    }
    void SetEndTrim(int ms)
    {
        trimEnd  = MillisecondsToSamples(ms);
    }
    void SetDelayLevel(float level)
    {
        this->delayLevel = level;
    }
    void SetDelayLength(int length)
    {
        delay->SetDelay(length);
    }
    void SetDelayFeedback(float feedback)
    {
        delay->SetFeedback(feedback);
    }
    private:
      SynthVoice mSynthVoice[numvoices];
      int voices_notes[numvoices];
      float* waveforms;
	  int activenotes = 0;
	  int osc1Wave = 0;
	  int osc2Wave = 0;
	  int osc1Vol = 0;
	  int osc2Vol = 0;
	  int osc1Pwm = 0;
	  int osc2Pwm = 0;
	  int tet = 12;
	  oboe::ManagedStream outStream;
	  //Stream params
	  int kChannelCount = 2;
	  int sampleRate = 48000;
	  float wavedata[256];
	  int index = 0;
	  float* recBuffer;
	  int recIndexStart = 0;
	  int recIndexEnd = 0;
	  int recIndex = 0;
	  int recBufferLen = 0;
	  float* playBuffer;
	  int playIndex = 0;
	  int playIndexStart = 0;
	  int playIndexEnd = 0;
	  int playBufferLen = 0;

	  bool play = false;
	  bool record = false;
	  float upMinValue = 0;
	  float upMaxValue = 1.0;
	  float downMinValue = 0;
	  float downMaxValue = 1.0;
	  float upfactor;
	  float upTimeMs;
	  int upSamples;
	  float downfactor;
	  float downTimeMs;
	  int downSamples;
	  int trimEnd = 0;
	  int trimStart = 0;
	  float delayLevel = 0.0f;
	  Delay* delay;
};
#endif
