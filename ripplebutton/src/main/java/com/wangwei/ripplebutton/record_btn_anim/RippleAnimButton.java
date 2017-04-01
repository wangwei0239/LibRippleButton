package com.wangwei.ripplebutton.record_btn_anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wangwei.ripplebutton.R;
import com.wangwei.ripplebutton.utils.DensityUtils;

import java.util.ArrayList;

/**
 * Created by wangwei on 17/3/31.
 */

public class RippleAnimButton extends View {

    private int iconNormalRes;
    private int iconPressedRes;
    private int iconHeight;
    private int iconWidth;
    private Bitmap iconNormal;
    private Bitmap iconPressed;
    private Bitmap icon;
    private int iconRadius;
    private int iconMargin;
    private Point iconCenter;
    //0:icon_around  1:icon_diagonal
    private int iconRadiusType = 0;
    private Paint iconCirclePaint;
    private Rect iconRect;
    private Rect paddingRect;


    private int ripplePathRange;
    private int rippleStartRadius;
    private int rippleEndRadius;
    private int rippleInterval = 60;
    private int rippleColor;
    private int angle = 0;


    private final int LEFT_MASK = 0b100000;
    private final int TOP_MASK = 0b010000;
    private final int RIGHT_MASK = 0b001000;
    private final int BOTTOM_MASK = 0b000100;
    private final int HORIZONTAL_CENTER_MASK = 0b000010;
    private final int VERTICAL_CENTER_MASK = 0b000001;
    private final int CENTER_MASK = 0b000011;
    private int mGravity = 0b000011;


    private boolean isClipPadding = false;
    private State mState = State.STAND_BY;
    private ArrayList<ShapeRippleEntry> shapeRippleEntries = new ArrayList<>();
    private ArrayList<ShapeRippleEntry> shapeRippleEntryPool = new ArrayList<>();
    private ArrayList<ShapeRippleEntry> finishedShapeRippleEntries = new ArrayList<>();


    private ValueAnimator rippleAnim;
    private int LOADING_ANIM_DURATION = 1400;
    private int rippleAnimDuration;
    private ValueAnimator loadingAnim;
    private float lastFractionValue = 0;
    private long lastFakeTime = 0;
    private long lastTime = 0;
    private static final long TIME_INTERVAL = 50;


    private float loadingStrokeWidth = DensityUtils.dip2px(getContext(), 3.5f);

    private RippleButtonListener rippleButtonListener = null;

    public RippleAnimButton(Context context) {
        this(context, null);
    }

