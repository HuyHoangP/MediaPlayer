package com.hhp.mp3player;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.lifecycle.LiveData;

import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.database.entity.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Storage {
    public int mainColor;
    public int prevColor = Color.BLACK;
    public List<Song> listSongAll;



    public Song prevSong;
    public Bitmap thumbnail;
    public Set<String> setDate;
    public List<String> listDate = new ArrayList<>();
}
