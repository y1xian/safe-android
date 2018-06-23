package com.yyx.yyxframe.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 提供禁止滑动功能的自定义ViewPager
 */
public class YNoScrollViewPager extends ViewPager {
    private boolean isCanScroll = true;
    private boolean isHasScrollAnim=true;

    public YNoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public YNoScrollViewPager(Context context) {
        super(context);
    }

    /**
     * 设置其是否能滑动
     * @param isCanScroll false 禁止滑动， true 可以滑动
     */
    public void setNoScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    /**
     *  设置其是否有动画效果
     * @param isHasScrollAnim false 无动画 true 有动画
     */
    public void setHasScrollAnim(boolean isHasScrollAnim){
        this.isHasScrollAnim=isHasScrollAnim;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return !isCanScroll && super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return !isCanScroll && super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    /**
     * 设置其是否去求切换时的滚动动画
     *isHasScrollAnim为false时，会去除滚动效果
     */
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,isHasScrollAnim);
    }

}