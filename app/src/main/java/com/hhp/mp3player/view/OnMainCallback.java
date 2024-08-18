package com.hhp.mp3player.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.service.MusicService;

import java.util.List;

public interface OnMainCallback {
    void callback (String key, Object[] data);
    void showFragment(String tag, Object data, Boolean isBack, int slideAnim);
    MutableLiveData<MusicService> getServiceLD();
    MusicService getService();

    //

    LiveData<List<Song>> getListSongDbLD();
    void insertSong(Song...songs);
    void deleteSong(Song song);
    void updateSong(Song song);

    void deleteAllSong();

    void insertPhotos(Photo...photos);
    void deletePhotos(Photo...photos);
    void deleteMemory(String date);
    LiveData<List<Photo>> getListPhotoLD(String date);
    LiveData<MemoryInfo> getMemoryInfo(String date);

    void insertMemoryInfo(MemoryInfo...memoryInfos);

    void updateMemoryInfo(MemoryInfo...memoryInfos);

    void deleteMemoryInfo(String date);

    LiveData<List<MemoryInfo>> getListMemoryInfoLD();


}
