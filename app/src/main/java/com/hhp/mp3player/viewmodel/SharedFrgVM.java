package com.hhp.mp3player.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.view.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedFrgVM extends BaseViewModel {
    private boolean isViewAvail = true;
    private boolean isMenuRunning;
    private boolean isDetailRunning;
    private MutableLiveData<List<Song>> listSongLD = new MutableLiveData<>();
    private MutableLiveData<Song> selectedSongLD = new MutableLiveData<>();

    public boolean isMenuRunning() {
        return isMenuRunning;
    }

    public void setMenuRunning(boolean menuRunning) {
        this.isMenuRunning = menuRunning;
    }

    public boolean isDetailRunning() {
        return isDetailRunning;
    }

    public void setDetailRunning(boolean detailRunning) {
        this.isDetailRunning = detailRunning;
    }

    public boolean isViewAvail() {
        return isViewAvail;
    }

    public void setViewAvail(boolean viewAvail) {
        isViewAvail = viewAvail;
    }

    public MutableLiveData<List<Song>> getListSongLD() {
        return listSongLD;
    }

    public MutableLiveData<Song> getSelectedSongLD() {
        return selectedSongLD;
    }

}
