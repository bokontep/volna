package org.bokontep.midi;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceInfo.PortInfo;

public class MidiPortWrapper {
    private MidiDeviceInfo mInfo;
    private int mPortIndex;
    private int mType;
    private String mString;

    /**
     * Wrapper for a MIDI device and port description.
     * @param info
     * @param portType
     * @param portIndex
     */
    public MidiPortWrapper(MidiDeviceInfo info, int portType, int portIndex) {
        mInfo = info;
        mType = portType;
        mPortIndex = portIndex;
    }

    private void updateString() {
        if (mInfo == null) {
            mString = "- - - - - -";
        } else {
            StringBuilder sb = new StringBuilder();
            String name = mInfo.getProperties()
                    .getString(MidiDeviceInfo.PROPERTY_NAME);
            if (name == null) {
                name = mInfo.getProperties()
                        .getString(MidiDeviceInfo.PROPERTY_MANUFACTURER) + ", "
                        + mInfo.getProperties()
                        .getString(MidiDeviceInfo.PROPERTY_PRODUCT);
            }
            sb.append("#" + mInfo.getId());
            sb.append(", ").append(name);
            PortInfo portInfo = findPortInfo();
            sb.append("[" + mPortIndex + "]");
            if (portInfo != null) {
                sb.append(", ").append(portInfo.getName());
            } else {
                sb.append(", null");
            }
            mString = sb.toString();
        }
    }

    /**

     */
    private PortInfo findPortInfo() {
        PortInfo[] ports = mInfo.getPorts();
        for (PortInfo portInfo : ports) {
            if (portInfo.getPortNumber() == mPortIndex
                    && portInfo.getType() == mType) {
                return portInfo;
            }
        }
        return null;
    }

    public int getPortIndex() {
        return mPortIndex;
    }

    public MidiDeviceInfo getDeviceInfo() {
        return mInfo;
    }

    @Override
    public String toString() {
        if (mString == null) {
            updateString();
        }
        return mString;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof org.bokontep.midi.MidiPortWrapper))
            return false;
        org.bokontep.midi.MidiPortWrapper otherWrapper = (org.bokontep.midi.MidiPortWrapper) other;
        if (mPortIndex != otherWrapper.mPortIndex)
            return false;
        if (mType != otherWrapper.mType)
            return false;
        if (mInfo == null)
            return (otherWrapper.mInfo == null);
        return mInfo.equals(otherWrapper.mInfo);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + mPortIndex;
        hashCode = 31 * hashCode + mType;
        hashCode = 31 * hashCode + mInfo.hashCode();
        return hashCode;
    }

}

