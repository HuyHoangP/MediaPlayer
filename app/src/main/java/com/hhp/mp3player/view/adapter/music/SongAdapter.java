package com.hhp.mp3player.view.adapter.music;

import static com.hhp.mp3player.view.activity.MainActivity.HANDLE_SONG_REMOVED;
import static com.hhp.mp3player.view.activity.MainActivity.PREPARE_PLAYER;
import static com.hhp.mp3player.view.base.BaseActivity.SLIDE_UP_AND_DOWN;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hhp.mp3player.App;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.fragment.music.SongDetailFragment;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    private final Context context;
    private final OnMainCallback callback;
    private final MutableLiveData<Song> songLD = new MutableLiveData<>();
    private List<Song> listSong;
    private OnCategoryCallback adapterCallback;
    private boolean toSongDetailFrg;

    private String listName;

    public void setListName(String listName) {
        this.listName = listName;
    }

    public SongAdapter(Context context, OnMainCallback callback, List<Song> listSong) {
        this.listSong = listSong;
        this.context = context;
        this.callback = callback;
    }

    public void setToSongDetailFrg(boolean toSongDetailFrg) {
        this.toSongDetailFrg = toSongDetailFrg;
    }

    public void setAdapterCallback(OnCategoryCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    public MutableLiveData<Song> getSongLD() {
        return songLD;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        Song song = listSong.get(position);
        holder.tvSongTitle.setTag(song);
        App.getInstance().setSongThumbnail(holder.ivThumbnail, song, true);
        holder.tvSongTitle.setText(song.getTitle());
//        holder.lnSong.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_fall_down));
        holder.frSong.setBackgroundColor(Color.parseColor(song.isSelected() ? "#99FFFFFF" : "#66FFFFFF"));
        holder.ivGifPlaying.setVisibility(song.isSelected() ? View.VISIBLE : View.INVISIBLE);
        holder.tvSongAlbum.setText(song.getAlbum());
    }

    @Override
    public int getItemCount() {
        return listSong.size();
    }

    public void updateListSong(List<Song> listSong) {
        this.listSong = listSong;
        notifyDataSetChanged();
    }

    public void updateListAddedSong(List<Song> listSong) {
        int position = getItemCount();
        this.listSong = listSong;
        notifyItemRangeInserted(position, listSong.size());
    }

    public void setSelectedSong(Song song) {
        if (listSong.isEmpty() || song == null) return;

        Song prevSong = App.getInstance().getStorage().prevSong;
        if (prevSong != null && !song.equals(prevSong)) prevSong.setSelected(false);

        if (!song.isSelected()) {
            song.setSelected(true);
            if (songLD.getValue() != null) {
                songLD.getValue().setSelected(false);
            }
        }
        songLD.postValue(song);
        notifyItemRangeChanged(0, listSong.size());
        if (adapterCallback != null) adapterCallback.notifyChanged(song);
        if (toSongDetailFrg) callback.showFragment(SongDetailFragment.TAG, null, true, SLIDE_UP_AND_DOWN);
    }


    public class SongHolder extends RecyclerView.ViewHolder {
        public FrameLayout frSong;
        public TableRow trDelete;
        public TableRow trSongFg;
        TextView tvSongTitle, tvSongAlbum;
        ImageView ivThumbnail, ivGifPlaying;

        public SongHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongAlbum = itemView.findViewById(R.id.tvSongAlbum);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            ivGifPlaying = itemView.findViewById(R.id.ivGifPlaying);
            trDelete = itemView.findViewById(R.id.trDelete);
            frSong = itemView.findViewById(R.id.frSong);
            trSongFg = itemView.findViewById(R.id.trSongFg);
            Glide.with(context).load(R.mipmap.ic_playing).into(ivGifPlaying);

            itemView.setOnClickListener(view -> {
                Song song = (Song) tvSongTitle.getTag();
                int songIndex = toSongDetailFrg ? App.getInstance().getStorage().listSongAll.indexOf(song) : listSong.indexOf(song);
                callback.callback(PREPARE_PLAYER, new Object[]{songIndex, toSongDetailFrg ? App.getInstance().getStorage().listSongAll : listSong, listName});
                setSelectedSong(song);
            });

            trDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = (Song) tvSongTitle.getTag();
                    int index = listSong.indexOf(song);
                    listSong.remove(index);
                    notifyItemRemoved(index);
                    callback.callback(HANDLE_SONG_REMOVED, new Object[]{song});
                }
            });
        }
    }
}
