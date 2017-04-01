package com.jackwang.ripplebuttonlib.record_btn_anim;

import android.content.Context;
import android.graphics.Canvas;

public class Circle extends BaseShapeRipple {

    public static final String TAG = Circle.class.getSimpleName();

    public Circle(Context context) {
    		super(context);
    }

    @Override
    public void draw(Canvas canvas, int x, int y, float currentRadiusSize, int currentColor, int rippleIndex, float fraction) {
        shapePaint.setColor(currentColor);
        shapePaint.setAlpha((int) (255 * (1 - fraction)));
        canvas.drawCircle(x, y, currentRadiusSize, shapePaint);
    }
}