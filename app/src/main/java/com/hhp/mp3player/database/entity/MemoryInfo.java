package com.hhp.mp3player.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MemoryInfo {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Date")
    private String date;

    @NonNull
    @ColumnInfo(name = "Cover")
    private String cover;

    @NonNull
    @ColumnInfo(name = "Title")
    private String title;

    public MemoryInfo(@NonNull String date, @NonNull String cover, @NonNull String title) {
        this.date = date;
        this.cover = cover;
        this.title = title;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public String getCover() {
        return cover;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}
