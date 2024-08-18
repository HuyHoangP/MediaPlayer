package com.hhp.mp3player.view.fragment.music;

import static com.hhp.mp3player.service.MusicService.OPTION_NORMAL;
import static com.hhp.mp3player.service.MusicService.OPTION_REPEAT;
import static com.hhp.mp3player.service.MusicService.OPTION_SHUFFLE;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.SeekBar;

import com.hhp.mp3player.App;
import com.hhp.mp3player.CommonUtils;
import com.hhp.mp3player.R;
import com.hhp.mp3player.databinding.FragmentSongDetailBinding;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.SharedFrgVM;

public class SongDetailFragment extends BaseFragment<FragmentSongDetailBinding, SharedFrgVM> {
    public static final String TAG = SongDetailFragment.class.getName();
    public static final String OPTION = "OPTION";
    private final MediaPlayer.OnCompletionListener completionListener = mediaPlayer -> service.onSongCompleted();
    private Thread thread;
    private final ValueAnimator.AnimatorUpdateListener colorAnimator = animator -> binding.lnSong.setBackgroundColor((int) animator.getAnimatedValue());

    @Override
    public void initView() {
        App.getInstance().getStorage().prevColor = App.getInstance().getStorage().mainColor;
        viewModel.setDetailRunning(true);
        service = callback.getService();
        service.setCompletionListener(completionListener);
        initOnClickView();
        initUI();
    }

    private void initOnClickView() {
        binding.ivRepeat.setOnClickListener(this);
        binding.ivShuffle.setOnClickListener(this);
        binding.ivToggle.setOnClickListener(this);
        binding.ivNextSong.setOnClickListener(this);
        binding.ivBackSong.setOnClickListener(this);
        binding.tvSongTitle.setOnClickListener(this);
    }


    private void initUI() {
        setSongUI();
        binding.tvCurrentList.setText(service.getCurrentListName());
        binding.ivToggle.setImageLevel(service.getStatus());
        binding.tvSongTitle.setSelected(true);
        binding.ivRepeat.setColorFilter(service.getOption() == OPTION_REPEAT ? Color.WHITE : Color.GRAY);
        binding.ivShuffle.setColorFilter(service.getOption() == OPTION_SHUFFLE ? Color.WHITE : Color.GRAY);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                service.seekTo(seekBar.getProgress());
                binding.tvCurrentTime.setText(service.getCurrentTimeText());
            }
        });

        thread = new Thread(this::updateSeekBar);
        thread.setDaemon(true);
        thread.start();

    }

    private void setSongUI() {
        service.getCurrentSongLD().observe(this, currentSong -> {
            App.getInstance().setMainColor(service.getCurrentSong(), colorAnimator);
            binding.tvSongTitle.setText(service.getCurrentSong().getTitle());
            binding.tvSongAlbum.setText(service.getCurrentSong().getAlbum());
            binding.seekBar.setMax(service.getSongDuration());
            binding.tvDuration.setText(service.getSongDurationText());
            App.getInstance().setSongThumbnail(binding.ivThumbnail, service.getCurrentSong(), false);
            service.setSongNotificationUI();
        });
        service.getStatusLD().observe(this, integer -> binding.ivToggle.setImageLevel(service.getStatus()));
    }

    private void updateSeekBar() {
        while (viewModel.isViewAvail()) {
            try {
                while (viewModel.isDetailRunning()){
                    int currentTime = service.getCurrentTime();
                    requireActivity().runOnUiThread(() -> {
                        binding.seekBar.setProgress(currentTime);
                        binding.tvCurrentTime.setText(service.getCurrentTimeText());
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
    public FragmentSongDetailBinding initViewBinding() {
        return FragmentSongDetailBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void clickView(View view) {
        if (view.getId() == R.id.ivShuffle) {
            setShuffle();
        } else if (view.getId() == R.id.ivRepeat) {
            setRepeat();
        } else if (view.getId() == R.id.ivToggle) {
            service.toggle();
        } else if (view.getId() == R.id.ivNextSong) {
            service.next();
        } else if (view.getId() == R.id.ivBackSong) {
            service.back();
        } else if (view.getId() == R.id.tvSongTitle) {
            binding.tvCurrentList.setVisibility(binding.tvCurrentList.getVisibility() == View.VISIBLE?View.INVISIBLE:View.VISIBLE);
        }
    }

    private void setRepeat() {
        if (service.getOption() == OPTION_REPEAT) {
            service.setOption(OPTION_NORMAL);
            binding.ivRepeat.setColorFilter(Color.GRAY);
        } else {
            service.setOption(OPTION_REPEAT);
            binding.ivRepeat.setColorFilter(Color.WHITE);
            binding.ivShuffle.setColorFilter(Color.GRAY);
        }
        CommonUtils.getInstance().savePref(OPTION, String.valueOf(service.getOption()));
    }

    private void setShuffle() {
        if (service.getOption() == OPTION_SHUFFLE) {
            service.setOption(OPTION_NORMAL);
            binding.ivShuffle.setColorFilter(Color.GRAY);
        } else {
            service.setOption(OPTION_SHUFFLE);
            binding.ivShuffle.setColorFilter(Color.WHITE);
            binding.ivRepeat.setColorFilter(Color.GRAY);
        }
        CommonUtils.getInstance().savePref(OPTION, String.valueOf(service.getOption()));
    }

    @Override
    public void onDestroy() {
        viewModel.setDetailRunning(false);
        service.setDetailRunning(false);
        viewModel.setViewAvail(false);
        thread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        viewModel.setDetailRunning(false);
        service.setDetailRunning(false);
        super.onPause();
    }

    @Override
    public void onResume() {
        viewModel.setDetailRunning(true);
        if(service != null) service.setDetailRunning(true);
        super.onResume();
    }
}
