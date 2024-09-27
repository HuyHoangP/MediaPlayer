package com.hhp.mp3player.view.fragment.photo;

import static android.app.Activity.RESULT_OK;
import static com.hhp.mp3player.view.fragment.photo.MemoriesFragment.MY_MEMORIES;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;

import com.hhp.mp3player.App;
import com.hhp.mp3player.CommonUtils;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.databinding.FragmentMemoryBinding;
import com.hhp.mp3player.view.adapter.photo.MemoryAdapter;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.CommonVM;

import java.util.List;
import java.util.Set;

public class MemoryFragment extends BaseFragment<FragmentMemoryBinding, CommonVM> implements OnMemoryFrgCallback{
    public static final String TAG = MemoryFragment.class.getName();
    public static final String DELETE_MODE = "DELETE MODE";
    public static final String EDIT_TITLE_MODE = "EDIT TITLE MODE";
    private static final String CHOOSE_COVER = "CHOOSE COVER";
    private String mode;
    private Observer<List<Photo>> listPhotoObserver;

    private List<Photo> listPhoto;
    private MemoryInfo memoryInfo;
    private MemoryAdapter adapter;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Observer<MemoryInfo> memoryInfoObserver;

    @Override
    public void initView() {
        String date = (String) data;
        initObserver();
        initLauncher();
        callback.getMemoryInfo(date).observe(this, memoryInfoObserver);

        binding.ivAdd.setOnClickListener(this);
        binding.ivDelete.setOnClickListener(this);
        binding.ivCancel.setOnClickListener(this);
        binding.ivConfirm.setOnClickListener(this);
        binding.tvTitle.setOnClickListener(this);
        binding.ivChangeCover.setOnClickListener(this);
        binding.ivMemory.setOnClickListener(this);
    }

    private void initObserver() {
        if(memoryInfoObserver == null){
            memoryInfoObserver = memoryInfo -> {
                MemoryFragment.this.memoryInfo = memoryInfo;
                binding.tvDate.setText(memoryInfo.getDate());
                binding.tvTitle.setText(memoryInfo.getTitle());
                binding.ivMemory.setImageURI(Uri.parse(memoryInfo.getCover()));
                callback.getListPhotoLD(memoryInfo.getDate()).observe(MemoryFragment.this, listPhotoObserver);
            };
        }
        if (listPhotoObserver == null) {
            listPhotoObserver = photos -> {
                if(photos.isEmpty()){
                    callback.deleteMemoryInfo(memoryInfo.getDate());
                    Set<String> setDate = App.getInstance().getStorage().setDate;
                    setDate.remove(memoryInfo.getDate());
                    App.getInstance().getStorage().listDate.remove(memoryInfo.getDate());
                    CommonUtils.getInstance().saveSetPref(MY_MEMORIES, setDate);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    return;
                }
                if (adapter == null) {
                    adapter = new MemoryAdapter(context, callback, photos);
                    adapter.setChildFragment(getChildFragmentManager());
                    adapter.setFrgCallback(MemoryFragment.this);
                    adapter.setMemoryInfo(memoryInfo);
                } else {
                    adapter.updateListPhoto(photos);
                }
                listPhoto = photos;
                binding.rvSelectPhotos.setAdapter(adapter);
            };
        }
    }

