package com.hhp.mp3player.view.adapter.photo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.MemoryInfo;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.dialog.PhotoDialog;
import com.hhp.mp3player.view.fragment.photo.OnMemoryFrgCallback;

import java.util.HashMap;
import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.PhotoHolder> {
    public static final String TAG = MemoryAdapter.class.getName();
    private Context context;
    private List<Photo> listPhoto;
    private HashMap<Photo, Integer> mapDeletePhoto = new HashMap<>();
    private OnMainCallback mainCallback;
    private OnMemoryFrgCallback frgCallback;
    private FragmentManager childFragmentManager;
    private boolean deleteMode;
    private boolean chooseCoverMode;
    private MemoryInfo memoryInfo;

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoHolder(view);
    }

    public MemoryAdapter(Context context, OnMainCallback mainCallback, List<Photo> listPhoto){
        this.context = context;
        this.mainCallback = mainCallback;
        this.listPhoto = listPhoto;
    }

    public void cancelDelete(){
        mapDeletePhoto.forEach((photo, position) -> notifyItemChanged(position));
        mapDeletePhoto.clear();
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        Photo photo = listPhoto.get(position);
        Uri uri = Uri.parse(photo.getPath());
        holder.ivPhoto.setImageURI(uri);
        holder.ivSelect.setVisibility(View.GONE);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteMode){
                    choosePhotos(holder,photo);
                } else if (chooseCoverMode) {
                    mainCallback.updateMemoryInfo(new MemoryInfo(memoryInfo.getDate(), photo.getPath(), memoryInfo.getTitle()));
                    setChooseCoverMode(false);
                    frgCallback.backToNormal();
                } else {
                    PhotoDialog dialog = new PhotoDialog(mainCallback, listPhoto);
                    dialog.setPosition(holder.getAdapterPosition());
                    dialog.show(childFragmentManager, PhotoDialog.TAG);
                }
            }
        });
    }

    private void choosePhotos(PhotoHolder holder, Photo photo) {
        int position = holder.getAdapterPosition();
        if(mapDeletePhoto.remove(photo, position)){
            holder.ivSelect.setVisibility(View.GONE);
        } else {
            mapDeletePhoto.put(photo, position);
            holder.ivSelect.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listPhoto.size();
    }

    public void setChildFragment(FragmentManager childFragmentManager) {
        this.childFragmentManager = childFragmentManager;
    }

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
    }

    public void setChooseCoverMode(boolean chooseCoverMode) {
        this.chooseCoverMode = chooseCoverMode;
    }

    public void setFrgCallback(OnMemoryFrgCallback frgCallback) {
        this.frgCallback = frgCallback;
    }

    public void setMemoryInfo(MemoryInfo memoryInfo) {
        this.memoryInfo = memoryInfo;
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }

    public HashMap<Photo, Integer> getMapDeletePhoto() {
        return mapDeletePhoto;
    }

    public void updateListPhoto(List<Photo> photos) {
        int lastPos = listPhoto.size();
        int diff = photos.size() - lastPos;
        listPhoto = photos;
        if(diff > 0){
            notifyItemRangeInserted(lastPos, listPhoto.size());
        } else {
            mapDeletePhoto.forEach((photo, position) -> notifyItemRemoved(position));
        }
    }

    public static class PhotoHolder extends RecyclerView.ViewHolder{
        ImageView ivSelect, ivPhoto;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            ivSelect = itemView.findViewById(R.id.ivSelect);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
