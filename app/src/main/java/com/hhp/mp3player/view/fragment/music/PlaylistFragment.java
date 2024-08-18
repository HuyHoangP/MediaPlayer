package com.hhp.mp3player.view.fragment.music;

import android.graphics.Canvas;
import android.view.View;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hhp.mp3player.App;
import com.hhp.mp3player.CommonUtils;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.FragmentPlaylistBinding;
import com.hhp.mp3player.view.adapter.music.SongAdapter;
import com.hhp.mp3player.view.base.BaseChildFragment;
import com.hhp.mp3player.view.dialog.AddSongDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistFragment extends BaseChildFragment<FragmentPlaylistBinding> {
    public static final String TAG = PlaylistFragment.class.getName();
    public static final String MY_PLAYLIST = "MY PLAYLIST";
    private static final String LIST_NAME = "PLAYING: MY PLAYLIST";
    private List<Song> myPlaylist;
    private SongAdapter adapter;
    private ItemTouchHelper touchHelper;
    private Observer<Song> observerSelectedSong;
    private Observer<List<Song>> observerListAddSong;
    private Observer<Song> observerCurrentSong;
    private AddSongDialog dialog;
    private Set<String> setID;


    @Override
    public FragmentPlaylistBinding initViewBinding() {
        return FragmentPlaylistBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initChildView(List<Song> listSong) {
//        CommonUtils.getInstance().clearPref(MY_PLAYLIST);
        if (myPlaylist == null) {
            initMyPlaylist();
        }
        initAdapter();
        initObservers();
        service.getCurrentSongLD().observe(this, observerCurrentSong);
        adapter.getSongLD().observe(this, observerSelectedSong);
        binding.ivAddSong.setOnClickListener(this);
    }

    private void initMyPlaylist() {
        myPlaylist = new ArrayList<>();
        setID = CommonUtils.getInstance().getSetPref(MY_PLAYLIST);
        if (setID == null) {
            setID = new HashSet<>();
        } else {
            setID = new HashSet<>(CommonUtils.getInstance().getSetPref(MY_PLAYLIST));
            HashMap<String, Song> allSong = new HashMap<>();
            App.getInstance().getStorage().listSongAll.forEach(song -> allSong.put(song.getId(), song));
            setID.forEach(id -> myPlaylist.add(allSong.get(id)));
        }
    }

    private void initObservers() {
        if (observerSelectedSong == null)
            observerSelectedSong = song -> {
                if (song != null) {
                    viewModel.getSelectedSongLD().postValue(song);
                }
            };
//        else viewModel.getSelectedSongLD().postValue(service.getCurrentSong());

        if (observerListAddSong == null)
            observerListAddSong = listAddSong -> {
                int size = myPlaylist.size();
                if (listAddSong.isEmpty()) return;
                if (dialog.getMode() == 1) {
                    listAddSong.removeAll(myPlaylist);
                    listAddSong.forEach(song -> {
                        setID.add(song.getId());
                        if (!myPlaylist.contains(song)) {
                            myPlaylist.add(song);
                        }
                    });
                    adapter.updateListAddedSong(myPlaylist);
                    Toast.makeText(context, String.format(listAddSong.size() + " SONG%s ADDED", listAddSong.size() <= 1 ? " HAS" : "S HAVE"), Toast.LENGTH_SHORT).show();
                } else if (myPlaylist.removeAll(listAddSong)) {
                    setID.clear();
                    myPlaylist.forEach(song -> setID.add(song.getId()));
                    adapter.updateListSong(myPlaylist);
                    int deleted = size - myPlaylist.size();
                    Toast.makeText(context, String.format(deleted + " SONG%s DELETED", deleted <= 1 ? " HAS" : "S HAVE"), Toast.LENGTH_SHORT).show();
                }
                CommonUtils.getInstance().saveSetPref(MY_PLAYLIST, setID);
            };

        if (observerCurrentSong == null)
            observerCurrentSong = currentSong -> adapter.setSelectedSong(currentSong);
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new SongAdapter(context, callback, myPlaylist);
            adapter.setListName(LIST_NAME);
            touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
                int draggedIndex;
                int targetIndex;

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    draggedIndex = viewHolder.getAdapterPosition();
                    targetIndex = target.getAdapterPosition();
                    Collections.swap(myPlaylist, draggedIndex, targetIndex);
                    adapter.notifyItemMoved(draggedIndex, targetIndex);
                    if (service.getCurrentIndex() == draggedIndex) {
                        service.setCurrentIndex(targetIndex);
                    } else if (service.getCurrentIndex() == targetIndex) {
                        service.setCurrentIndex(draggedIndex);
                    }
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                }

                @Override
                public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                    if (viewHolder != null) {
                        TableRow trSongFg = ((SongAdapter.SongHolder) viewHolder).trSongFg;
                        getDefaultUIUtil().onSelected(trSongFg);
                    }
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    TableRow trSongFg = ((SongAdapter.SongHolder) viewHolder).trSongFg;
                    getDefaultUIUtil().clearView(trSongFg);
                }

            });

        }
        binding.rvListSong.setAdapter(adapter);
        touchHelper.attachToRecyclerView(binding.rvListSong);
    }

    @Override
    protected void clickView(View view) {
        if (view.getId() == R.id.ivAddSong) {
            openListSongDialog();
        }
    }

    private void openListSongDialog() {
        if (dialog == null) {
            dialog = new AddSongDialog(callback, viewModel);
        }
        dialog.show(getChildFragmentManager(), AddSongDialog.TAG);
        dialog.getListAddSongLD().observe(this, observerListAddSong);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        if (adapter != null) adapter.notifyItemRangeChanged(0, myPlaylist.size());
        super.onResume();
    }
}

