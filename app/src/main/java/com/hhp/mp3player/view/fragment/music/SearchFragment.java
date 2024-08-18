package com.hhp.mp3player.view.fragment.music;

import static com.hhp.mp3player.view.fragment.music.AllSongFragment.LIST_NAME;

import android.animation.ValueAnimator;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;

import com.hhp.mp3player.App;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.FragmentSearchBinding;
import com.hhp.mp3player.view.adapter.music.SongAdapter;
import com.hhp.mp3player.view.base.BaseFragment;
import com.hhp.mp3player.viewmodel.SharedFrgVM;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment<FragmentSearchBinding, SharedFrgVM> {
    public static final String TAG = SearchFragment.class.getName();
    private List<Song> listSongFiltered = new ArrayList<>();
    private SongAdapter adapter;
    private Observer<Song> observerCurrentSong;

    private final ValueAnimator.AnimatorUpdateListener colorAnimator = animator -> binding.lnSearch.setBackgroundColor((int) animator.getAnimatedValue());

    @Override
    public void initView() {
        service = callback.getService();

        if(adapter == null){
            adapter = new SongAdapter(context, callback, listSongFiltered);
            adapter.setListName(LIST_NAME);
            adapter.setToSongDetailFrg(true);
        }
        binding.rvListSong.setHasFixedSize(true);
        binding.rvListSong.setAdapter(adapter);

        if(observerCurrentSong == null){
            observerCurrentSong = currentSong -> {
                App.getInstance().setMainColor(service.getCurrentSong(), colorAnimator);
                adapter.setToSongDetailFrg(false);
                adapter.setSelectedSong(currentSong);
                adapter.setToSongDetailFrg(true);
            };
        } else {
            App.getInstance().setMainColor(service.getCurrentSong(), colorAnimator);
        }

        service.getCurrentSongLD().observe(this, observerCurrentSong);


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterListSong(newText);
                return false;
            }
        });
    }

    private void filterListSong(String newText) {
        if(newText.equals("")){
            listSongFiltered.clear();
        } else {
            listSongFiltered = new ArrayList<>();
            App.getInstance().getStorage().listSongAll.forEach(song -> {
                if(song.getTitle().toLowerCase().contains(newText.toLowerCase())){
                    listSongFiltered.add(song);
                }
            });
        }
        adapter.updateListSong(listSongFiltered);

    }

    @Override
    public Class<SharedFrgVM> initViewModel() {
        return SharedFrgVM.class;
    }

    @Override
    public FragmentSearchBinding initViewBinding() {
        return FragmentSearchBinding.inflate(getLayoutInflater());
    }

}
