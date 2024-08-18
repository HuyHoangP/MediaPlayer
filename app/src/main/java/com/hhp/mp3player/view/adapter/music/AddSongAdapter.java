package com.hhp.mp3player.view.adapter.music;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.hhp.mp3player.App;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.view.OnMainCallback;

import java.util.ArrayList;
import java.util.List;

public class AddSongAdapter extends RecyclerView.Adapter<AddSongAdapter.SongHolder> {
    public static final String TAG = AddSongAdapter.class.getName();
    private final Context context;
    private final OnMainCallback callback;
    private final MutableLiveData<Song> songLD = new MutableLiveData<>();
    private List<Song> listSong;

    private MutableLiveData<List<Song>> listAddSongLD = new MutableLiveData<>(new ArrayList<>());

    public AddSongAdapter(Context context, OnMainCallback callback, List<Song> listSong) {
        this.listSong = listSong;
        this.context = context;
        this.callback = callback;
    }

    public MutableLiveData<List<Song>> getListAddSongLD() {
        return listAddSongLD;
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
        holder.tvSongAlbum.setText(song.getAlbum());
        holder.ckAddSong.setVisibility(View.VISIBLE);
        holder.ivGifPlaying.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listSong.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        TextView tvSongTitle, tvSongAlbum;
        ImageView ivThumbnail, ivGifPlaying;
        public FrameLayout frSong;
        public TableRow trDelete;
        public TableRow trSongFg;
        public CheckBox ckAddSong;

        public SongHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongAlbum = itemView.findViewById(R.id.tvSongAlbum);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            ivGifPlaying = itemView.findViewById(R.id.ivGifPlaying);
            trDelete = itemView.findViewById(R.id.trDelete);
            frSong = itemView.findViewById(R.id.frSong);
            trSongFg= itemView.findViewById(R.id.trSongFg);
            ckAddSong = itemView.findViewById(R.id.ckAddSong);

            itemView.setOnClickListener(view -> {
                Song song = (Song) tvSongTitle.getTag();
                ckAddSong.setChecked(!ckAddSong.isChecked());
                frSong.setBackgroundColor(Color.parseColor(ckAddSong.isChecked() ? "#99FFFFFF" : "#00FFFFFF"));
                if(ckAddSong.isChecked()){
                    listAddSongLD.getValue().add(song);
                } else {
                    listAddSongLD.getValue().remove(song);
                }
                listAddSongLD.postValue(listAddSongLD.getValue());
            });
        }
    }
}
