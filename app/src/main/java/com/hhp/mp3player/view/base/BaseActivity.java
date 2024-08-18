package com.hhp.mp3player.view.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.hhp.mp3player.R;
import com.hhp.mp3player.service.MusicService;
import com.hhp.mp3player.view.OnMainCallback;

import java.lang.reflect.Constructor;

public abstract class BaseActivity<T extends ViewBinding, V extends ViewModel> extends FragmentActivity implements IView<T, V>, OnMainCallback {
    protected T binding;
    protected V viewModel;
    protected MusicService service;
    protected BaseFragment<?, ?> fragment;
    public static final int SLIDE_UP_AND_DOWN = 1;
    public static final int SLIDE_LEFT_AND_RIGHT = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = initViewBinding();
        viewModel = new ViewModelProvider(this).get(initViewModel());
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        initView();
    }

    @Override
    public void showFragment(String tag, Object data, Boolean isBack, int slideAnim) {
        FragmentTransaction fragmentTransaction = initFragmentTransaction(tag, data, isBack, slideAnim);
        if (fragmentTransaction != null)
            fragmentTransaction.replace(R.id.frMainAct, fragment, tag).commit();
    }

    private FragmentTransaction initFragmentTransaction(String tag, Object data, Boolean isBack, int slideAnim) {
        try {
            Class<?> clazz = Class.forName(tag);
            Constructor<?> constructor = clazz.getConstructor();
            fragment = (BaseFragment<?, ?>) constructor.newInstance();
            fragment.setCallback(this);
            fragment.setData(data);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if(slideAnim == SLIDE_UP_AND_DOWN){
                fragmentTransaction.setCustomAnimations(R.anim.item_slide_up_fade_in,
                        R.anim.item_fade_out,
                        R.anim.item_fade_in,
                        R.anim.item_slide_down_fade_out);
            } else if (slideAnim == SLIDE_LEFT_AND_RIGHT) {
                fragmentTransaction.setCustomAnimations(R.anim.item_slide_in,
                        R.anim.item_fade_out,
                        R.anim.item_fade_in,
                        R.anim.item_slide_out);
            }

            if (isBack) {
                fragmentTransaction.addToBackStack(null);
            }
            return fragmentTransaction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