    public RippleAnimButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleAnimButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RippleAnimButton);
            try {
                rippleColor = ta.getColor(R.styleable.RippleAnimButton_ripple_color, Color.RED);
                iconNormalRes = ta.getResourceId(R.styleable.RippleAnimButton_icon_src, 0);
                iconPressedRes = ta.getResourceId(R.styleable.RippleAnimButton_icon_src_pressed, 0);
                rippleAnimDuration = ta.getInt(R.styleable.RippleAnimButton_ripple_duration, 2000);
                rippleInterval = ta.getInt(R.styleable.RippleAnimButton_ripple_interval, 20);
                isClipPadding = ta.getBoolean(R.styleable.RippleAnimButton_clip_padding, false);
                iconMargin = ta.getDimensionPixelSize(R.styleable.RippleAnimButton_icon_margin, 0);
                iconRadiusType = ta.getInt(R.styleable.RippleAnimButton_icon_radius_type,0);
                loadingStrokeWidth = ta.getDimensionPixelSize(R.styleable.RippleAnimButton_loading_stroke_width, DensityUtils.dip2px(getContext(), 3.5f));
                iconHeight = ta.getDimensionPixelSize(R.styleable.RippleAnimButton_icon_height, (iconNormalRes == 0 && iconPressedRes == 0) ? 0 : -1);
                iconWidth = ta.getDimensionPixelSize(R.styleable.RippleAnimButton_icon_width, (iconNormalRes == 0 && iconPressedRes == 0) ? 0 : -1);
                if (iconPressedRes == 0) {
                    iconPressedRes = iconNormalRes;
                }else if(iconNormalRes == 0){
                    iconNormalRes = iconPressedRes;
                }
                mGravity = ta.getInt(R.styleable.RippleAnimButton_gravity, CENTER_MASK);

            } finally {
                ta.recycle();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    private void init() {
        initBitmap();
        initIconPosition();
        initIconCirclePaint();
        initRadius();
        initRippleAnim();
        initLoadingAnim();
        loadingAnim.start();
    }

    private Gravity getGravity() {
        Gravity gravity = new Gravity();
        if ((mGravity & CENTER_MASK) == CENTER_MASK) {
            gravity.center = true;
        }

        if ((mGravity & HORIZONTAL_CENTER_MASK) == HORIZONTAL_CENTER_MASK) {
            gravity.h_center = true;
        }

        if ((mGravity & VERTICAL_CENTER_MASK) == VERTICAL_CENTER_MASK) {
            gravity.v_center = true;
        }

        if ((mGravity & LEFT_MASK) == LEFT_MASK) {
            gravity.left = true;
        }

        if ((mGravity & TOP_MASK) == TOP_MASK) {
            gravity.top = true;
        }

        if ((mGravity & RIGHT_MASK) == RIGHT_MASK) {
            gravity.right = true;
        }

        if ((mGravity & BOTTOM_MASK) == BOTTOM_MASK) {
            gravity.bottom = true;
        }
        return gravity;
    }

    private Point getIconCenter(Gravity gravity) {
        int viewHH = getMeasuredHeight() / 2;
        int viewWH = getMeasuredWidth() / 2;
        int iconHH = iconHeight / 2;
        int iconWH = iconWidth / 2;

        Point iconCenterPoint = new Point(viewWH, viewHH);

        if (gravity.center) {
            return iconCenterPoint;
        }

        if (!gravity.h_center && gravity.left) {
            iconCenterPoint.x = iconWH + getPaddingLeft();
        }

        if (!gravity.v_center && gravity.top) {
            iconCenterPoint.y = iconHH + getPaddingTop();
        }

        if (!gravity.h_center && !gravity.left && gravity.right) {
            iconCenterPoint.x = viewWH * 2 - iconWH - getPaddingRight();
        }

        if (!gravity.v_center && !gravity.top && gravity.bottom) {
            iconCenterPoint.y = viewHH * 2 - iconHH - getPaddingBottom();
        }

        if (gravity.h_center) {
            iconCenterPoint.x = viewWH;
        }

        if (gravity.v_center) {
            iconCenterPoint.y = viewHH;
        }
        return iconCenterPoint;
    }

    private Rect getIconRect(Point iconCenter) {
        int iconHH = iconHeight / 2;
        int iconWH = iconWidth / 2;
        int iconL = iconCenter.x - iconWH;
        int iconT = iconCenter.y - iconHH;
        int iconR = iconCenter.x + iconWH;
        int iconB = iconCenter.y + iconHH;
        Rect rect = new Rect(iconL, iconT, iconR, iconB);
        return rect;
    }

    private void initBitmap() {
        if (iconNormalRes != 0) {
            iconNormal = decodeBitmap(getContext(), iconNormalRes, iconWidth, iconHeight);
            iconPressed = decodeBitmap(getContext(), iconPressedRes, iconWidth, iconHeight);
            icon = iconNormal;
            iconHeight = icon.getHeight();
            iconWidth = icon.getWidth();
        }
    }

    private void initRadius() {
        if(iconRadiusType == 0){
            iconRadius = Math.max(iconHeight, iconWidth);
        }else {
            iconRadius = (int) Math.sqrt(Math.pow(iconHeight, 2) + Math.pow(iconWidth, 2)) / 2;
        }
        rippleStartRadius = iconRadius + iconMargin;
        rippleEndRadius = getLongIconCenterToEdgeDistance();
        ripplePathRange = rippleEndRadius - rippleStartRadius;
    }

    private int getLongIconCenterToEdgeDistance() {
        return Math.max(iconCenter.x, getMeasuredWidth() - iconCenter.x);
    }

    private void initIconPosition() {
        Gravity gravity = getGravity();
        iconCenter = getIconCenter(gravity);
        iconRect = getIconRect(iconCenter);
        paddingRect = new Rect(getPaddingLeft(), getPaddingTop(),getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
    }

    private void initIconCirclePaint() {
        if (iconCirclePaint == null) {
            iconCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            iconCirclePaint.setColor(Color.WHITE);
            iconCirclePaint.setStyle(Paint.Style.STROKE);
            iconCirclePaint.setStrokeWidth(loadingStrokeWidth);
            iconCirclePaint.setShader(new SweepGradient(iconCenter.x, iconCenter.y, Color.TRANSPARENT, Color.parseColor("#FFBFEE")));
        }
    }


    private static Bitmap decodeBitmap(Context context, int res, int displayWidth, int displayHeight) {

        if (res == 0) {
            return null;
        }

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), res, op);
        if (displayHeight < 0) {
            displayHeight = op.outHeight;
        }

        if (displayWidth < 0) {
            displayWidth = op.outWidth;
        }

        float wRatio = op.outWidth / (float) displayWidth;
        float hRatio = op.outHeight / (float) displayHeight;
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                op.inSampleSize = (int) wRatio;
            } else {
                op.inSampleSize = (int) hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeResource(context.getResources(), res, op);

        displayWidth = displayWidth <= 0 ? 96 : displayWidth;
        displayHeight = displayHeight <= 0 ? 96 : displayHeight;


        return Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);
    }

    private void initRippleAnim() {
        rippleAnim = ValueAnimator.ofFloat(0f, 1f);
        rippleAnim.setDuration(rippleAnimDuration);
        rippleAnim.setRepeatCount(ValueAnimator.INFINITE);
        rippleAnim.setRepeatMode(ValueAnimator.RESTART);
        rippleAnim.setInterpolator(new LinearInterpolator());
        rippleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                render(valueAnimator.getAnimatedFraction());
            }
        });
        rippleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                shapeRippleEntries.clear();
                lastFractionValue = 0;
                invalidate();
            }
        });
    }

    private void initLoadingAnim() {
        loadingAnim = ValueAnimator.ofInt(0, 360);
        loadingAnim.setDuration(LOADING_ANIM_DURATION);
        loadingAnim.setRepeatMode(ValueAnimator.RESTART);
        loadingAnim.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                angle = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        loadingAnim.setInterpolator(new LinearInterpolator());
    }

    private void render(float fraction) {

        finishedShapeRippleEntries.clear();

        float delta = calcuDelta(fraction);

        for (ShapeRippleEntry entry : shapeRippleEntries) {
            entry.setFractionValue(entry.getFractionValue() + delta);
            if (entry.getFractionValue() >= 1f) {
                entry.reset();
                finishedShapeRippleEntries.add(entry);
            } else {
                entry.setRadiusSize(ripplePathRange * entry.getFractionValue() + rippleStartRadius);
            }
        }

        for (ShapeRippleEntry entry : finishedShapeRippleEntries) {
            if (shapeRippleEntries.contains(entry)) {
                if (shapeRippleEntries.remove(entry)) {
                    shapeRippleEntryPool.add(entry);
                }
            }
        }

        lastFractionValue = fraction;
        if (shapeRippleEntries.size() == 0) {
            rippleAnim.cancel();
        }
        invalidate();
    }

    private float calcuDelta(float fraction) {
        if (fraction >= lastFractionValue) {
            return fraction - lastFractionValue;
        } else {
            return fraction + 1 - lastFractionValue;
        }
    }

    private void addRipple() {
        if (mState == State.RIPPLE) {
            if (shapeRippleEntryPool.size() <= 0) {
                shapeRippleEntryPool.add(new ShapeRippleEntry(new Circle(getContext())));
            }
            ShapeRippleEntry entry = shapeRippleEntryPool.remove(0);
            entry.reset();
            shapeRippleEntries.add(entry);
            if (!rippleAnim.isRunning()) {
                rippleAnim.ofFloat(0f, 1f);
                rippleAnim.start();
            }
        }
    }

    private void addRipples(int num) {
        for (int i = 0; i < num; i++) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    addRipple();
                }
            }, i * (rippleInterval + 10 * i));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        loadingAnim.cancel();
        rippleAnim.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getX() > iconRect.left && event.getX() < iconRect.right && event.getY() > iconRect.top && event.getY() < iconRect.bottom) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                icon = iconPressed;

                if (mState == State.STAND_BY) {
                    setState(State.LOADING);
                } else {
                    setState(State.STAND_BY);
                }

                invalidate();

            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                icon = iconNormal;
                invalidate();
            }

            return super.onTouchEvent(event);
        } else {
            icon = iconNormal;
            invalidate();
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(isClipPadding){
            canvas.clipRect(paddingRect);
        }

        if (icon != null) {
            canvas.drawBitmap(icon, iconRect.left, iconRect.top,
                    null);
        }

        for (ShapeRippleEntry entry : shapeRippleEntries) {
            entry.getBaseShapeRipple().draw(canvas, iconCenter.x, iconCenter.y, entry.getRadiusSize(), rippleColor, 0,
                    entry.getFractionValue());
        }
        if (mState == State.LOADING) {
            canvas.save();
            canvas.rotate(angle, iconCenter.x, iconCenter.y);
            canvas.drawCircle(iconCenter.x, iconCenter.y, iconWidth / 2 - loadingStrokeWidth / 2, iconCirclePaint);
            canvas.restore();
        }

    }

    public void setState(State state) {
        if (rippleButtonListener != null) {
            rippleButtonListener.onStateChanged(mState, state);
        }

        mState = state;
        switch (state) {
            case STAND_BY:
                if (loadingAnim.isRunning()) {
                    loadingAnim.end();
                }
                break;
            case LOADING:
                loadingAnim.start();
                break;
            case RIPPLE:
                if (loadingAnim.isRunning()) {
                    loadingAnim.end();
                }
                break;
        }
        invalidate();
    }

    public void resetButtonState() {
        setState(State.STAND_BY);
    }


    public void onRmsChanged(float rmsdB) {
        if (mState == State.RIPPLE) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastFakeTime) > TIME_INTERVAL * 10) {
                addRipple();
                lastFakeTime = curTime;
            }
            if ((curTime - lastTime) > TIME_INTERVAL) {
                lastTime = curTime;
                float rippleNum = rmsdB / (float) 800;
                addRipples((int) rippleNum);
            }
        }
    }


    public enum State {
        STAND_BY, LOADING, RIPPLE
    }

    public interface RippleButtonListener {
        void onStateChanged(State oldState, State curState);
    }


}
