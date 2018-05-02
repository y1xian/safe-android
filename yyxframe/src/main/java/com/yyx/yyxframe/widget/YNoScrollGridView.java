package com.yyx.yyxframe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * GridView嵌套在SrcollView中会出现显示不全的情况
 * <p>
 * 这个类通过设置不滚动来避免
 */

public class YNoScrollGridView extends GridView {
	public YNoScrollGridView(Context context) {
		super(context);
	}

	public YNoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public YNoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 重写该方法，设置不滚动
	 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}
}
