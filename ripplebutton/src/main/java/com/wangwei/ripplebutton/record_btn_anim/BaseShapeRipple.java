package com.wangwei.ripplebutton.record_btn_anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.wangwei.ripplebutton.utils.DensityUtils;


public abstract class BaseShapeRipple {

    protected Paint shapePaint;

    protected int width;

    protected int height;

    protected float strokeWidth;

    public BaseShapeRipple(Context context) {
        init(context);
    }

    private void init(Context context) {
        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        shapePaint.setDither(true);
        shapePaint.setStrokeWidth(DensityUtils.dip2px(context, 0.3f));
        shapePaint.setStyle(Paint.Style.STROKE);
    }

    public abstract void draw(Canvas canvas, int x, int y, float currentRadiusSize, int currentColor, int rippleIndex, float fraction);

    public Paint getShapePaint() {
        return shapePaint;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;

        shapePaint.setStrokeWidth(strokeWidth);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
