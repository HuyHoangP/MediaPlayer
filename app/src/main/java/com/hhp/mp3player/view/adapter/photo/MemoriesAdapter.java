package com.hhp.mp3player.view.adapter.photo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.hhp.mp3player.R;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.dialog.DeleteDialog;
import com.hhp.mp3player.view.fragment.photo.MemoriesFragment;
import com.hhp.mp3player.view.fragment.photo.MemoryFragment;

import java.util.List;

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.MemoriesHolder> {
    public static final String TAG = MemoriesAdapter.class.getName();
    private final Context context;
    private OnMainCallback callback;
    private List<String> listDate;
    private FragmentManager childFragmentManager;

    private MemoriesFragment.OnMemoriesFragmentCallback fragmentCallback;


    @NonNull
    @Override
    public MemoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_memory, parent, false);
        return new MemoriesHolder(view);
    }

    public MemoriesAdapter(Context context, OnMainCallback callback, List<String> listDate){
        this.callback = callback;
        this.context = context;
        this.listDate = listDate;
    }

    public void setChildFragmentManager(FragmentManager childFragmentManager) {
        this.childFragmentManager = childFragmentManager;
    }

    public void setFragmentCallback(MemoriesFragment.OnMemoriesFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    public void setListDate(List<String> listDate) {
        this.listDate = listDate;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoriesHolder holder, int position) {
        String date = listDate.get(position);
        callback.getMemoryInfo(date).observe((LifecycleOwner) context, memoryInfo -> {
            if(memoryInfo != null){
                Uri uri = Uri.parse(memoryInfo.getCover());
                holder.ivCover.setImageURI(uri);
                holder.tvTitle.setText(memoryInfo.getTitle());
                holder.ivExpand.setOnClickListener(view -> {
                    DeleteDialog dialog = new DeleteDialog(callback, memoryInfo);
                    dialog.setAdapterCallback(new OnMemoriesAdapterCallback() {
                        @Override
                        public void updateAdapter() {
                            notifyItemRemoved(holder.getAdapterPosition());
                            if(listDate.isEmpty()) fragmentCallback.noMemoryUI();
                        }
                    });
                    dialog.show(childFragmentManager, DeleteDialog.TAG);
                });
            }
        });
        if(position == listDate.size() - 1){
            int dpi = context.getResources().getDisplayMetrics().densityDpi;
            int margin = 70 * dpi / 160;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, margin, 0, margin);
            holder.rlMemory.setLayoutParams(layoutParams);
        }
        holder.tvDate.setText(date);
        holder.ivCover.setOnClickListener(view -> callback.showFragment(MemoryFragment.TAG, date, true, 1));
    }


    @Override
    public int getItemCount() {
        return listDate.size();
    }


    public static class MemoriesHolder extends RecyclerView.ViewHolder{
        private final RelativeLayout rlMemory;
        TextView tvDate, tvTitle, tvEditTitle, tvRemove;
        ImageView ivExpand, ivCover;
        public MemoriesHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvEditTitle = itemView.findViewById(R.id.tvEditTitle);
            tvRemove = itemView.findViewById(R.id.tvRemove);
            ivExpand = itemView.findViewById(R.id.ivExpand);
            ivCover = itemView.findViewById(R.id.ivCover);
            rlMemory = itemView.findViewById(R.id.rlMemory);
        }
    }

    public interface OnMemoriesAdapterCallback{
        void updateAdapter();
    }
}
