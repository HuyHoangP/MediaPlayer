package com.hhp.mp3player.view.fragment.music;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.hhp.mp3player.App;
import com.hhp.mp3player.database.entity.Category;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.FragmentAlbumBinding;
import com.hhp.mp3player.view.adapter.music.CategoryAdapter;
import com.hhp.mp3player.view.adapter.music.OnCategoryCallback;
import com.hhp.mp3player.view.base.BaseChildFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AlbumFragment extends BaseChildFragment<FragmentAlbumBinding> implements OnCategoryCallback {
    public static final String TAG = AlbumFragment.class.getName();
    private HashMap<String, List<Song>> mapAlbum;
    private List<Category> listCategory;
    private CategoryAdapter adapter;
    private static final String LIST_NAME = "PLAYING: ALBUM - ";


    @Override
    public FragmentAlbumBinding initViewBinding() {
        return FragmentAlbumBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initChildView(List<Song> listSong) {
        if(mapAlbum == null) initListAlbum();
        initAdapter();
    }


    private void initListAlbum() {
        listCategory = new ArrayList<>();
        mapAlbum = new HashMap<>();
        App.getInstance().getStorage().listSongAll.forEach(song -> {
            if(!mapAlbum.containsKey(song.getAlbum())){
                mapAlbum.put(song.getAlbum(), new ArrayList<>());
            }
            mapAlbum.get(song.getAlbum()).add(song);
        });
        mapAlbum.forEach((name, listSong) -> listCategory.add(new Category(listSong, name)));
        listCategory.sort(Comparator.comparing(Category::getName));

    }

    private void initAdapter() {
        if(adapter== null) adapter = new CategoryAdapter(service, context, callback, viewModel , listCategory, CategoryAdapter.ALBUM);
        adapter.setAdapterCallback(this);
        adapter.setListName(LIST_NAME);
        binding.rvCategory.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        binding.rvCategory.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null) adapter.notifyItemRangeChanged(0, mapAlbum.size());
    }

    @Override
    public void notifyChanged(Song song) {
        if(!binding.rvCategory.isComputingLayout() && adapter != null)
            adapter.notifyItemRangeChanged(0, mapAlbum.size());
    }
}
