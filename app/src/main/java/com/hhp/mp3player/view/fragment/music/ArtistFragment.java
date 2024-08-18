package com.hhp.mp3player.view.fragment.music;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.hhp.mp3player.App;
import com.hhp.mp3player.database.entity.Category;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.FragmentArtistBinding;
import com.hhp.mp3player.view.adapter.music.CategoryAdapter;
import com.hhp.mp3player.view.adapter.music.OnCategoryCallback;
import com.hhp.mp3player.view.base.BaseChildFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ArtistFragment extends BaseChildFragment<FragmentArtistBinding> implements OnCategoryCallback {
    public static final String TAG = ArtistFragment.class.getName();
    private static final String LIST_NAME = "PLAYING: ARTIST - ";
    private HashMap<String, List<Song>> mapArtist;
    private List<Category> listCategory;
    private CategoryAdapter adapter;

    @Override
    public FragmentArtistBinding initViewBinding() {
        return FragmentArtistBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initChildView(List<Song> listSong) {
        if (mapArtist == null) initListArtist();
        initAdapter();
        binding.swipeF5.setOnRefreshListener(() -> {
            initListArtist();
            adapter.notifyDataSetChanged();
            binding.swipeF5.setRefreshing(false);
        });
    }


    private void initListArtist() {
        listCategory = new ArrayList<>();
        mapArtist = new HashMap<>();
        App.getInstance().getStorage().listSongAll.forEach(song -> {
            if (!mapArtist.containsKey(song.getArtist())) {
                mapArtist.put(song.getArtist(), new ArrayList<>());
            }
            mapArtist.get(song.getArtist()).add(song);
        });
        mapArtist.forEach((name, listSong) -> listCategory.add(new Category(listSong, name.equals("<unknown>") ? "Unknown Artist" : name)));
        listCategory.sort(Comparator.comparing(Category::getName));

    }

    private void initAdapter() {
        if (adapter == null)
            adapter = new CategoryAdapter(service, context, callback, viewModel, listCategory, CategoryAdapter.ARTIST);
        adapter.setAdapterCallback(this);
        adapter.setListName(LIST_NAME);
        binding.rvCategory.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        binding.rvCategory.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyItemRangeChanged(0, mapArtist.size());
    }

    @Override
    public void notifyChanged(Song song) {
        if (!binding.rvCategory.isComputingLayout() && adapter != null)
            adapter.notifyItemRangeChanged(0, mapArtist.size());
    }
}
