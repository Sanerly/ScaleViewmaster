package com.sanerly.scale.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.sanerly.scale.R;


/**
 * @Author: Sanerly
 * @CreateDate: 2019/7/22 9:15
 * @Description: 类描述
 */
public class ScaleView extends View {
    private final String TAG = "ScaleView";
    private Drawable mAddDrawable;
    private Drawable mSubDrawable;
    private Drawable mBgDrawable;
    private Drawable mBallDrawable;
    private int mMax;
    private int mImageWidth;
    private int mWidth;
    private int mHeight;
    private int mExtent;

    private Region mAddRegion;
    private Region mSubRegion;
    //当前进度
    private int mCurrent = 0;
    //每块的进度
    private int mProgress = 0;
    //每次点击的标识
    private int mPosition = 2;
    // 按下的声音
    private int soundPass;
    // 刻度走到中心位置的声音
    private int soundCenter;

    public final static int NONE = 0;
    public final static int ADD = 1;
    public final static int SUB = 2;
    private onPassChangeListener onPassChangeListener;

    private int mOrientation;
    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;

    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mAddDrawable = typedArray.getDrawable(R.styleable.ScaleView_image_add);
        mSubDrawable = typedArray.getDrawable(R.styleable.ScaleView_image_subtract);
        mSubDrawable = typedArray.getDrawable(R.styleable.ScaleView_image_subtract);
        mBgDrawable = typedArray.getDrawable(R.styleable.ScaleView_bg);
        mBallDrawable = typedArray.getDrawable(R.styleable.ScaleView_ball);
        mMax = typedArray.getInt(R.styleable.ScaleView_max, 4);
        mPosition = typedArray.getInt(R.styleable.ScaleView_ball_position, mMax / 2);
        mImageWidth = typedArray.getDimensionPixelSize(R.styleable.ScaleView_image_width, dp2px(20));
        mOrientation = typedArray.getInt(R.styleable.ScaleView_orientation, 0);
        typedArray.recycle();

        soundPass = R.raw.button;
        soundCenter = R.raw.mid;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (mOrientation == HORIZONTAL) {
            mExtent = w;
            mSubDrawable.setBounds(0, (h - mImageWidth) / 2, mImageWidth, mImageWidth + (h - mImageWidth) / 2);
            mAddDrawable.setBounds(w - mImageWidth, (h - mImageWidth) / 2, w, mImageWidth + (h - mImageWidth) / 2);
            mBgDrawable.setBounds(mImageWidth, (h - mBgDrawable.getIntrinsicHeight()) / 2,
                    w - mImageWidth, mBgDrawable.getIntrinsicHeight() + (h - mBgDrawable.getIntrinsicHeight()) / 2);
            mAddRegion = new Region();
            mAddRegion.set(w - mImageWidth, 0, w, h);
            mSubRegion = new Region();
            mSubRegion.set(0, 0, mImageWidth, h);
            mProgress = getProgress();
            mCurrent = mImageWidth + mProgress * mPosition;
        } else {
            mExtent = h;
            mAddDrawable.setBounds((w - mImageWidth) / 2, 0, (w - mImageWidth) / 2 + mImageWidth, mImageWidth);

            mSubDrawable.setBounds((w - mImageWidth) / 2, h - mImageWidth, (w - mImageWidth) / 2 + mImageWidth, h);
            mBgDrawable.setBounds((w - mBgDrawable.getIntrinsicWidth()) / 2, mImageWidth,
                    (w - mBgDrawable.getIntrinsicWidth()) / 2 + mBgDrawable.getIntrinsicWidth(), h - mImageWidth);
            mAddRegion = new Region();
            mAddRegion.set(0, 0, w, mImageWidth);

            mSubRegion = new Region();
            mSubRegion.set(0, h - mImageWidth, w, h);

            mProgress = getProgress();
            mCurrent = h - mImageWidth - mProgress * mPosition;
        }


        if (onPassChangeListener != null) {
            onPassChangeListener.onPassChange(mPosition, mProgress, mCurrent, NONE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);
        mSubDrawable.draw(canvas);
        mAddDrawable.draw(canvas);
        mBgDrawable.draw(canvas);
        if (mOrientation == HORIZONTAL) {
            //mCurrent - mBallDrawable.getIntrinsicHeight() / 2 将Ball的中心点和刻度对齐
            //(mHeight - mBallDrawable.getIntrinsicHeight()) / 2  计算Ball的top值
            // mBallDrawable.getIntrinsicHeight() + ballTop 计算Ball的bottom值
            //  mBallDrawable.getIntrinsicWidth() + ballLeft  计算移动的右距离，中心点和刻度对齐
            int ballTop = (mHeight - mBallDrawable.getIntrinsicHeight()) / 2;
            int ballLeft = mCurrent - mBallDrawable.getIntrinsicWidth() / 2;
            mBallDrawable.setBounds(ballLeft, ballTop, mBallDrawable.getIntrinsicWidth() + ballLeft, mBallDrawable.getIntrinsicHeight() + ballTop);
        } else {
            int ballLeft = (mWidth - mBallDrawable.getIntrinsicWidth()) / 2;
            int ballTop = mCurrent - mBallDrawable.getIntrinsicHeight() / 2;
            mBallDrawable.setBounds(ballLeft, ballTop, mBallDrawable.getIntrinsicWidth() + ballLeft, mBallDrawable.getIntrinsicHeight() + ballTop);
        }
        mBallDrawable.draw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (mAddRegion.contains(x, y) && mPosition < mMax) {
            if (mOrientation == HORIZONTAL) {
                mCurrent += mProgress;
            } else {
                mCurrent -= mProgress;
            }
            mPosition++;
            playSound();
            invalidate();
            if (onPassChangeListener != null) {
                onPassChangeListener.onPassChange(mPosition, mProgress, mCurrent, ADD);
            }
        }
        if (mSubRegion.contains(x, y) && mPosition > 0) {
            if (mOrientation == HORIZONTAL) {
                mCurrent -= mProgress;
            } else {
                mCurrent += mProgress;
            }

            mPosition--;
            playSound();
            invalidate();
            if (onPassChangeListener != null) {
                onPassChangeListener.onPassChange(mPosition, mProgress, mCurrent, SUB);
            }
        }
        return false;
    }

    private int getProgress() {
        int pro = mExtent - mImageWidth * 2;
        pro /= mMax;
        return pro;
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(1, dp, getContext().getResources().getDisplayMetrics());
    }


    private void playSound() {
        if (0 != soundPass) {
            if (0 != soundCenter && mPosition == mMax / 2) {
                SoundPoolUtil.play(getContext(), soundCenter);
            } else {
                SoundPoolUtil.play(getContext(), soundPass);
            }
        }
    }


    public interface onPassChangeListener {
        void onPassChange(int position, int progress, int current, int type);
    }

    public void setOnPassChangeListener(onPassChangeListener onPassChangeListener) {
        this.onPassChangeListener = onPassChangeListener;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setSoundPass(int soundPass) {
        this.soundPass = soundPass;
    }

    public void setSoundCenter(int soundCenter) {
        this.soundCenter = soundCenter;
    }
}
