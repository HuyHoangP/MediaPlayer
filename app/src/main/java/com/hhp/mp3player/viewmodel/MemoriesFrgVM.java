package com.hhp.mp3player.viewmodel;

import android.util.Log;

import com.hhp.mp3player.view.base.BaseViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MemoriesFrgVM extends BaseViewModel {

    public static final String TAG = MemoriesFrgVM.class.getName();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);


    public String getCurrentTime(){
        Date date = new Date();
        Log.i(TAG, "getCurrentTime: " + date);
        return dateFormat.format(date);
    }
}
