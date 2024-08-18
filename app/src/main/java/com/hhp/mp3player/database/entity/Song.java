package com.hhp.mp3player.database.entity;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Song {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ID")
    private String id;
    @NonNull
    @ColumnInfo(name = "Title")
    private String title;
    @NonNull
    @ColumnInfo(name = "Path")
    private String path;
    @ColumnInfo(name = "Album")
    private String album;
    @ColumnInfo(name = "Artist")
    private String artist;
    @ColumnInfo(name = "AlbumUri")
    private String albumUri;
    private boolean isSelected;


    public Song(String title, String path, String album, String artist, String albumUri) {
        id = UUID.randomUUID().toString();
        this.title = title;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.albumUri = albumUri;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumUri() {
        return albumUri;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setAlbumUri(String albumUri) {
        this.albumUri = albumUri;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return id.equals(song.id) || title.equals(song.title) && path.equals(song.path) && Objects.equals(artist, song.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, title, album, artist, albumUri);
    }
}
