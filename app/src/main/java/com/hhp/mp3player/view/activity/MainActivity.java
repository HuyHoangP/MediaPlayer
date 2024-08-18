package com.hhp.mp3player.view.activity;

import static com.hhp.mp3player.view.fragment.music.SongDetailFragment.OPTION;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hhp.mp3player.CommonUtils;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.ActivityMainBinding;
import com.hhp.mp3player.service.MusicService;
import com.hhp.mp3player.view.base.BaseActivity;
import com.hhp.mp3player.view.fragment.music.MenuMusicFragment;
import com.hhp.mp3player.viewmodel.MainActVM;

import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainActVM> {
    public static final String PREPARE_PLAYER = "PREPARE_PLAYER";
    public static final String ACTION_EXIT = "ACTION_EXIT";
    public static final String HANDLE_SONG_REMOVED = "HANDLE_SONG_REMOVED";
    private BroadcastReceiver receiver;
    private final MutableLiveData<MusicService> serviceLD = new MutableLiveData<>();
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((MusicService.MusicBinder) iBinder).getMusicService();
            service.setCallback(MainActivity.this);
            String option = CommonUtils.getInstance().getPref(OPTION);
            if(option != null) service.setOption(Integer.parseInt(option));
            serviceLD.postValue(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };

    @Override
    public void callback(String key, Object[] data) {
        if (key.equals(PREPARE_PLAYER)) {
            preparePlayerOnClick(data);
        } else if (key.equals(HANDLE_SONG_REMOVED)) {
            handleSongRemoved((Song)data[0]);
        }
    }

    private void preparePlayerOnClick(Object[] data) {
        service.getListShuffle().clear();
        service.setCurrentListName((String) data[2]);
        service.setCurrentPlaylist((List<Song>) data[1]);
        service.preparePlayer((int) data[0]);
    }

    private void handleSongRemoved(Song song) {
        service.getListSongLD().getValue().remove(song);
        Log.i("MainActOnRemoved", "handleSongRemoved: " + service.getListSongLD().getValue().size());
        service.setCurrentIndex(service.getCurrentIndex() == 0? 0: service.getCurrentIndex() - 1);
    }

    @Override
    public MutableLiveData<MusicService> getServiceLD() {
        return serviceLD;
    }

    @Override
    public MusicService getService() {
        return service;
    }

    @Override
    public LiveData<List<Song>> getListSongDbLD() {
        return viewModel.getListSongDbLD();
    }

    @Override
    public void insertSong(Song... songs) {
        viewModel.insertSong(songs);
    }

    @Override
    public void deleteSong(Song song) {
        viewModel.deleteSong(song);
    }

    @Override
    public void updateSong(Song song) {
        viewModel.updateSong(song);
    }

    @Override
    public void deleteAllSong() {
        viewModel.deleteAllSong();
    }

    @Override
    public void insertPhotos(Photo... photos) {
        viewModel.insertPhotos(photos);
    }

    @Override
    public void deletePhotos(Photo... photos) {
        viewModel.deletePhoto(photos);
    }

    @Override
    public void deleteMemory(String date) {
        viewModel.deleteMemory(date);
    }

    @Override
    public LiveData<List<Photo>> getListPhotoLD(String date) {
        return viewModel.getListPhotoLD(date);
    }


    @Override
    public LiveData<MemoryInfo> getMemoryInfo(String date) {
        return viewModel.getMemoryInfo(date);
    }

    @Override
    public void insertMemoryInfo(MemoryInfo... memoryInfos) {
        viewModel.insertMemoryInfo(memoryInfos);
    }

    @Override
    public void updateMemoryInfo(MemoryInfo... memoryInfos) {
        viewModel.updateMemoryInfo(memoryInfos);
    }

    @Override
    public void deleteMemoryInfo(String date) {
        viewModel.deleteMemoryInfo(date);
    }

    @Override
    public LiveData<List<MemoryInfo>> getListMemoryInfoLD() {
        return viewModel.getListMemoryInfoLD();
    }


    @Override
    public void initView() {
        initReceiver();

        bindService(new Intent(this, MusicService.class), connection, Context.BIND_AUTO_CREATE);
        showFragment(MenuMusicFragment.TAG, null, false, 0);

    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_EXIT)) {
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_EXIT);
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED);
    }

    @Override
    public Class<MainActVM> initViewModel() {
        return MainActVM.class;
    }

    @Override
    public ActivityMainBinding initViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onDestroy() {
        unbindService(connection);
        unregisterReceiver(receiver);
        super.onDestroy();
    }


}