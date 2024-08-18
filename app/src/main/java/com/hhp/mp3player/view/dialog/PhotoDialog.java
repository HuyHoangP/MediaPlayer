package com.hhp.mp3player.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.databinding.DialogPhotoBinding;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.adapter.photo.PhotoDetailAdapter;
import com.hhp.mp3player.view.base.BaseDialog;
import com.hhp.mp3player.viewmodel.CommonVM;

import java.util.List;

public class PhotoDialog extends BaseDialog<DialogPhotoBinding, CommonVM, OnMainCallback> {
    public static final String TAG = PhotoDialog.class.getName();
    private List<Photo> listPhoto;
    private int position;

    public PhotoDialog(OnMainCallback callBack, List<Photo> listPhoto) {
        super(callBack, null);
        this.listPhoto = listPhoto;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void initView() {
        binding.ivBack.setOnClickListener(view -> animateDismiss());
        PhotoDetailAdapter adapter = new PhotoDetailAdapter(getContext(), listPhoto);
        binding.vpPhoto.setAdapter(adapter);
        binding.vpPhoto.setCurrentItem(position, false);
        binding.lnPhoto.setAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.item_fade_in));
    }


    @Override
    public Class<CommonVM> initViewModel() {
        return CommonVM.class;
    }

    @Override
    public DialogPhotoBinding initViewBinding() {
        return DialogPhotoBinding.inflate(getLayoutInflater());
    }


    private void animateDismiss() {
        binding.lnPhoto.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.item_fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        binding.lnPhoto.setAnimation(animation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
