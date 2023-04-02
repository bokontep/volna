//
// Created by mprizols on 5/26/2022.
//

#ifndef WAVESYNTH_MTWISTER_H
#define WAVESYNTH_MTWISTER_H




#define STATE_VECTOR_LENGTH 624
#define STATE_VECTOR_M      397 /* changes to STATE_VECTOR_LENGTH also require changes to this */

typedef struct tagMTRand {
    unsigned long mt[STATE_VECTOR_LENGTH];
    int index;
} MTRand;

MTRand seedRand(unsigned long seed);
unsigned long genRandLong(MTRand* rand);
double genRand(MTRand* rand);


#endif //WAVESYNTH_MTWISTER_H
