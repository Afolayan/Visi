package com.jcedar.visinaas.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Afolayan on 22/1/2016.
 */
public class PrefUtils {
    public static  final String SOUND_KEY = "sounds";
    public static  final String VIBRATE_KEY = "vibration";
    public static  final String NOTIFY_KEY = "notification";

    public static boolean hasSound(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SOUND_KEY, true); // by default we want sound
    }

    public static boolean hasVibration(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(VIBRATE_KEY, true); // by default we want vibrations
    }

    public static boolean hasNotification(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(NOTIFY_KEY, true); // by default we are notified
    }
}
