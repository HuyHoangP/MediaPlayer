package com.hhp.mp3player.database.entity;

import android.graphics.RectF;

public class Indicator {
    private RectF rect;
    private final float left;
    private final float right;
    private float x;

    public Indicator(RectF rect, float left, float right) {
        this.rect = rect;
        this.left = left;
        this.right = right;
        x = left;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public void incrementX(float addX) {
        x += addX;
        rect.right = Math.min(x, right);
    }

    public void setMaxHeight(int height) {
        rect.top = 0;
        rect.bottom = height;
    }

    public void setNormalHeight(int height) {
        rect.top = height / 4.0f;
        rect.bottom = 3 * height / 4.0f;
    }

    public void setMaxX() {
        x = right;
        rect.right = right;
    }

    public void setMinX() {
        x = left;
        rect.right = left;
    }

    public float getX() {
        return x;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    @Override
    public String toString() {
        return "Indicator{" +
                "rect=" + rect +
                ", left=" + left +
                ", right=" + right +
                ", x=" + x +
                '}';
    }
}
