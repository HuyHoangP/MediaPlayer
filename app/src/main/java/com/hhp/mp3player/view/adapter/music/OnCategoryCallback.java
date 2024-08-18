package com.hhp.mp3player.view.adapter.music;

import com.hhp.mp3player.database.entity.Song;

public interface OnCategoryCallback {
    void notifyChanged(Song song);
}
