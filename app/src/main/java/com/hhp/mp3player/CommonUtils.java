package com.hhp.mp3player;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class CommonUtils {
    private static CommonUtils instance;
    private static final String PREF_FILE = "pref_saving";
    private CommonUtils(){};
    public static CommonUtils getInstance(){
        if(instance == null){
            instance = new CommonUtils();
        }
        return instance;
    }
    public void savePref(String key, String value){
        SharedPreferences pref = App.getInstance().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().putString(key, value).apply();
    }

    public void saveSetPref(String key, Set<String> value){
        SharedPreferences pref = App.getInstance().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().putStringSet(key, value).apply();
    }

    public String getPref(String key){
        SharedPreferences pref = App.getInstance().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return pref.getString(key,null);
    }

    public Set<String> getSetPref(String key){
        SharedPreferences pref = App.getInstance().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return pref.getStringSet(key,null);
    }

    public void clearPref(String key){
        SharedPreferences pref = App.getInstance().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().remove(key).apply();
    }
}
