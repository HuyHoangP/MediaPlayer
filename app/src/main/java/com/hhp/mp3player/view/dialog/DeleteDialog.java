package com.hhp.mp3player.view.dialog;

import static com.hhp.mp3player.view.fragment.photo.MemoriesFragment.MY_MEMORIES;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hhp.mp3player.App;
import com.hhp.mp3player.CommonUtils;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.databinding.DialogDeleteBinding;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.adapter.photo.MemoriesAdapter;
import com.hhp.mp3player.view.base.BaseDialog;
import com.hhp.mp3player.viewmodel.CommonVM;

import java.util.Set;

public class DeleteDialog extends BaseDialog<DialogDeleteBinding, CommonVM, OnMainCallback> {
    public static final String TAG = DeleteDialog.class.getName();
    private MemoryInfo memoryInfo;
    private MemoriesAdapter.OnMemoriesAdapterCallback adapterCallback;

    public DeleteDialog(OnMainCallback callBack, MemoryInfo memoryInfo) {
        super(callBack, null);
        this.memoryInfo = memoryInfo;
    }

    public void setAdapterCallback(MemoriesAdapter.OnMemoriesAdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void initView() {
        binding.btBack.setOnClickListener(view -> dismiss());
        binding.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.deleteMemory(memoryInfo.getDate());
                callBack.deleteMemoryInfo(memoryInfo.getDate());
                Set<String> setDate = App.getInstance().getStorage().setDate;
                setDate.remove(memoryInfo.getDate());
                App.getInstance().getStorage().listDate.remove(memoryInfo.getDate());
                CommonUtils.getInstance().saveSetPref(MY_MEMORIES, setDate);
                dismiss();
                adapterCallback.updateAdapter();
            }
        });
        binding.tvDelete.setText(String.format("Are you sure to delete memory\n%s from %s", memoryInfo.getTitle(), memoryInfo.getDate()));
    }


    @Override
    public Class<CommonVM> initViewModel() {
        return CommonVM.class;
    }

    @Override
    public DialogDeleteBinding initViewBinding() {
        return DialogDeleteBinding.inflate(getLayoutInflater());
    }


}
