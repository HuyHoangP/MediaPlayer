package com.hhp.mp3player.database;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.hhp.mp3player.App;
import com.hhp.mp3player.database.db.MediaDB;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.database.entity.Song;

import java.util.List;

public class MediaRepository {
    public static final String TAG = MediaRepository.class.getName();
    private final MediaDAO mediaDAO;

    public MediaRepository() {
        MediaDB db = App.getInstance().getDb();
        mediaDAO = db.songDAO();

    }


    public void insertSong(Song... songs) {
        MediaDB.databaseWriteExecutor.execute(() ->
                mediaDAO.insertSongs(songs));
    }

    public void deleteSong(Song song) {
        MediaDB.databaseWriteExecutor.execute(() ->
                mediaDAO.deleteSong(song));
    }

    public void updateSong(Song song) {
        MediaDB.databaseWriteExecutor.execute(() ->
                mediaDAO.updateSong(song));
    }

    public void deleteAllSong() {
        MediaDB.databaseWriteExecutor.execute(() ->
                mediaDAO.deleteAllSong());
    }

    public void insertPhotos(Photo... photos) {
        MediaDB.databaseWriteExecutor.execute(() -> mediaDAO.insertPhotos(photos));
    }

    public void deletePhotos(Photo... photos) {
        MediaDB.databaseWriteExecutor.execute(() -> mediaDAO.deletePhotos(photos));
    }

    public void deleteMemory(String date) {
        MediaDB.databaseWriteExecutor.execute(() -> mediaDAO.deleteMemory(date));
    }

    public void insertMemoryInfo(MemoryInfo...memoryInfos){
        MediaDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mediaDAO.insertMemoryInfo(memoryInfos);
            }
        });
    }

    public void updateMemoryInfo(MemoryInfo...memoryInfos){
        MediaDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mediaDAO.updateMemoryInfo(memoryInfos);
            }
        });
    }

    public void deleteMemoryInfo(String date){
        MediaDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mediaDAO.deleteMemoryInfo(date);
            }
        });
    }


}
