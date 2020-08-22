//
// Created by user on 22/8/2020.
//

#ifndef WAVESYNTH_DELAY_H
#define WAVESYNTH_DELAY_H


class Delay {
public:
    Delay(int length)
    {
        this->len = length;
        buffer = new float[length];
    }

    ~Delay()
    {
        delete [] buffer;
    }
    void SetDelay(int delaytime)
    {
        if(delaytime<=len) {
            this->dtime = delaytime;

        }
        else{
            this->dtime = len;
        }

    }
    void SetFeedback(float feedback)
    {
        if(feedback>0.99)
        {
            this->feedback = 0.99;
        } else if(feedback<0)
        {
            this->feedback = 0.0f;
        }
        else
        {
            this->feedback = feedback;
        }
    }
    void Reset()
    {
        for(int i=0;i<len;i++)
        {
            buffer[i] = 0.0f;
        }
        idx = 0;
    }
    float Process(float in)
    {
        float out = buffer[idx];
        buffer[idx++] = in+out*feedback;
        if(idx>=dtime)
            idx = 0;
        return out;

    }
private:
    float* buffer;
    int len;
    float feedback;
    int dtime;
    int idx;

};


#endif //WAVESYNTH_DELAY_H
