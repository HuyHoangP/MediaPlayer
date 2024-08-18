package com.hhp.mp3player.view.fragment.photo;

import com.hhp.mp3player.databinding.FragmentPhotosBinding;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.CommonVM;

public class PhotosFragment extends BaseFragment<FragmentPhotosBinding, CommonVM> {
    @Override
    public void initView() {

    }

    @Override
    public Class<CommonVM> initViewModel() {
        return CommonVM.class;
    }

    @Override
    public FragmentPhotosBinding initViewBinding() {
        return FragmentPhotosBinding.inflate(getLayoutInflater());
    }
}
