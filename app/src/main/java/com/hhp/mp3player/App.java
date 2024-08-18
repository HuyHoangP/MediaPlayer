package com.hhp.mp3player;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.palette.graphics.Palette;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.hhp.mp3player.database.db.MediaDB;
import com.hhp.mp3player.database.entity.Song;

import java.io.IOException;

public class App extends Application {
    public static final String CHANNEL_ID = "MP3_PLAYER";
    private static App instance;
    private Storage storage;
    private MediaDB db;

    public MediaDB getDb() {
        return db;
    }

    public static App getInstance() {
        return instance;
    }

    public Storage getStorage() {
        return storage;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        storage = new Storage();
        synchronized (MediaDB.class){
            db = Room.databaseBuilder(this, MediaDB.class, "CSDL-BaiHat")
                    .build();
        }
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


    //NATIVE METHOD HERE
    public void setMainColor(Song currentSong, ValueAnimator.AnimatorUpdateListener colorAnimator) {
        if(currentSong == null) return;
        byte[] data = getThumbnailByteArr(currentSong.getPath());
        if (data == null) {
            App.getInstance().getStorage().mainColor = Color.BLACK;
            App.getInstance().getStorage().thumbnail = null;
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            App.getInstance().getStorage().thumbnail = bitmap;
            createPaletteSync(bitmap);
        }

        if (colorAnimator != null) {
            ValueAnimator colorAnimation = new ValueAnimator();
            colorAnimation.setIntValues(App.getInstance().getStorage().prevColor, App.getInstance().getStorage().mainColor);
            colorAnimation.setEvaluator(new ArgbEvaluator());
            colorAnimation.addUpdateListener(colorAnimator);
            colorAnimation.setDuration(1500);
            colorAnimation.start();
            App.getInstance().getStorage().prevColor = App.getInstance().getStorage().mainColor;
        }
    }

    public void setSongThumbnail(ImageView ivThumbnail, Song song, boolean isCenterCrop) {
        byte[] data = getThumbnailByteArr(song.getPath());
        if (data == null) {
            ivThumbnail.setImageResource(R.drawable.ic_music);
            return;
        }
        if (isCenterCrop) {
            Glide.with(this).load(data).centerCrop().into(ivThumbnail);
        } else {
            Glide.with(this).load(data).into(ivThumbnail);
        }
    }


    private byte[] getThumbnailByteArr(String absolutePath) {
        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(absolutePath);
            byte[] data = retriever.getEmbeddedPicture();
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }

    private void createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch vibrantSwatch = p.getDarkVibrantSwatch();
        if (vibrantSwatch == null) vibrantSwatch = p.getDarkMutedSwatch();
        if (vibrantSwatch != null) { // return null if there is no image
            App.getInstance().getStorage().mainColor = vibrantSwatch.getRgb();
        }
    }

    public Drawable createGradientPaletteSync(String uri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uri));
            Palette p = Palette.from(bitmap).generate();
            int color1 = p.getDarkMutedColor(0);
            int color2 = p.getDominantColor(0);
            if(color1 == color2) color2 = p.getDarkVibrantColor(0);
            return new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{color1, color2});

        } catch (IOException e) {
            return null;
        }
    }
}
