# volna - wavetable touch synthesizer for android

For a prebuilt apk you can install on your android device, go to the releases.
https://github.com/bokontep/volna/releases/tag/v0.5

волна (pronounced volna in English) means wave in Russian. It is a wavetable synthesizer/musical instrument presented as an android application. I made it in order to test some ideas on audio synthesis and ui interface design.
It utilises a 10 voice architecture with 2 oscillators per voice. The 2 oscillators are wavetable oscillators. The sound is produced using a table with 256 waveforms of 256 samples each.
The samples are sine, triangle, square, saw and noise, smootly interpolated and low pass filtered in order to reduce aliasing.
You can play the instrument simply by touching on the screen. The application supports multitouch and variable pressure (not all phones support pressure though!). The screen is presented
in landscape. By sliding your finger on the x axis you select the pitch, and by sliding your finger on the y axis you select the wavetable and octave. On the lower part of the screen,
we have lower pitched notes, on the middle, higher pitched notes and on the upper part of the screen we have the highest pitched notes. By long pressing on the upper left part of the screen for five
seconds the settings menu is displayed. The five second delay is for avoiding to open settings by mistake. On the settings menu, we can select the root note from C0 to B6 and the scale used.
Currently the synth supports chromatic, major and minor scales as well as pentatonic and blues scales, as well as some more. You can also set the ADSR envelopes of the two oscillators and you can also select
the grid size that determines how much you should slide your finger in the horizontal axis in order to change the note played.
As you play, a waveform of the currently playing sound is displayed on screen.
Below you can see an image of the program in action:
![Image of volna program in action](./images/volna01.png?raw=true)

And you can see a short demo in this youtube video:
https://youtu.be/Cf_wpDYhudk

How to download code and compile application:

Open command prompt

Clone this repository: git clone https://github.com/bokontep/volna.git

Change to volna directory: cd volna

Init submodules: git submodule init

Fetch submodules: git submodule update

Now you are ready to build. Open the project with Android Studio (Version 4.0 should work) and build your project. You should have NDK installed.

# Technical info
Volna uses the Google oboe library for low latency audio. The GUI is java but all the audio functions are implemented in C++ based on previous work done in my other repositories like
https://github.com/bokontep/ofsynth
Currently I test this application on a Xiaomi Mi A2 Lite (It supports pressure!) and a Samsung Galaxy 7 edge (No pressure sensitivity). I would recommend a fairly recent device because the application is quite cpu heavy.
Maybe someday I will release this on Google Play, who knows...
Enjoy and as always peace...
