package com.hhp.mp3player.view.fragment.music;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hhp.mp3player.App;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.databinding.FragmentAllSongBinding;
import com.hhp.mp3player.view.adapter.music.SongAdapter;
import com.hhp.mp3player.view.base.BaseChildFragment;

import java.util.Collections;
import java.util.List;

public class AllSongFragment extends BaseChildFragment<FragmentAllSongBinding> {
    public static final String TAG = AllSongFragment.class.getName();
    private SongAdapter adapter;
    private ItemTouchHelper touchHelper;
    private Observer<Song> observerSelectedSong;
    private Observer<List<Song>> observerListSong;
    private Observer<Song> observerCurrentSong;
    public static final String LIST_NAME = "PLAYING: ALL SONG";


    @Override
    public FragmentAllSongBinding initViewBinding() {
        return FragmentAllSongBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initChildView(List<Song> listSong) {
        initAdapter(listSong);
        initObservers();
        adapter.getSongLD().observe(AllSongFragment.this, observerSelectedSong);
        service.getCurrentSongLD().observe(AllSongFragment.this, observerCurrentSong);
        service.getListSongLD().observe(AllSongFragment.this, observerListSong);

    }

    private void initObservers() {
        if (observerListSong == null)
            observerListSong = listSong -> {
                adapter.updateListSong(listSong);
                binding.rvListSong.scheduleLayoutAnimation();
            };

        if (observerSelectedSong == null)
            observerSelectedSong = song -> {
                if (song != null) {
                    viewModel.getSelectedSongLD().postValue(song);
                    binding.rvListSong.scrollToPosition(service.getCurrentIndex());
                }
            };
//        else viewModel.getSelectedSongLD().postValue(service.getCurrentSong());

        if (observerCurrentSong == null)
            observerCurrentSong = currentSong -> adapter.setSelectedSong(currentSong);
    }

    private void initAdapter(List<Song> listSong) {
        if (adapter == null) {
            adapter = new SongAdapter(context, callback, listSong);
            adapter.setListName(LIST_NAME);
            touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
                int draggedIndex;
                int targetIndex;

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.END);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    draggedIndex = viewHolder.getAdapterPosition();
                    targetIndex = target.getAdapterPosition();
                    Collections.swap(service.getListSongLD().getValue(), draggedIndex, targetIndex);
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
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        TableRow trSongFg = ((SongAdapter.SongHolder) viewHolder).trSongFg;
                        TableRow trDelete = ((SongAdapter.SongHolder) viewHolder).trDelete;
                        FrameLayout frSong = ((SongAdapter.SongHolder) viewHolder).frSong;
                        final ColorDrawable background = new ColorDrawable(Color.RED);
                        background.setBounds(0, frSong.getTop(), (int) (frSong.getLeft() + dX) / 6, frSong.getBottom());
                        background.draw(c);
                        getDefaultUIUtil().onDraw(c, recyclerView, trSongFg, dX / 6, dY, actionState, isCurrentlyActive);
                        trDelete.setVisibility(dX > 300 ? View.VISIBLE : View.GONE);
                    } else {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        if(adapter != null) adapter.notifyItemRangeChanged(0, App.getInstance().getStorage().listSongAll.size());
        super.onResume();
    }
}
