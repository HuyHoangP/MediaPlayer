package com.hhp.mp3player.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.hhp.mp3player.R;
import com.hhp.mp3player.database.entity.Indicator;
import com.hhp.mp3player.view.fragment.photo.OnPlayMemoryFrgCallback;

import java.util.ArrayList;
import java.util.List;

public class MemoryIndicator extends View {
    public static final int TIMER = 5000;
    private static final int THREAD_SLEEP = 20;
    private static final String TAG = MemoryIndicator.class.getName();
    private static final float DISTANCE = 20.0f;
    private final MutableLiveData<List<Indicator>> listIndicatorLD = new MutableLiveData<>();
    private final Object lock = new Object();
    private int size = 1;
    private Thread thread;
    private Paint paintWhite, paintGray;
    private int width, height, position;
    private float x;
    private float indicator, addX;
    private boolean initDraw;
    private List<Indicator> listIndicator;
    private OnPlayMemoryFrgCallback callback;
    private boolean autoMode;
    private float right;

    public MemoryIndicator(Context context) {
        this(context, null);
    }

    public MemoryIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MemoryIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    public MutableLiveData<List<Indicator>> getListIndicatorLD() {
        return listIndicatorLD;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setAutoMode(boolean autoMode) {
        if (this.autoMode && autoMode) return;
        this.autoMode = autoMode;
        callback.setTextAutoMode(autoMode);
        if (autoMode) {
            listIndicator.get(position).setNormalHeight(height);
            right = listIndicator.get(position).getRight();
            synchronized (lock) {
                lock.notify();
            }
        } else {
            listIndicator.get(position).setMinX();
            listIndicator.get(position).setNormalHeight(height);
            if (position == 0) {
                setAutoMode(true);
                return;
            }
            position = Math.max(0, position - 1);
            listIndicator.get(position).setMaxHeight(height);
            postInvalidate();
            callback.setPhoto(position);
        }
    }

    private void initViews(AttributeSet attrs) {
        autoMode = true;
        listIndicator = new ArrayList<>();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LinearIndicator);
        size = typedArray.getInt(R.styleable.LinearIndicator_size, 1);
        typedArray.recycle();

        paintWhite = new Paint();
        paintWhite.setAntiAlias(true);
        paintWhite.setColor(Color.WHITE);
        paintWhite.setStyle(Paint.Style.FILL_AND_STROKE);

        paintGray = new Paint();
        paintGray.setAntiAlias(true);
        paintGray.setColor(Color.GRAY);
        paintGray.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void startProgressIndicator() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                while (position < size) {
                    if (autoMode) {
                        callback.setPhoto(position);
                    }
                    x = listIndicator.get(position).getLeft();
                    right = listIndicator.get(position).getRight();
                    while (width == 0 || x <= right) {
                        listIndicator.get(position).incrementX(addX);
                        sleepIndicator();
                        postInvalidate();
                        x = listIndicator.get(position).getX();
                    }
                    position++;
                }
                callback.backPressed();
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void sleepIndicator() {
        try {
            if (!autoMode) {
                synchronized (lock) {
                    lock.wait();
                }
            }
            Thread.sleep(THREAD_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width == 0) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        indicator = (width - (size - 1) * DISTANCE) / size;
        addX = indicator * THREAD_SLEEP / TIMER;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < size; i++) {
            float left = (indicator + DISTANCE) * i;
            float right = left + indicator;
            float top = height / 4.0f;
            float bottom = 3 * height / 4.0f;
            if (!initDraw) {
                RectF rect = new RectF();
                rect.top = top;
                rect.left = left;
                rect.bottom = bottom;
                rect.right = left;
                listIndicator.add(new Indicator(rect, left, right));
            }
            canvas.drawRect(left, top, right, bottom, paintGray);
        }
        if (!initDraw) {
            listIndicatorLD.postValue(listIndicator);
        }
        initDraw = true;
        for (int i = 0; i < size; i++) {
            canvas.drawRect(listIndicator.get(i).getRect(), paintWhite);
        }


    }

    public void setCallback(OnPlayMemoryFrgCallback callback) {
        this.callback = callback;
    }

    public void nextIndicator() {
        listIndicator.get(position).setMaxX();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: DETACH");
        thread.interrupt();
    }
}
