package com.hhp.mp3player.view.fragment.photo;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.hhp.mp3player.App;
import com.hhp.mp3player.CommonUtils;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.databinding.FragmentMemoriesBinding;
import com.hhp.mp3player.view.adapter.photo.MemoriesAdapter;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.MemoriesFrgVM;

import java.util.Comparator;
import java.util.HashSet;

public class MemoriesFragment extends BaseFragment<FragmentMemoriesBinding, MemoriesFrgVM> {
    public static final String TAG = MemoriesFragment.class.getName();
    public static final String MY_MEMORIES = "MY MEMORIES";
    private static final String DEFAULT_TITLE = "Add a Title";

    private MemoriesAdapter adapter;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public void initView() {
        initMyMemories();
        initLauncher();
        binding.btCreateMemories.setOnClickListener(this);
        binding.btAddMemory.setOnClickListener(this);
    }

    private void initLauncher() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    if ( result.getData().getClipData() != null) {
                        String date = viewModel.getCurrentTime();
                        App.getInstance().getStorage().setDate.add(date);
                        App.getInstance().getStorage().listDate.add(0 ,date);
                        ClipData photosUri = result.getData().getClipData();
                        for(int i =0; i < photosUri.getItemCount(); i++){
                            Uri photoUri = photosUri.getItemAt(i).getUri();
                            addPhotoToDB(date,photoUri);
                        }
                        String firstPhotoUri = photosUri.getItemAt(0).getUri().toString();
                        callback.insertMemoryInfo(new MemoryInfo(date, firstPhotoUri, DEFAULT_TITLE));
                        initAdapter();
                    } else if (result.getData().getData() != null) {
                        String date = viewModel.getCurrentTime();
                        App.getInstance().getStorage().setDate.add(date);
                        App.getInstance().getStorage().listDate.add(0 ,date);
                        Uri photoUri = result.getData().getData();
                        addPhotoToDB(date, photoUri);
                        callback.insertMemoryInfo(new MemoryInfo(date, photoUri.toString(), DEFAULT_TITLE));
                        initAdapter();
                    }
                }
            }
        });
    }

    private void addPhotoToDB(String date, Uri photoUri) {
        requireActivity().getContentResolver().takePersistableUriPermission(photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        callback.insertPhotos(new Photo(date, photoUri.toString()));
        CommonUtils.getInstance().saveSetPref(MY_MEMORIES, App.getInstance().getStorage().setDate);
    }

    private void initMyMemories() {
        App.getInstance().getStorage().setDate = CommonUtils.getInstance().getSetPref(MY_MEMORIES);
        if (App.getInstance().getStorage().setDate == null || App.getInstance().getStorage().setDate.isEmpty()) {
            App.getInstance().getStorage().setDate = new HashSet<>();
        } else {
            App.getInstance().getStorage().setDate = new HashSet<>(CommonUtils.getInstance().getSetPref(MY_MEMORIES));
            if(App.getInstance().getStorage().listDate.isEmpty()){
                App.getInstance().getStorage().setDate.forEach(date -> App.getInstance().getStorage().listDate.add(0, date));
                App.getInstance().getStorage().listDate.sort(Comparator.reverseOrder());
            }
            initAdapter();
        }
    }


    private void initAdapter() {
        setUI(false);
        if(adapter == null){
            adapter = new MemoriesAdapter(context, callback, App.getInstance().getStorage().listDate);
            adapter.setChildFragmentManager(getChildFragmentManager());
            adapter.setFragmentCallback(() -> setUI(true));
        } else {
            adapter.setListDate(App.getInstance().getStorage().listDate);
            adapter.notifyDataSetChanged();
        }
        binding.rvMemories.setAdapter(adapter);
    }

    private void setUI(boolean isListEmpty) {
        binding.ivMemories.setVisibility(isListEmpty?View.VISIBLE: View.GONE);
        binding.tvMemories.setVisibility(isListEmpty?View.VISIBLE: View.GONE);
        binding.btCreateMemories.setVisibility(isListEmpty?View.VISIBLE: View.GONE);
        binding.btAddMemory.setVisibility(isListEmpty?View.GONE: View.VISIBLE);
        binding.rvMemories.setVisibility(isListEmpty?View.GONE: View.VISIBLE);
    }


    private void selectPhotos() {
        if (requireActivity().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requireActivity().requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
        } else {
            choosePhotoToAdd();
        }

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
    protected void clickView(View view) {
        if(view.getId() == R.id.btCreateMemories || view.getId() == R.id.btAddMemory){
            selectPhotos();
        }
    }

    @Override
    public Class<MemoriesFrgVM> initViewModel() {
        return MemoriesFrgVM.class;
    }

    @Override
    public FragmentMemoriesBinding initViewBinding() {
        return FragmentMemoriesBinding.inflate(getLayoutInflater());
    }

    public interface OnMemoriesFragmentCallback{
        void noMemoryUI();
    }
}
