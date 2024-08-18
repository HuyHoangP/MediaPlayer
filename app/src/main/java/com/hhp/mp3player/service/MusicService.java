package com.hhp.mp3player.service;


import static com.hhp.mp3player.App.CHANNEL_ID;
import static com.hhp.mp3player.view.activity.MainActivity.ACTION_EXIT;
import static com.hhp.mp3player.view.fragment.music.PlaylistFragment.MY_PLAYLIST;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.hhp.mp3player.App;
import com.hhp.mp3player.CommonUtils;
import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Song;
import com.hhp.mp3player.view.OnMainCallback;
import com.hhp.mp3player.view.activity.MainActivity;
import com.hhp.mp3player.view.fragment.music.AllSongFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MusicService extends Service {
    public static final String TAG = MusicService.class.getName();
    public static final int STATE_PAUSED = 0;
    public static final int STATE_PLAYING = 1;
    public static final int OPTION_NORMAL = 0;
    public static final int OPTION_REPEAT = 1;
    public static final int OPTION_SHUFFLE = 2;
    private static final String CLICK_EVENT = "CLICK_EVENT";
    private static final String TOGGLE_EVENT = "TOGGLE_EVENT";
    private static final String NEXT_EVENT = "NEXT_EVENT";
    private static final String BACK_EVENT = "BACK_EVENT";
    private final MutableLiveData<List<Song>> listSongLD = new MutableLiveData<>(new ArrayList<>());
    private final List<Integer> listShuffle = new ArrayList<>();
    private List<Song> currentPlaylist;
    private MediaPlayer player;
    private int status = STATE_PAUSED;
    private final MutableLiveData<Integer> statusLD = new MutableLiveData<>(status);
    private int option = OPTION_NORMAL;
    private int currentIndex;
    private Song currentSong;
    private final MutableLiveData<Song> currentSongLD = new MutableLiveData<>(currentSong);
    private Notification notification;
    private NotificationManagerCompat notificationManager;
    private RemoteViews views;
    private NotificationTarget target;
    private boolean isServiceRunning = true;
    private boolean isMenuRunning ;
    private boolean isDetailRunning;
    private OnMainCallback callback;
    private String currentListName = AllSongFragment.LIST_NAME;

    public String getCurrentListName() {
        return currentListName;
    }

    public void setCurrentListName(String currentListName) {
        this.currentListName = currentListName;
    }

    public void setCallback(OnMainCallback callback) {
        this.callback = callback;
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("MissingPermission")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            views.setProgressBar(R.id.progressBar, getSongDuration(), getCurrentTime(), false);
            notificationManager.notify(1001, notification);
            return false;
        }
    });

    public void setCompletionListener(MediaPlayer.OnCompletionListener completionListener) {
        player.setOnCompletionListener(completionListener);
    }

    public void setDetailRunning(boolean detailRunning) {
        isDetailRunning = detailRunning;
    }

    public void setMenuRunning(boolean menuRunning) {
        isMenuRunning = menuRunning;
    }

    public int getStatus() {
        return status;
    }

    public MutableLiveData<Integer> getStatusLD() {
        return statusLD;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public List<Integer> getListShuffle() {
        return listShuffle;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public MutableLiveData<Song> getCurrentSongLD() {
        return currentSongLD;
    }

    public MutableLiveData<List<Song>> getListSongLD() {
        return listSongLD;
    }

    public void setCurrentPlaylist(List<Song> currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void loadOffline() {
        listSongLD.getValue().clear();
        callback.deleteAllSong();
        CommonUtils.getInstance().clearPref(MY_PLAYLIST);
        listShuffle.clear();
        Cursor c = App.getInstance().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.TITLE + " ASC");
        c.moveToFirst();
        int colPath = c.getColumnIndex(MediaStore.Audio.Media.DATA);
        Log.i(TAG, "loadOffline: " + MediaStore.Audio.Media.TITLE);
        int colTitle = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int colAlbum = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int colArtist = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        while (!c.isAfterLast()) {
            String title = c.getString(colTitle);
            String path = c.getString(colPath);
            String album = c.getString(colAlbum);
            String artist = c.getString(colArtist);
            Uri imageUri = getAlbumArt(new File(path));
            String imageStr = imageUri == null ? null : imageUri.toString();
            Song song = new Song(title, path, album, artist, imageStr);
            if(!listSongLD.getValue().contains(song)){
                listSongLD.getValue().add(song);
                callback.insertSong(song);
            }
            c.moveToNext();
        }
        listSongLD.postValue(listSongLD.getValue());
        c.close();
        Log.i(TAG, "loadOffline: " + listSongLD.getValue().toString());
        Toast.makeText(this, "SCAN COMPLETE!", Toast.LENGTH_SHORT).show();
    }

    private Uri getAlbumArt(File file) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media.ALBUM_ID};
        String path = file.getAbsolutePath();
        if (path.contains("'")) {
            path = path.replace("'", "''");
        }
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " + MediaStore.Audio.Media.DATA + " = '"
                + path + "'";
        final Cursor cursor = App.getInstance().getContentResolver().query(uri, cursor_cols, where, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }
        return null;
    }


    public void preparePlayer(int currentIndex) {
        this.currentIndex = currentIndex;
        App.getInstance().getStorage().prevSong = currentSong;
        currentSong = currentPlaylist.get(currentIndex);
        player.reset();
        if (currentIndex == -1) return;
        try {
            player.setDataSource(currentPlaylist.get(currentIndex).getPath());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (status == STATE_PLAYING) {
            player.start();
        }
    }

    public void toggle() {
        switch (status) {
            case STATE_PAUSED:
                player.start();
                status = STATE_PLAYING;
                onStatusChanged();
                break;
            case STATE_PLAYING:
                player.pause();
                status = STATE_PAUSED;
                onStatusChanged();
                break;
        }
    }

    private void onStatusChanged() {
        statusLD.postValue(status);
        setToggleUI();
    }

    public void onSongCompleted() {
        if (getCurrentTime() < 300) return; //Flag for unknown bug
        if (option == OPTION_REPEAT) {
            preparePlayer(currentIndex);
        } else {
            next();
        }

    }

    private void shuffle() {
        if (listShuffle.isEmpty()) {
            for (int i = 0; i < currentPlaylist.size(); i++) {
                listShuffle.add(i);
            }
            Collections.shuffle(listShuffle);
        }
        currentIndex = listShuffle.get(0);
        listShuffle.remove(0);
    }

    public void next() {
        if (option == OPTION_SHUFFLE) {
            shuffle();
        } else {
            currentIndex++;
            if (currentIndex >= currentPlaylist.size()) {
                currentIndex = 0;
            }
        }
        preparePlayer(currentIndex);
        currentSongLD.postValue(currentSong);
        Log.i(TAG, "next: " + currentIndex +"." +currentSong.getTitle() + "\n" + currentPlaylist.size());
        checkActivityPaused();
    }

    private void checkActivityPaused() {
        if(!isDetailRunning && !isMenuRunning){
            App.getInstance().setMainColor(currentSong, null);
            setSongNotificationUI();
        }
    }

    public void back() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = currentPlaylist.size() - 1;
        }
        preparePlayer(currentIndex);
        currentSongLD.postValue(currentSong);
        checkActivityPaused();
    }


    public String getCurrentTimeText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(new Date(player.getCurrentPosition()));
    }

    public int getCurrentTime() {
        return player.getCurrentPosition();
    }

    public String getSongDurationText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(new Date(player.getDuration()));
    }

    public int getSongDuration(){
        return currentSong == null? 0 :player.getDuration();
    }

    public void seekTo(int progress) {
        player.seekTo(progress);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initPlayer();
        initNotification();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String key = intent.getStringExtra(CLICK_EVENT);
        if (key == null || listSongLD.getValue().isEmpty()) return START_NOT_STICKY;
        switch (key) {
            case TOGGLE_EVENT:
                toggle();
                break;
            case NEXT_EVENT:
                next();
                break;
            case BACK_EVENT:
                back();
                break;
            case ACTION_EXIT:
                sendBroadcast(new Intent(ACTION_EXIT));
                break;
        }
        return START_NOT_STICKY;
    }


    private void initPlayer() {
        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
    }

    @SuppressLint("MissingPermission")
    private void initNotification() {
        Intent reopenIntent = new Intent(this, MainActivity.class);
        reopenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent reopenPIntent = PendingIntent.getActivity(this, 0, reopenIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);

        views = new RemoteViews(getPackageName(), R.layout.item_notification);
        notificationManager = NotificationManagerCompat.from(this);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle("MP3_PLAYER_FOREGROUND_SERVICE")
                .setContentText("HHP")
                .setCustomBigContentView(views)
                .setOnlyAlertOnce(true)
                .setContentIntent(reopenPIntent)
                .build();

        initUI();

        Intent toggleIntent = new Intent(this, MusicService.class);
        toggleIntent.putExtra(CLICK_EVENT, TOGGLE_EVENT);
        PendingIntent togglePIntent = PendingIntent.getService(this, 111, toggleIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.ivToggle, togglePIntent);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.putExtra(CLICK_EVENT, NEXT_EVENT);
        PendingIntent nextPIntent = PendingIntent.getService(this, 112, nextIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.ivNextSong, nextPIntent);

        Intent backIntent = new Intent(this, MusicService.class);
        backIntent.putExtra(CLICK_EVENT, BACK_EVENT);
        PendingIntent backPIntent = PendingIntent.getService(this, 113, backIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.ivBackSong, backPIntent);

        Intent exitIntent = new Intent(this, MusicService.class);
        exitIntent.putExtra(CLICK_EVENT, ACTION_EXIT);
        PendingIntent exitPIntent = PendingIntent.getService(this, 114, exitIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.ivExit, exitPIntent);


        startForeground(1001, notification);
        target = new NotificationTarget(this, R.id.ivThumbnail, views, notification, 1001);
        notificationManager.notify(1001, notification);
    }

    private void initUI() {
        if (currentSong != null) setSongNotificationUI();
        Thread thread = new Thread(this::updateProgressBar);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateProgressBar() {
        while (isServiceRunning){
            try {
                Message.obtain(handler).sendToTarget();
                    Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void setSongNotificationUI() {
        if(currentSong != null){
            views.setTextViewText(R.id.tvSongTitle, currentSong.getTitle());
            views.setTextViewText(R.id.tvSongAlbum, currentSong.getAlbum());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            views.setColorStateList(R.id.frSong, "setBackgroundTintList", ColorStateList.valueOf(App.getInstance().getStorage().mainColor));
        } else {
            views.setInt(R.id.frSong, "setBackgroundColor", App.getInstance().getStorage().mainColor);
        }

        Bitmap thumbnail = App.getInstance().getStorage().thumbnail;
        if (thumbnail == null) {
            views.setImageViewResource(R.id.ivThumbnail, R.drawable.ic_music);
        } else {
            Glide.with(App.getInstance()).asBitmap()
                    .override(50, 50).centerCrop()
                    .placeholder(R.drawable.ic_music)
                    .load(thumbnail).into(target);
        }
        notificationManager.notify(1001, notification);
    }

    @SuppressLint("MissingPermission")
    private void setToggleUI() {
        views.setInt(R.id.ivToggle, "setImageLevel", status);
        notificationManager.notify(1001, notification);
    }

    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    @Override
    public void onDestroy() {
        player.release();
        isServiceRunning = false;
        super.onDestroy();
    }
}