    private void initLauncher() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData().getClipData() != null) {
                    ClipData photosUri = result.getData().getClipData();
                    for (int i = 0; i < photosUri.getItemCount(); i++) {
                        Uri photoUri = photosUri.getItemAt(i).getUri();
                        addPhotoToDB(memoryInfo.getDate(), photoUri);
                    }
                } else if (result.getData().getData() != null) {
                    Uri photoUri = result.getData().getData();
                    addPhotoToDB(memoryInfo.getDate(), photoUri);
                }
            }
        });
    }

    private void addPhotoToDB(String date, Uri photoUri) {
        requireActivity().getContentResolver().takePersistableUriPermission(photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        callback.insertPhotos(new Photo(date, photoUri.toString()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivAdd) {
            choosePhotoToAdd();
        } else if (view.getId() == R.id.ivDelete) {
            deleteMode(true);
        } else if (view.getId() == R.id.ivCancel) {
            cancel();
        } else if (view.getId() == R.id.ivConfirm) {
            confirm();
        } else if (view.getId() == R.id.tvTitle) {
            editTitle();
        } else if (view.getId() == R.id.ivChangeCover) {
            changeCover();
        } else if (view.getId() == R.id.ivMemory) {
            callback.showFragment(PlayMemoryFragment.TAG, listPhoto, true, 2);
        }
    }

    private void changeCover() {
        mode = CHOOSE_COVER;
        adapter.setChooseCoverMode(true);
        binding.ivAdd.setVisibility(View.GONE);
        binding.ivDelete.setVisibility(View.GONE);
        binding.tvChooseCover.setVisibility(View.VISIBLE);
        binding.frMemory.setVisibility(View.GONE);
    }

    private void editTitle() {
        mode = EDIT_TITLE_MODE;
        binding.rvSelectPhotos.setVisibility(View.GONE);
        binding.ivMemory.setEnabled(false);
        binding.ivChangeCover.setEnabled(false);
        binding.edtTitle.setText(binding.tvTitle.getText());
        binding.edtTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setVisibility(View.GONE);
        selectMode(true);
    }

    private void cancel() {
        switch (mode) {
            case DELETE_MODE:
                deleteMode(false);
                adapter.cancelDelete();
                break;
            case EDIT_TITLE_MODE:
                binding.rvSelectPhotos.setVisibility(View.VISIBLE);
                binding.edtTitle.setVisibility(View.GONE);
                binding.tvTitle.setVisibility(View.VISIBLE);
                binding.ivMemory.setEnabled(true);
                binding.ivChangeCover.setEnabled(true);
                selectMode(false);
                break;
        }
    }

    private void confirm() {
        switch (mode) {
            case DELETE_MODE:
                deleteMode(false);
                adapter.getMapDeletePhoto().forEach((photo, integer) -> {
                    listPhoto.remove(photo);
                    callback.deletePhotos(photo);
                    if(!listPhoto.isEmpty() && photo.getPath().equals(memoryInfo.getCover())){
                        callback.updateMemoryInfo(new MemoryInfo(memoryInfo.getDate(), listPhoto.get(0).getPath(), memoryInfo.getTitle()));
                    }

                });
                break;
            case EDIT_TITLE_MODE:
                String newTitle = String.valueOf(binding.edtTitle.getText());
                callback.updateMemoryInfo(new MemoryInfo(memoryInfo.getDate(), memoryInfo.getCover(), newTitle));
                binding.tvTitle.setText(newTitle);
                binding.rvSelectPhotos.setVisibility(View.VISIBLE);
                binding.edtTitle.setVisibility(View.GONE);
                binding.tvTitle.setVisibility(View.VISIBLE);
                binding.ivMemory.setEnabled(true);
                binding.ivChangeCover.setEnabled(true);
                selectMode(false);
                break;
        }
    }


    private void deleteMode(boolean deleteMode) {
        mode = DELETE_MODE;
        adapter.setDeleteMode(deleteMode);
        boolean mode = adapter.isDeleteMode();
        binding.tvDelete.setVisibility(mode ? View.VISIBLE : View.GONE);
        binding.frMemory.setVisibility(mode ? View.GONE : View.VISIBLE);
        selectMode(deleteMode);
    }

    private void selectMode(boolean mode) {
        binding.ivConfirm.setVisibility(mode ? View.VISIBLE : View.GONE);
        binding.ivCancel.setVisibility(mode ? View.VISIBLE : View.GONE);
        binding.ivAdd.setVisibility(mode ? View.GONE : View.VISIBLE);
        binding.ivDelete.setVisibility(mode ? View.GONE : View.VISIBLE);
    }

    private void choosePhotoToAdd() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galleryLauncher.launch(Intent.createChooser(intent, "Select photos"));
    }

    @Override
    public Class<CommonVM> initViewModel() {
        return CommonVM.class;
    }

    @Override
    public FragmentMemoryBinding initViewBinding() {
        return FragmentMemoryBinding.inflate(getLayoutInflater());
    }

    @Override
    public void backToNormal() {
        binding.frMemory.setVisibility(View.VISIBLE);
        binding.tvTitle.setVisibility(View.GONE);
        selectMode(false);
    }
}
