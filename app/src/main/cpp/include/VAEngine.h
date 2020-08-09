#ifndef VAEngine_h_
#define VAEngine_h_

#include <oboe/Oboe.h>
#include "SynthVoice.h"
 
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
        delete recBuffer;
        if(playBuffer!=NULL)
        delete playBuffer;
    }
    void init(float sampleRate)
    {
        recBufferLen = sampleRate*60;
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


    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                            void* audioData,int32_t numFrames) override
    {
        float *floatData = (float *) audioData;
        for (int i = 0; i < numFrames; ++i) {
            float sampleValue = Process();
            if(play)
            {
                if(playIndex<playIndexEnd) {
                    sampleValue = sampleValue + playBuffer[playIndex];
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
       return s/((float)numvoices);
	  //return s;
	  
    }
    void handleNoteSpread(uint8_t channel, uint8_t note, uint8_t spread)
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

	void handleNoteOn(uint8_t channel, uint8_t note, uint8_t velocity)
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
    
    void handleNoteOff(uint8_t channel, uint8_t note, uint8_t velocity)
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
    void handlePitchBend(uint8_t channel, uint8_t bendlsb, uint8_t bendmsb)
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
            memcpy(playBuffer,recBuffer,sizeof(float)*recBufferLen);
            playIndexStart = recIndexStart;
            playIndexEnd = recIndexEnd;
            playIndex = recIndexStart;
        }
        this->play = flag;
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
                memcpy(playBuffer,recBuffer,sizeof(float)*recBufferLen);
                playIndexStart = recIndexStart;
                playIndexEnd = recIndexEnd;
                playIndex = recIndexStart;
            }
        }
        this->record = flag;
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
};
#endif
