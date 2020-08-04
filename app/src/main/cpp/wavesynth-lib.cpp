#include <jni.h>
#include <string>
#include <locale>
#include <math.h>
#include <random>
#include "VAEngine.h"
#include <oboe/Oboe.h>
#define WTLEN 256
#define WTCOUNT 256

#define POLYPHONY 10
VAEngine<POLYPHONY,WTCOUNT,WTLEN>* engine;
float Waveforms[WTCOUNT*WTLEN];
std::mt19937 eng{std::random_device()()};
float wavedata[256];
float data[256];
int index = 0;
void initWaveforms(bool bandlimit,float freq,float q)
{
    float sintable[WTLEN];
    float tritable[WTLEN];
    float squtable[WTLEN];
    float sawtable[WTLEN];
    float rndtable[WTLEN];
    LowPass lp;
    lp.SetParameters(freq,q);
    float max = 0.8;
    for (int i = 0; i < WTLEN; i++)
    {
        sintable[i] = max*sin(2.0 * (M_PI / (float)WTLEN) * i);


    }
    if (bandlimit)
    {
        lp.reset();
        for (int i = 0; i < 256; i++)
        {

            sintable[i] = lp.Process(sintable[i]);
        }
    }

    for (int i = 0; i <WTLEN/4;i++)
    {
        tritable[i] = i*(max/(WTLEN / 4));

    }
    for (int i = 0; i<128;i++)
    {
        tritable[i+64] = max*(+1.0-i*(2.0 / ((double)WTLEN / 2)));
    }

    for (int i = 0;i<64;i++)
    {
        tritable[i+128+64] = max*(-1.0 +i*(1.0 / ((double)WTLEN / 2)));
    }
    if(bandlimit)
    {
        lp.reset();
        for(int i=0;i<256;i++)
        {

            tritable[i] = lp.Process(tritable[i]);
        }
    }

    for (int i = 0; i < WTLEN; i++)
    {
        squtable[i] = max*(i < (WTLEN / 2) ? 1.0 : -1.0);
    }
    if (bandlimit)
    {
        lp.reset();
        for (int i = 0; i < WTLEN; i++)
        {
            squtable[i] = lp.Process(squtable[i]);
        }
    }

    for (int i = 0; i < 256; i++)
    {
        sawtable[i] = max*((-1.0 + (2.0 / WTLEN) * i));

    }
    if (bandlimit)
    {
        lp.reset();
        for (int i = 0; i < WTLEN; i++)
        {
            sawtable[i] = lp.Process(sawtable[i]);
        }
    }
    std::uniform_real_distribution<double> dice{-1,1};

    for (int i = 0; i < 256; i++)
    {
        float r = (float)dice(eng);
        rndtable[i] = max*r;


    }
    if (bandlimit)
    {
        lp.reset();
        for (int i = 0; i < WTLEN; i++)
        {
            rndtable[i] = lp.Process(rndtable[i]);
        }
    }
    float sinf = 1.0;
    float trif = 0.0;
    float squf = 0.0;
    float sawf = 0.0;
    float rndf = 0.0;
    float l = (float)WTCOUNT / 5.0;
    int counter = 0;
    float f = (1.0 / (float)l);
    for (int w = 0; w < WTCOUNT; w++)
    {
        for (int i = 0; i < 256; i++)
        {
            Waveforms[w*WTLEN + i] = sinf * sintable[i] + trif * tritable[i] + squf * squtable[i] + sawf * sawtable[i] + rndf * rndtable[i];
        }
        if (counter <= l)
        {
            sinf = sinf - f;
            trif = trif + f;
        }
        else if (counter > l && counter <= 2 * l)
        {
            sinf = 0.0;
            trif = trif - f;
            squf = squf + f;
        }
        else if (counter >= 2 * l && counter < 3 * l)
        {
            trif = 0.0;
            squf = squf - f;
            sawf = sawf + f;
        }
        else if (counter >= 3 * l && counter < 4 * l)
        {
            squf = 0.0;
            sawf = sawf - f;
            rndf = rndf + f;
        }
        else
        {
            sawf = 0.0;
            rndf = rndf - f;
            sinf = sinf + f;
        }
        counter++;
    }




}


extern "C" JNIEXPORT jstring JNICALL
Java_org_bokontep_wavesynth_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_initVAEngine(JNIEnv *env, jobject thiz, jint sample_rate) {
    // TODO: implement initVAEngine()
    initWaveforms(true,0.5,0.6);
    engine = new VAEngine<POLYPHONY,WTCOUNT,WTLEN>(Waveforms);
    engine->init(sample_rate);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_setVAEngineDefaultStreamValues(JNIEnv *env,
                                            jobject thiz,
                                            jint sample_rate,
                                            jint frames_per_burst) {
    // TODO: implement native_setDefaultStreamValues()
    oboe::DefaultStreamValues::SampleRate = (int32_t) sample_rate;
    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) frames_per_burst;
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_sendMidiCC(JNIEnv *env,
                        jobject thiz,
                        jint channel,
                        jint cc,
                        jint data
        )
{
    engine->handleControlChange((uint8_t)channel,(uint8_t)cc,(uint8_t)data);
    return 0;
}



extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_sendMidiNoteOn(
                                                        JNIEnv * env,
                                                        jobject thiz,
                                                        jint channel,
                                                        jint note,
                                                        jint velocity
)
{
    engine->handleNoteOn((uint8_t)channel,(uint8_t)note, (uint8_t)velocity);
return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_sendMidiNoteOff(
        JNIEnv * env,
        jobject thiz,
        jint channel,
        jint note,
        jint velocity
)
{
    engine->handleNoteOff((uint8_t)channel,(uint8_t)note, (uint8_t)velocity);
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_sendMidiNoteSpread(JNIEnv *env, jobject thiz, jint channel,
                                                            jint note, jint spread) {
    engine->handleNoteSpread(channel,note, spread);

    return 0;
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_org_bokontep_wavesynth_MainActivity_getWaveform(JNIEnv* env, jobject thiz)
{
    jfloatArray result;
    result = (*env).NewFloatArray(256);
    //float* data;
    //data=(float*)malloc(sizeof(float)*256);
    for(int i=0;i<256;i++)
    {
        data[i] = engine->getWavedata()[i];
    }
    (*env).SetFloatArrayRegion(result,0,256,data);
    //free(data);
    return result;

}

extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_selectWaveform(JNIEnv *env, jobject thiz, jint channel,
                                                        jint osc, jint note, jint wave) {
    // TODO: implement selectWaveform()
    engine->handleSelectWaveform(channel, osc, note, wave);
    return 0;
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_org_bokontep_wavesynth_MainActivity_getWavetable(JNIEnv *env, jobject thiz, jint index) {
    jfloatArray result;
    result = (*env).NewFloatArray(256);

    (*env).SetFloatArrayRegion(result,0,256,&Waveforms[index*WTLEN]);

    return result;

}

extern "C"
JNIEXPORT jint JNICALL
Java_org_bokontep_wavesynth_MainActivity_setWavetable(JNIEnv *env, jobject thiz, jint index,
                                                      jfloatArray wavetable) {
    jfloat * w = env->GetFloatArrayElements(wavetable,0);
    for(int i=0;i<WTLEN;i++)
    {
        Waveforms[index*WTLEN+i] = w[i];
    }
    env->ReleaseFloatArrayElements(wavetable,w,0);
return 0;
}