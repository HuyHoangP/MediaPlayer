package com.hhp.mp3player.view.fragment.photo;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.hhp.mp3player.App;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Indicator;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.databinding.FragmentPlayMemoryBinding;
import com.hhp.mp3player.view.adapter.photo.PlayMemoryAdapter;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.CommonVM;

import java.util.List;

public class PlayMemoryFragment extends BaseFragment<FragmentPlayMemoryBinding, CommonVM> implements OnPlayMemoryFrgCallback {

    public static final String TAG = PlayMemoryFragment.class.getName();
    private List<Photo> listPhoto;
    public static final int TIMER = 5000;
    public int position;

    @Override
    public void initView() {
        listPhoto = (List<Photo>) data;
        PlayMemoryAdapter adapter = new PlayMemoryAdapter(context, this, listPhoto);
        binding.vpMemory.setAdapter(adapter);
        binding.ivBack.setOnClickListener(this);
        binding.indicator.setSize(listPhoto.size());
        binding.indicator.setCallback(this);
        binding.indicator.getListIndicatorLD().observe(this, new Observer<List<Indicator>>() {
            @Override
            public void onChanged(List<Indicator> indicators) {
                binding.indicator.startProgressIndicator();
            }
        });

        binding.vpMemory.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Drawable drawable = App.getInstance().createGradientPaletteSync(listPhoto.get(position).getPath());
                if(drawable != null) binding.vpMemory.setBackground(drawable);
                if(PlayMemoryFragment.this.position < position){
                    clickNextPhoto();
                } else if (PlayMemoryFragment.this.position > position){
                    binding.indicator.setAutoMode(false);
                }
                PlayMemoryFragment.this.position = position;
                super.onPageSelected(position);
            }
        });
    }

    @Override
    protected void clickView(View view) {
        if(view.getId() == R.id.ivBack){
            backPressed();
        }
    }

    @Override
    public Class<CommonVM> initViewModel() {
        return CommonVM.class;
    }

    @Override
    public FragmentPlayMemoryBinding initViewBinding() {
        return FragmentPlayMemoryBinding.inflate(getLayoutInflater());
    }

    @Override
    public void setPhoto(int position) {
            this.position = position;
            binding.vpMemory.setCurrentItem(position);
    }

    @Override
    public void clickNextPhoto() {
        binding.indicator.setAutoMode(true);
        binding.indicator.nextIndicator();
    }

    @Override
    public void backPressed() {
        try {
            requireActivity().runOnUiThread(() ->
                    requireActivity().getOnBackPressedDispatcher().onBackPressed());
        } catch (IllegalStateException e){
            e.printStackTrace();
            Log.i(TAG, "backPressed: DEO HIEU LUON");
        }
    }

    @Override
    public void pauseAutoMode() {
        binding.indicator.setAutoMode(false);
    }

    @Override
    public void setTextAutoMode(boolean autoMode) {
        binding.tvAutoOff.setVisibility(autoMode?View.GONE:View.VISIBLE);
    }
}
