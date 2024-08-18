package com.hhp.mp3player.view.fragment.photo;

import android.animation.ValueAnimator;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationBarView;
import com.hhp.mp3player.App;
import com.hhp.mp3player.R;
import com.hhp.mp3player.databinding.FragmentMenuPhotoBinding;
import com.hhp.mp3player.view.adapter.photo.BotNavAdapter;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.CommonVM;

public class MenuPhotoFragment extends BaseFragment<FragmentMenuPhotoBinding, CommonVM> {
    public static final String TAG = MenuPhotoFragment.class.getName();
    private final ValueAnimator.AnimatorUpdateListener colorAnimator = animator -> {
        binding.lnMenu.setBackgroundColor((int) animator.getAnimatedValue());
        binding.botNav.setBackgroundColor((int) animator.getAnimatedValue());
    };
    @Override
    public void initView() {
        service = callback.getService();
        service.getCurrentSongLD().observe(this, song -> App.getInstance().setMainColor(service.getCurrentSong(), colorAnimator));
        setBotNavAdapter();


    }

    private void setBotNavAdapter() {
        BotNavAdapter adapter = new BotNavAdapter(this, callback);
        binding.vpCategory.setAdapter(adapter);
        binding.vpCategory.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        binding.botNav.setSelectedItemId(R.id.memories);
                        break;
                    case 1: binding.botNav.setSelectedItemId(R.id.photos);
                }
            }
        });
        binding.botNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.memories){
                    binding.vpCategory.setCurrentItem(0);
                } else if(item.getItemId() == R.id.photos) {
                    binding.vpCategory.setCurrentItem(1);
                }
                return true;
            }
        });
    }

    @Override
    public Class<CommonVM> initViewModel() {
        return CommonVM.class;
    }

    @Override
    public FragmentMenuPhotoBinding initViewBinding() {
        return FragmentMenuPhotoBinding.inflate(getLayoutInflater());
    }
}
