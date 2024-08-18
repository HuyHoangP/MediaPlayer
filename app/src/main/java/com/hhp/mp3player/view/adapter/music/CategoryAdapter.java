package com.hhp.mp3player.view.adapter.music;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Category;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.service.MusicService;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.viewmodel.SharedFrgVM;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder>{
    public static final String ARTIST = "ARTIST";
    public static final String ALBUM = "ALBUM";
    private String categoryType;
    private Context context;
    private List<Category> listCategory;
    private OnMainCallback callback;
    private MusicService service;
    private Observer<Song> observerSelectedSong;
    private Observer<Song> observerCurrentSong;
    private SharedFrgVM viewModel;
    private SongAdapter nestedAdapter;
    private OnCategoryCallback adapterCallback;

    private String listName;

    public void setListName(String listName) {
        this.listName = listName;
    }

    public SongAdapter getNestedAdapter() {
        return nestedAdapter;
    }

    public CategoryAdapter(MusicService service, Context context, OnMainCallback callback, SharedFrgVM viewModel, List<Category> listCategory, String categoryType) {
        this.context = context;
        this.listCategory = listCategory;
        this.callback = callback;
        this.viewModel = viewModel;
        this.service = service;
        this.categoryType = categoryType;
    }

    public void setAdapterCallback(OnCategoryCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Category category = listCategory.get(position);
        Uri albumUri = Uri.parse(category.getListSong().get(0).getAlbumUri());
        holder.tvCategory.setText(category.getName());
        if(categoryType.equals(ALBUM)){
            Glide.with(context).load(albumUri).centerCrop().into(holder.ivAlbumArt);
        }

        boolean isExpanded = category.isExpanded();
        holder.ivExpand.setImageResource(isExpanded ? R.drawable.ic_expand_less: R.drawable.ic_expand_more);
        holder.rvListSong.setVisibility(isExpanded? View.VISIBLE: View.GONE);

        holder.lnCategory.setOnClickListener(view -> {
            category.setExpanded(!isExpanded);
            notifyItemChanged(holder.getAdapterPosition());
        });

        nestedAdapter = new SongAdapter(context, callback, category.getListSong());
        nestedAdapter.setListName(listName + category.getName().toUpperCase());
        holder.rvListSong.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvListSong.setAdapter(nestedAdapter);
        nestedAdapter.setAdapterCallback(adapterCallback);

        initObserver();
        service.getCurrentSongLD().observe((LifecycleOwner) context, observerCurrentSong);
        nestedAdapter.getSongLD().observe((LifecycleOwner) context, observerSelectedSong);

    }

    private void initObserver() {
        if (observerSelectedSong == null)
            observerSelectedSong = song -> {
                if (song != null) {
                    viewModel.getSelectedSongLD().postValue(song);
                }
            };
//        else  viewModel.getSelectedSongLD().postValue(service.getCurrentSong());

        if (observerCurrentSong == null)
            observerCurrentSong = currentSong -> nestedAdapter.setSelectedSong(currentSong);
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }



    public static class CategoryHolder extends RecyclerView.ViewHolder{
        public LinearLayout lnCategory;
        public ImageView ivAlbumArt;
        public ImageView ivExpand;
        public TextView tvCategory;
        public RecyclerView rvListSong;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            lnCategory = itemView.findViewById(R.id.lnCategory);
            ivAlbumArt = itemView.findViewById(R.id.ivAlbumArt);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivExpand = itemView.findViewById(R.id.ivExpand);
            rvListSong = itemView.findViewById(R.id.rvListSong);
        }
    }
}
