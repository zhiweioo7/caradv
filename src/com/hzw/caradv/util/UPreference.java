package com.hzw.caradv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * preference管理类
 * 
 * @author Du
 * @Email llfer2006@gmail.com
 * @time:2012-1-16
 */

public class UPreference {

    public static SharedPreferences getPreference(Context context) {
        if(context == null)
            return null;
        context.getSharedPreferences("caradv", Context.MODE_MULTI_PROCESS);
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return false;
        return sharePre.edit().putString(key, value).commit();
    }

    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return false;
        return sharePre.edit().putBoolean(key, value).commit();
    }

    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return false;
        return sharePre.edit().putFloat(key, value).commit();
    }

    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return false;
        return sharePre.edit().putLong(key, value).commit();
    }

    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return false;
        return sharePre.edit().putInt(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return defValue;
        return sharePre.getString(key, defValue);
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return defValue;
        return sharePre.getInt(key, defValue);
    }

    public static float getFloat(Context context, String key, float defValue) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return defValue;
        return sharePre.getFloat(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return defValue;
        return sharePre.getLong(key, defValue);
    }

    public static boolean getBoolean(Context context, String key,
            boolean defValue) {
        SharedPreferences sharePre = getPreference(context);
        if (sharePre == null)
            return defValue;
        return sharePre.getBoolean(key, defValue);
    }
}
