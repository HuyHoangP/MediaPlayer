package com.hhp.mp3player.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.UUID;

@Entity (primaryKeys = {"Date","Path"})
public class Photo {

    @NonNull
    @ColumnInfo(name = "Date")
    private String date;

    @NonNull
    @ColumnInfo(name = "Path")
    private String path;

    public Photo(@NonNull String date, @NonNull String path) {
        this.date = date;
        this.path = path;
    }

    public String getPath() {
        return path;
    }


    public String getDate() {
        return date;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "date='" + date + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
