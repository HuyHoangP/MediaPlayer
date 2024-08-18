package com.hhp.mp3player.view.fragment.photo;

public interface OnPlayMemoryFrgCallback {
    void setPhoto(int position);

    void clickNextPhoto();

    void backPressed();

    void pauseAutoMode();

    void setTextAutoMode(boolean autoMode);
}
