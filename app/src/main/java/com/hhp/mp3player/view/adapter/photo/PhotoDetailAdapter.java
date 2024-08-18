package com.hhp.mp3player.view.adapter.photo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Photo;

import java.util.List;

public class PhotoDetailAdapter extends RecyclerView.Adapter<PhotoDetailAdapter.PhotoHolder> {
    private Context context;
    private List<Photo> listPhoto;
    public PhotoDetailAdapter(Context context, List<Photo> listPhoto){
        this.context = context;
        this.listPhoto = listPhoto;
    }
    @NonNull
    @Override
    public PhotoDetailAdapter.PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_detail, parent, false);

        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        Photo photo = listPhoto.get(position);
        Uri uri = Uri.parse(photo.getPath());
        holder.ivPhoto.setImageURI(uri);
    }


    @Override
    public int getItemCount() {
        return listPhoto.size();
    }

    public static class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView ivPhoto;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
