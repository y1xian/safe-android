package com.yyx.yyxframe.widget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.IntRange;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.yyx.yyxframe.R;


/**
 * 根据用户点击状态设置不同的图片
 * <p>
 * //设置ImageView的背景
 * app:normalBackground=""
 * app:pressedBackground=""
 * app:unableBackground=""
 * //设置背景切换动画时长
 * app:animDuration="200"
 *
 * @author yyx
 */
public class YStateImageView extends AppCompatImageView {
    private Drawable mNormalDrawable;

    private Drawable mPressedDrawable;

    private Drawable mUnableDrawable;

    private int mDuration = 0;

    private int[][] states;

    private StateListDrawable mStateBackground;

    public YStateImageView(Context context) {
        this(context, null);
    }

    public YStateImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YStateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        states = new int[4][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[3] = new int[]{-android.R.attr.state_enabled};
        states[2] = new int[]{android.R.attr.state_enabled};

        Drawable drawable = getBackground();
        if (drawable != null && drawable instanceof StateListDrawable) {
            mStateBackground = (StateListDrawable) drawable;
        } else {
            mStateBackground = new StateListDrawable();
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.YStateImageView);

        mNormalDrawable = a.getDrawable(R.styleable.YStateImageView_normalBackground);
        mPressedDrawable = a.getDrawable(R.styleable.YStateImageView_pressedBackground);
        mUnableDrawable = a.getDrawable(R.styleable.YStateImageView_unableBackground);
        setStateBackground(mNormalDrawable, mPressedDrawable, mUnableDrawable);

        mDuration = a.getInteger(R.styleable.YStateImageView_animDuration, mDuration);
        setAnimationDuration(mDuration);
        a.recycle();
    }

    /**
     * 设置不同状态下的背景
     *
     * @param normal
     * @param pressed
     * @param unable
     */
    public void setStateBackground(Drawable normal, Drawable pressed, Drawable unable) {
        this.mNormalDrawable = normal;
        this.mPressedDrawable = pressed;
        this.mUnableDrawable = unable;

        //set background
        if (mPressedDrawable != null) {
            mStateBackground.addState(states[0], mPressedDrawable);
            mStateBackground.addState(states[1], mPressedDrawable);
        }

        if (mUnableDrawable != null) {
            mStateBackground.addState(states[3], mUnableDrawable);
        }

        if (mNormalDrawable != null) {
            mStateBackground.addState(states[2], mNormalDrawable);
        }
        setBackgroundDrawable(mStateBackground);
    }

    /**
     * 设置动画时长
     *
     * @param duration
     */
    public void setAnimationDuration(@IntRange(from = 0) int duration) {
        this.mDuration = duration;
        mStateBackground.setEnterFadeDuration(mDuration);
        mStateBackground.setExitFadeDuration(mDuration);
    }
}
