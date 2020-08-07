package org.bokontep.midi;

import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;

/**
 * Miscellaneous tools for Android MIDI.
 */
public class MidiTools {

    /**
     * @return a device that matches the manufacturer and product or null
     */
    public static MidiDeviceInfo findDevice(MidiManager midiManager,
                                            String manufacturer, String product) {
        for (MidiDeviceInfo info : midiManager.getDevices()) {
            String deviceManufacturer = info.getProperties()
                    .getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
            if ((manufacturer != null)
                    && manufacturer.equals(deviceManufacturer)) {
                String deviceProduct = info.getProperties()
                        .getString(MidiDeviceInfo.PROPERTY_PRODUCT);
                if ((product != null) && product.equals(deviceProduct)) {
                    return info;
                }
            }
        }
        return null;
    }
}

