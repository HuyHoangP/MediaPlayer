package com.hhp.mp3player.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.database.entity.Song;

import java.util.List;

@Dao
public interface MediaDAO {
    @Insert
    void insertSongs(Song...songs);
    @Update
    void updateSong(Song song);
    @Delete
    void deleteSong(Song song);
    @Query("DELETE FROM Song")
    void deleteAllSong();
    @Query("SELECT * FROM Song")
    LiveData<List<Song>> getListSong();
    @Query("SELECT * FROM Song WHERE Song.ID LIKE :id")
    LiveData<Song> getSong(String id);
    @Query("SELECT * FROM Song")
    List<Song> getListSongNotLD();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPhotos(Photo...photos);
    @Delete
    void deletePhotos(Photo...photos);
    @Query("DELETE FROM Photo WHERE Photo.Date LIKE :date")
    void deleteMemory(String date);

    @Query("SELECT * FROM Photo WHERE date like :date")
    LiveData<List<Photo>> getListPhoto(String date);

    @Insert
    void insertMemoryInfo(MemoryInfo...memoryInfos);

    @Update
    void updateMemoryInfo(MemoryInfo...memoryInfos);

    @Query("DELETE FROM MemoryInfo WHERE date like :date")
    void deleteMemoryInfo(String date);

    @Query("SELECT * FROM MemoryInfo WHERE date like :date")
    LiveData<MemoryInfo> getMemoryInfo(String date);

    @Query("SELECT * FROM MemoryInfo")
    LiveData<List<MemoryInfo>> getListMemoryInfo();



}
