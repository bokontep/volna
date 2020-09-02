# volna - wavetable touch synthesizer for android

(for greek text, scroll down)

For a prebuilt apk you can install on your android device, go to the releases.
https://github.com/bokontep/volna/releases/tag/v0.8c

волна (pronounced volna in English) means wave in Russian. It is a wavetable synthesizer/musical instrument presented as an android application. I made it in order to test some ideas on audio synthesis and ui interface design.
It utilises a 10 voice architecture with 2 oscillators per voice. The 2 oscillators are wavetable oscillators. The sound is produced using a table with 256 waveforms of 256 samples each.

The samples are sine, triangle, square, saw and noise, smootly interpolated and low pass filtered in order to reduce aliasing.
![Image of volna wavetables](./images/volnawavetable.gif?raw=true)

You can play the instrument simply by touching on the screen. The application supports multitouch and variable pressure (not all phones support pressure though!). The screen is presented
in landscape. By sliding your finger on the x axis you select the pitch, and by sliding your finger on the y axis you select the wavetable and octave. On the lower part of the screen,
we have lower pitched notes, on the middle, higher pitched notes and on the upper part of the screen we have the highest pitched notes. By pressing the SET button, the settings menu is displayed.  On the settings menu, we can select the root note from C0 to B6 and the scale used.
Currently the synth supports chromatic, major and minor scales as well as pentatonic and blues scales, as well as some more. You can also set the ADSR envelopes of the two oscillators and you can also select
the grid size that determines how much you should slide your finger in the horizontal axis in order to change the note played.
As you play, a waveform of the currently playing sound is displayed on screen.
Below you can see an image of the program in action:
![Image of volna program in action](./images/volna0.8c.png?raw=true)

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


# Βόλνα, πολυφωνικό-πολυδειγματικό συνθεσάϊζερ για android

Το волна (βόλνα) σημαίνει κύμα στα Ρώσικα και είναι ένα συνθεσάϊζερ που δουλεύει σε κινητά και τάμπλετ με μια σχετικά πρόσφατη έκδοση android (το έχω δοκιμάσει σε Android 8 και Android 10). Η αρχιτεκτονική του είναι πολυφωνική (10 φωνές, όσα και τα δάχτυλα των περισσοτέρων από εμάς). Κάθε φωνή έχει 2 ταλαντωτές οι οποίοι έχουν 256 κυματομορφές. Οι κυματομορφές είναι ημίτονο, τριγωνική, τετραγωνική, πριονωτή και θόρυβος μιξαρισμένες μεταξύ τους και φιλτραρισμένες με ένα χαμηλοπερατό φίλτρο προκειμένου να περιοριστεί το aliasing. Στην εικόνα μπορείτε να δείτε τις κυματομορφές:
![Image of volna wavetables](./images/volnawavetable.gif?raw=true)
Για να παίξετε το volna απλά ακουμπάτε τα δάχτυλά σας στην οθόνη. Οι κάθετες γραμμές δείχνουν τα όρια των μουσικών φθόγγων. Στο κάτω μέρος της οθόνης είναι οι πιο μπάσσες νότες, ενώ πιο πάνω οι πιο πρίμες. Αναλυτικά το βόλνα υποστηρίζει μέσω του μενού:
1. Επιλογή βασικής νότας (η νότα από την οποία ξεκινά η κλίμακα)
2. Κλίμακες (για την ώρα υποστηρίζονται μόνο στο κούρδισμα TET 12. Οι κλίμακες είναι χρωματική (όλοι οι φθόγγοι, ματζόρε, διάφορες μινόρε, πεντατονική, μπλούζ, κινέζικη πεντατονική, ολόκληρου τόνου, μισού ολόκληρου- ολόκληρου μισού κ.α.). Σύντομα θα μπορείτε να προσθέτετε και τις δικές σας και μάλιστα και σε διαφορετικά κουρδίσματα.
3. Συχνότητα του Λα (δυνατότητα επιλογής από τα 400 ως τα 500 Hz.
4. Συντελεστής οκτάβας (κανονικά είναι 2, αλλά μπορείτε να τον θέσετε μεταξύ 1.5 και 2.5)
5. Κούρδισμα Tuning Equal Temperament (Μικροτονική Μουσική). Δυνατότητα από 2 έως 48 (θα προστεθούν και άλλα). Φυσικά οι περισσότεροι χρησιμοποιούν το 12.
6. Legato (αλλαγή συχνότητας στη νότα χωρίς να ενεργοποιηθεί εκ νέου η περιβάλλουσα).
7. Μία περιβάλλουσα ADSR για κάθε ταλαντωτή.
8. Άπλωμα ταλαντωτών. Μπορούμε αν μετακινήσουμε το δαχτυλό μας ελαφρά δεξιά - αριστερά να αυξήσουμε την απόσταση των ταλαντωτών από την κεντρική συχνότητα της νότας μας.
9. Επιλογή αρχικής κυματομορφής ταλαντωτή (μπορούμε να διαλέξουμε μεταξύ 256 διαφορετικών κυματομορφών)
10. Δυνατότητα αλλαγής της κυματομορφής μετακινώντας το δάχτυλό μας κατακόρυφα. Όταν η παράμετρος WaveControl είναι στη μέγιστη τιμή της, μπορούμε να μετακινηθούμε και στις 256 κυματομορφές σχετικά με την αρχική μας, ενώ όσο μικρότερη τιμή έχει τόσο λιγότερο μετακινούμαστε. Όταν η τιμή είναι 0 η κυματομορφή μας δεν αλλάζει.
11. Εφέ καθυστέρησης - ηχούς με δυνατότητα προσδιορισμού της έντασης, του χρόνου καθυστέρησης και της ανάδρασης.
12. Πάχος πλέγματος. Καθορίζει πόσο πρέπει να μετακινήσουμε το δάχτυλό μας προκειμένου να μετακινηθούμε στην επόμενη νότα.
13. Δυνατότητα να στείλουμε μέσω midi νότες στο volna και αυτό να της παίξει (μέσω σύνδεσης με το καλώδιο USB, ή επιλέγοντας κάποια άλλη εφαρμογή που μπορεί να στείλει midi).
14. Δυνατότητα να στείλουμε midi μέσω καλωδίου USB ή σε άλλη εφαρμογή στο τηλέφωνό μας). Λειτουργία MIDI controller.
Για να μπούμε στο μενού επιλογών μπορούμε να πατήσουμε το κουμπί SET.
Με τα κουμπιά REC και PLAY, μπορούμε να ηχογραφήσουμε ένα loop διάρκειας μέχρι ενός λεπτού και να το βάλουμε να παίζει, ενώ εμείς μπορούμε να παίζουμε από πάνω.
Το πρόγραμμα αυτό είναι δωρεάν, είναι ελεύθερο λογισμικό - λογισμικό ανοιχτού κώδικα κα μπορείτε να το βρείτε εδώ:
https://github.com/bokontep/volna
Αν θέλετε να κατεβάσετε ένα έτοιμο APK για να εγκαταστήσετε στο τάμπλετ ή στο κινητό σας μπορείτε να πάτε εδώ:
https://github.com/bokontep/volna/releases
Κάνοντας κλικ στα assets αυτά ανοίγουν και μπορείτε να επιλέξετε το APK που θέλετε. Το πιο πρόσφατο είναι αυτό:
https://github.com/bokontep/volna/releases/download/0.8c/volna0.8c.apk

Αυτά και καλή διασκέδαση!
