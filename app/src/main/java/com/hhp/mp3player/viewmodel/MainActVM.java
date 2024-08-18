package com.hhp.mp3player.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hhp.mp3player.App;
import com.hhp.mp3player.database.MediaRepository;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.database.entity.Song;

import java.util.List;

public class MainActVM extends AndroidViewModel {
    private MediaRepository repos;
    private LiveData<List<Song>> listSongDbLD;

    private LiveData<List<Photo>> listPhotoLD;
    private LiveData<MemoryInfo> memoryInfoLD;

    private LiveData<List<MemoryInfo>> listMemoryInfoLD;

    public MainActVM(@NonNull Application application) {
        super(application);
        repos = new MediaRepository();
        listSongDbLD = App.getInstance().getDb().songDAO().getListSong();
    }

    public LiveData<MemoryInfo> getMemoryInfo(String date) {
        memoryInfoLD = App.getInstance().getDb().songDAO().getMemoryInfo(date);
        return memoryInfoLD;
    }

    public LiveData<List<MemoryInfo>> getListMemoryInfoLD() {
        listMemoryInfoLD = App.getInstance().getDb().songDAO().getListMemoryInfo();
        return listMemoryInfoLD;
    }

    public LiveData<List<Photo>> getListPhotoLD(String date) {
        listPhotoLD = App.getInstance().getDb().songDAO().getListPhoto(date);
        return listPhotoLD;
    }

    public LiveData<List<Song>> getListSongDbLD() {
        return listSongDbLD;
    }

    public void insertSong(Song...songs){
        repos.insertSong(songs);
    }

    public void deleteSong(Song song){
        repos.deleteSong(song);
    }

    public void updateSong(Song song){
        repos.updateSong(song);
    }

    public void deleteAllSong(){
        repos.deleteAllSong();
    }

    public void insertPhotos(Photo...photos){
        repos.insertPhotos(photos);
    }

    public void deletePhoto(Photo...photos){
        repos.deletePhotos(photos);
    }

    public void deleteMemory(String date){
        repos.deleteMemory(date);
    }

    public void insertMemoryInfo(MemoryInfo...memoryInfos){
        repos.insertMemoryInfo(memoryInfos);
    }

    public void updateMemoryInfo(MemoryInfo...memoryInfos){
        repos.updateMemoryInfo(memoryInfos);
    }

    public void deleteMemoryInfo(String date){
        repos.deleteMemoryInfo(date);
    }

}
