package com.hhp.mp3player.database.entity;

import java.util.List;

public class Category {
    private List<Song> listSong;
    private String name;
    private boolean isExpanded;

    public Category(List<Song> listSong, String name) {
        this.listSong = listSong;
        this.name = name;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public List<Song> getListSong() {
        return listSong;
    }

    public String getName() {
        return name;
    }
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
