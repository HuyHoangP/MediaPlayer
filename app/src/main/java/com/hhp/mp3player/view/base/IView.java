package com.hhp.mp3player.view.base;

import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

public interface IView <T extends ViewBinding, V extends ViewModel> {
    void initView();
    Class<V> initViewModel();
    T initViewBinding();
}
