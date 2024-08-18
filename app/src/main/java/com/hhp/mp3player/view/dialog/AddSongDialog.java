package com.hhp.mp3player.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.hhp.mp3player.App;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.DialogAddSongBinding;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.adapter.music.AddSongAdapter;
import com.hhp.mp3player.view.base.BaseDialog;
import com.hhp.mp3player.viewmodel.SharedFrgVM;

import java.util.ArrayList;
import java.util.List;

public class AddSongDialog extends BaseDialog<DialogAddSongBinding, SharedFrgVM, OnMainCallback> {
    public static final String TAG = AddSongDialog.class.getName();

    private int mode = 1;
    private final MutableLiveData<List<Song>> listAddSongLD = new MutableLiveData<>();
    private List<Song> listAddSong = new ArrayList<>();

    public AddSongDialog(OnMainCallback callBack, SharedFrgVM viewModel) {
        super(callBack, viewModel);
    }

    public int getMode() {
        return mode;
    }

    public MutableLiveData<List<Song>> getListAddSongLD() {
        return listAddSongLD;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Override
    public void initView() {
        AddSongAdapter adapter = new AddSongAdapter(requireContext(), callBack, App.getInstance().getStorage().listSongAll);
        binding.rvListSong.setAdapter(adapter);
        adapter.getListAddSongLD().observe(this, listAddSong -> {
            AddSongDialog.this.listAddSong = listAddSong;
            binding.tvAmount.setText(String.format("%s", listAddSong.size()));
        });

        viewModel.getSelectedSongLD().observe(this, song -> {
            if (song != null) {
                App.getInstance().setMainColor(song, animator -> binding.lnAddSong.setBackgroundColor((int) animator.getAnimatedValue()));
            }
        });

        binding.trConfirm.setOnClickListener(this);
        binding.btCancel.setOnClickListener(this);
        binding.tvDesc.setOnClickListener(this);
        binding.swMode.setOnCheckedChangeListener((compoundButton, b) -> {
                binding.tvDesc.setText(String.format("SELECT SONG TO %s", b? "DELETE":"ADD"));
                binding.tvMode.setText(String.format("%s  ", b? "DELETE":"ADD"));
                mode = b?0:1;
        });
        binding.frDialogBg.setAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.item_fade_in));
        binding.cardDialog.setAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.item_pop_up));
    }


    @Override
    public Class<SharedFrgVM> initViewModel() {
        return SharedFrgVM.class;
    }

    @Override
    public DialogAddSongBinding initViewBinding() {
        return DialogAddSongBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void clickView(View view) {
        if (view.getId() == R.id.trConfirm) {
            updatePlaylist();
            animateDismiss();
        } else if (view.getId() == R.id.btCancel) {
            animateDismiss();
        } else if (view.getId() == R.id.tvDesc) {
            Log.i(TAG, "clickView: "+binding.swMode.isChecked());
        }
    }

    private void updatePlaylist() {
        listAddSongLD.postValue(listAddSong);
    }

    private void animateDismiss() {
        binding.frDialogBg.clearAnimation();
        binding.cardDialog.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.item_pop_out);
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
        binding.cardDialog.setAnimation(animation);
        binding.frDialogBg.setAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.item_fade_out));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
