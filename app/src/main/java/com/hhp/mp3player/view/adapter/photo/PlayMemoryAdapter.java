package com.hhp.mp3player.view.adapter.photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Photo;
import com.hhp.mp3player.view.fragment.photo.OnPlayMemoryFrgCallback;

import java.util.List;

public class PlayMemoryAdapter extends RecyclerView.Adapter<PlayMemoryAdapter.PhotoHolder> {
    public static final String TAG = PlayMemoryAdapter.class.getName();
    private static final int BACK_PHOTO = 1;
    private static final int NEXT_PHOTO = 2;
    private final Context context;
    private final List<Photo> listPhoto;
    private final OnPlayMemoryFrgCallback callback;

    public PlayMemoryAdapter(Context context, OnPlayMemoryFrgCallback callback, List<Photo> listPhoto) {
        this.context = context;
        this.listPhoto = listPhoto;
        this.callback = callback;
    }

    @NonNull
    @Override
    public PlayMemoryAdapter.PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_detail, parent, false);

        return new PhotoHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        Photo photo = listPhoto.get(position);
        Uri uri = Uri.parse(photo.getPath());
        holder.ivPhoto.setImageURI(uri);
        holder.ivPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float x = event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        int action = x < view.getWidth() / 2.0? BACK_PHOTO : NEXT_PHOTO;
                        if (action == BACK_PHOTO) {
                            callback.pauseAutoMode();
                        } else {
                            callback.clickNextPhoto();
                        }
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return listPhoto.size();
    }

    public static class PhotoHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
