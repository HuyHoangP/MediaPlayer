package com.hhp.mp3player.view.fragment.music;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.view.View;

import com.hhp.mp3player.App;
import com.hhp.mp3player.databinding.FragmentSettingBinding;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.view.fragment.photo.MenuPhotoFragment;
import com.hhp.mp3player.viewmodel.SharedFrgVM;

public class SettingFragment extends BaseFragment<FragmentSettingBinding, SharedFrgVM> {
    public static final String TAG = SettingFragment.class.getName();
    private final ValueAnimator.AnimatorUpdateListener colorAnimator = animator -> binding.lnSetting.setBackgroundColor((int) animator.getAnimatedValue());
    @Override
    public void initView() {
        service = callback.getService();
        service.getCurrentSongLD().observe(this, song -> App.getInstance().setMainColor(service.getCurrentSong(), colorAnimator));
        binding.trScan.setOnClickListener(view -> loadSongs());
        binding.trBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showFragment(MenuPhotoFragment.TAG, null, true, 1);
            }
        });
    }

    private void loadSongs() {
        if (requireActivity().checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requireActivity().requestPermissions(new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO
            }, 101);
        } else {
            service.loadOffline();
        }
    }

    @Override
    public Class<SharedFrgVM> initViewModel() {
        return SharedFrgVM.class;
    }

    @Override
    public FragmentSettingBinding initViewBinding() {
        return FragmentSettingBinding.inflate(getLayoutInflater());
    }
}
