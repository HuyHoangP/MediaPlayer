package com.hhp.mp3player.view.base;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.hhp.mp3player.App;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.viewmodel.SharedFrgVM;

import java.util.List;

public abstract class BaseChildFragment<T extends ViewBinding> extends BaseFragment<T , SharedFrgVM> {
    private Observer<List<Song>> observerListSong = listSong -> {
        service = callback.getService();
        App.getInstance().getStorage().listSongAll = listSong;
        initChildView(listSong);
    };
    @Override
    public void initView() {
        if(getParentFragment() != null){
            viewModel = new ViewModelProvider(getParentFragment()).get(initViewModel());
            setCallback((OnMainCallback) getParentFragment().getActivity());

            viewModel.getListSongLD().observe(this, observerListSong);
        }
    }
    protected abstract void initChildView(List<Song> listSong);

    @Override
    public Class<SharedFrgVM> initViewModel() {
        return SharedFrgVM.class;
    }
    
}
