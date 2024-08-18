package com.hhp.mp3player.database.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.hhp.mp3player.database.MediaDAO;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.database.entity.Song;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Song.class, Photo.class, MemoryInfo.class}, version = 1)
public abstract class MediaDB extends RoomDatabase {
    public abstract MediaDAO songDAO();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
}
