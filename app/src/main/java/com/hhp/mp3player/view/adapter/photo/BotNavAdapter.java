package com.hhp.mp3player.view.adapter.photo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.fragment.music.AlbumFragment;
import com.hhp.mp3player.view.fragment.music.AllSongFragment;
import com.hhp.mp3player.view.fragment.music.ArtistFragment;
import com.hhp.mp3player.view.fragment.music.PlaylistFragment;
import com.hhp.mp3player.view.fragment.photo.MemoriesFragment;
import com.hhp.mp3player.view.fragment.photo.PhotosFragment;

public class BotNavAdapter extends FragmentStateAdapter {
    private OnMainCallback callback;

    public BotNavAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public BotNavAdapter(@NonNull Fragment fragment, OnMainCallback callback) {
        super(fragment);
        this.callback = callback;
    }

    public BotNavAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                PhotosFragment photosFrg = new PhotosFragment();
                photosFrg.setCallback(callback);
                return photosFrg;
            default:
                MemoriesFragment memoriesFrg = new MemoriesFragment();
                memoriesFrg.setCallback(callback);
                return memoriesFrg;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
