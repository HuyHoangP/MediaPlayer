package com.hhp.mp3player.view.fragment.music;

import static com.hhp.mp3player.view.base.BaseActivity.SLIDE_LEFT_AND_RIGHT;
import static com.hhp.mp3player.view.base.BaseActivity.SLIDE_UP_AND_DOWN;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.hhp.mp3player.App;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.FragmentMenuMusicBinding;
import com.hhp.mp3player.view.adapter.music.TabAdapter;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.SharedFrgVM;

public class MenuMusicFragment extends BaseFragment<FragmentMenuMusicBinding, SharedFrgVM> {
    public static final String TAG = MenuMusicFragment.class.getName();
    private final ValueAnimator.AnimatorUpdateListener colorAnimator = animator -> {
        binding.lnMenu.setBackgroundColor((int) animator.getAnimatedValue());
        binding.controller.seekBar.setProgressTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
//        binding.controller.seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    };
    private final MediaPlayer.OnCompletionListener completionListener = mediaPlayer -> onSongCompleted();
    private Observer<Song> observerSelectedSong;
    private Observer<Integer> observerStatus;
    private Thread thread;

    @Override
    public void initView() {
        setUI();
        viewModel.setMenuRunning(true);
        callback.getServiceLD().observe(this, musicService -> {
            service = musicService;
            loadDB();
            service.setMenuRunning(true);
            service.setCompletionListener(completionListener);
            initOnClickView();
            initObserver();
            binding.controller.ivToggle.setImageLevel(service.getStatus());
            initTabLayout();
        });
    }

    private void initTabLayout() {
        TabAdapter tabAdapter = new TabAdapter(this);
        binding.vpCategory.setAdapter(tabAdapter);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vpCategory.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.vpCategory.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabLayout.getTabAt(position).select();
            }
        });
    }

    private void loadDB() {
        callback.getListSongDbLD().observe(this, listSong -> {
            viewModel.getListSongLD().postValue(listSong);
            service.getListSongLD().postValue(listSong);
            viewModel.getSelectedSongLD().observe(MenuMusicFragment.this, observerSelectedSong);
            initController();
        });
    }

    private void setUI() {
        binding.controller.tvSongTitle.setSelected(true);
        binding.controller.tvSongAlbum.setSelected(true);
        binding.controller.seekBar.setProgress(0);
        binding.controller.seekBar.setPadding(0, 0, 0, 0);

    }

    private void setMainColor(Song song) {
        // Get color from Resource to int
        String colorHex = "#" + Integer.toHexString(requireActivity().getColor(R.color.blue) & 0x00ffffff);
        int color = Color.parseColor(colorHex);
        //
        App.getInstance().setMainColor(song, colorAnimator);
    }

    private void initObserver() {
        if (observerSelectedSong == null){
            observerSelectedSong = song -> {
                if (song != null) {
                    setMainColor(song);
                    setControllerUI(song);
                    service.setSongNotificationUI();
                }
            } ;
        } else {
            viewModel.getSelectedSongLD().postValue(service.getCurrentSong());
        }


        if (observerStatus == null)
            observerStatus = status -> binding.controller.ivToggle.setImageLevel(status);
    }

    private void setControllerUI(Song song) {
        binding.controller.tvSongTitle.setText(song.getTitle());
        binding.controller.tvSongAlbum.setText(song.getAlbum());
        binding.controller.seekBar.setMax(service.getSongDuration());
        App.getInstance().setSongThumbnail(binding.controller.ivThumbnail, song, true);
    }

    private void initOnClickView() {
        binding.ivSetting.setOnClickListener(this);
        binding.ivSearch.setOnClickListener(this);
        binding.controller.ivThumbnail.setOnClickListener(this);
        binding.controller.lnSong.setOnClickListener(this);
        binding.controller.ivToggle.setOnClickListener(this);
        binding.controller.ivNextSong.setOnClickListener(this);
        binding.controller.ivBackSong.setOnClickListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initController() {
        service.getStatusLD().observe(this, observerStatus);
        binding.controller.seekBar.setOnTouchListener((view, motionEvent) -> true);
        setSeekBarThread();
    }

    private void setSeekBarThread() {
        thread = new Thread(this::updateSeekBar);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateSeekBar() {
        while (viewModel.isViewAvail()) {
            try {
                while (viewModel.isMenuRunning()) {
                    int currentTime = service.getCurrentTime();
                    requireActivity().runOnUiThread(() -> {
                        binding.controller.seekBar.setProgress(currentTime);
//                    Log.i(TAG, "updateSeekBar: " + currentTime + " / " + service.getSongDuration());
                    });
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public Class<SharedFrgVM> initViewModel() {
        return SharedFrgVM.class;
    }

    @Override
    public FragmentMenuMusicBinding initViewBinding() {
        return FragmentMenuMusicBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void clickView(View view) {
        if (view.getId() == R.id.ivSetting) {
            callback.showFragment(SettingFragment.TAG, null, true, SLIDE_LEFT_AND_RIGHT);
        } else if (service.getCurrentSong() == null) {
        } else if (view.getId() == R.id.ivThumbnail || view.getId() == R.id.lnSong) {
            callback.showFragment(SongDetailFragment.TAG, null, true, SLIDE_UP_AND_DOWN);
        } else if (view.getId() == R.id.ivToggle) {
            service.toggle();
        } else if (view.getId() == R.id.ivNextSong) {
            service.next();
        } else if (view.getId() == R.id.ivBackSong) {
            service.back();
        } else if (view.getId() == R.id.ivSearch) {
            callback.showFragment(SearchFragment.TAG, null, true, SLIDE_LEFT_AND_RIGHT);
        }
    }

    private void onSongCompleted() {
        if (service.getListSongLD().getValue().isEmpty()) return;
        Log.i(TAG, "onCompleted: " + service.getCurrentSong().getTitle() + " --- " + service.getCurrentTime() + " / " + service.getSongDuration());
        service.onSongCompleted();
    }

    @Override
    public void onDestroy() {
        viewModel.setMenuRunning(false);
        service.setMenuRunning(false);
        viewModel.setViewAvail(false);
        thread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        viewModel.setMenuRunning(true);
        if (service != null) service.setMenuRunning(true);
        super.onResume();
    }

    @Override
    public void onPause() {
        viewModel.setMenuRunning(false);
        service.setMenuRunning(false);
        super.onPause();
    }
}
