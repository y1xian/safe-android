package com.yyx.yyxframe.widget;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yyx.yyxframe.R;
import com.yyx.yyxframe.utils.YEmptyUtils;
import com.yyx.yyxframe.utils.YOutdatedUtils;


/**
 * 加载等待提示框
 *
 * @author yyx
 */
public class YLoadingDialog extends Dialog {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private static YLoadingDialog dialog;
    private Context context;
    private TextView loadingMessage;
    private ProgressBar progressBar;
    private LinearLayout loadingView;
    private YColorDrawable drawable;

    public YLoadingDialog(Context context) {
        super(context, R.style.loading_dialog);
        this.context = context;
        drawable = new YColorDrawable();
        setContentView(R.layout.yyx_loading_dialog);
        loadingMessage = findViewById(R.id.yyx_frame_loading_message);
        progressBar = findViewById(R.id.yyx_frame_loading_progressbar);
        loadingView = findViewById(R.id.yyx_frame_loading_view);
        loadingMessage.setPadding(15, 0, 0, 0);
        drawable.setColor(Color.WHITE);
        YOutdatedUtils.setBackground(loadingView, drawable);
    }

    public static YLoadingDialog with(Context context) {
        if (dialog == null) {
            dialog = new YLoadingDialog(context);
        }
        return dialog;
    }

    public YLoadingDialog setOrientation(int orientation) {
        loadingView.setOrientation(orientation);
        if (orientation == HORIZONTAL) {
            loadingMessage.setPadding(15, 0, 0, 0);
        } else {
            loadingMessage.setPadding(0, 15, 0, 0);
        }
        return dialog;
    }

    public YLoadingDialog setBackgroundColor(@ColorInt int color) {
        drawable.setColor(color);
        YOutdatedUtils.setBackground(loadingView, drawable);
        return dialog;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dialog != null) {
            dialog = null;
        }
    }

    public YLoadingDialog setCanceled(boolean cancel) {
        setCanceledOnTouchOutside(cancel);
        setCancelable(cancel);
        return dialog;
    }

    public YLoadingDialog setMessage(String message) {
        if (!YEmptyUtils.isSpace(message)) {
            loadingMessage.setText(message);
        }
        return this;
    }

    public YLoadingDialog setMessageColor(@ColorInt int color) {
        loadingMessage.setTextColor(color);
        return this;
    }
}
