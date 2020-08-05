package org.bokontep.wavesynth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class AppPreferences {

    private SharedPreferences appPreferences;
    public AppPreferences(Context context)
    {

        appPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
    }
    public void writeInt(String name, int number)
    {
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt(name, number);
        editor.apply();
    }
    public void writeIntArray(String name, int[] numbers)
    {
        SharedPreferences.Editor editor = appPreferences.edit();
        Set<String> set = new HashSet<String>();
        for(int i=0;i<numbers.length;i++)
        {
            set.add(""+numbers[i]);
        }
        editor.putStringSet(name,set);
        editor.apply();
    }
    public void writeFloat(String name, float number)
    {
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putFloat(name,number);
        editor.apply();
    }
    public void writeFloatArray(String name, float[] numbers)
    {
        SharedPreferences.Editor editor = appPreferences.edit();
        Set<String> set = new HashSet<String>();
        for(int i=0;i<numbers.length;i++)
        {
            set.add(""+numbers[i]);
        }
        editor.putStringSet(name,set);
        editor.apply();
    }
    public void writeString(String name, String text)
    {
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString(name, text);
        editor.apply();
    }
    public void writeStringArray(String name, String[] texts)
    {
        SharedPreferences.Editor editor = appPreferences.edit();
        Set<String> set = new HashSet<String>();
        for(int i=0;i<texts.length;i++)
        {
            set.add(""+texts[i]);
        }
        editor.putStringSet(name,set);
        editor.apply();

    }
    public int readInt(String name, int defaultValue)
    {
        return appPreferences.getInt(name,defaultValue);
    }
    public int[] readIntArray(String name, int[] defaultValues)
    {
        Set<String> defaultStringValues =  new HashSet<>();
        for(int i=0;i<defaultValues.length;i++)
        {
            defaultStringValues.add(""+defaultValues[i]);
        }

        String[] strRetVal = appPreferences.getStringSet(name,defaultStringValues).toArray(new String[0]);
        int[] retval= new int[strRetVal.length];

        for(int i=0;i<strRetVal.length;i++)
        {
            retval[i] = Integer.parseInt(strRetVal[i]);
        }
        return retval;
    }
    public float readFloat(String name, float defaultValue)
    {
        return  appPreferences.getFloat(name,defaultValue);
    }
    public float[] readFloatArray(String name, float[] defaultValues)
    {
        Set<String> defaultStringValues =  new HashSet<>();
        for(int i=0;i<defaultValues.length;i++)
        {
            defaultStringValues.add(""+defaultValues[i]);
        }

        String[] strRetVal = appPreferences.getStringSet(name,defaultStringValues).toArray(new String[0]);
        float[] retval= new float[strRetVal.length];

        for(int i=0;i<strRetVal.length;i++)
        {
            retval[i] = Float.parseFloat(strRetVal[i]);
        }
        return retval;
    }
    public String readString(String name, String defaultValue)
    {
        return appPreferences.getString(name, defaultValue);
    }
    public String[] readStringArray(String name, String[] defaultValues)
    {
        Set<String> defaultStringValues =  new HashSet<>();


        String[] strRetVal = appPreferences.getStringSet(name,defaultStringValues).toArray(new String[0]);

        return strRetVal;
    }



}
