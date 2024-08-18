package com.hhp.mp3player.view.adapter.music;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hhp.mp3player.view.fragment.music.AlbumFragment;
import com.hhp.mp3player.view.fragment.music.AllSongFragment;
import com.hhp.mp3player.view.fragment.music.ArtistFragment;
import com.hhp.mp3player.view.fragment.music.PlaylistFragment;

public class TabAdapter extends FragmentStateAdapter {


    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public TabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public TabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1: return new ArtistFragment();
            case 2: return new AlbumFragment();
            case 3: return new PlaylistFragment();
            default: return new AllSongFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
