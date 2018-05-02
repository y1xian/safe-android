package com.yyx.yyxframe.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.yyx.yyxframe.common.YActivityStack;
import com.yyx.yyxframe.utils.permission.YPermission;


public abstract class YActivity extends AppCompatActivity implements ICallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YActivityStack.getInstance().addActivity(this);
        if (layoutResID()>0) {
            setContentView(layoutResID());
        }
        initView();
        initData();
    }

    /**
     * Android M 全局权限申请回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        YPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YActivityStack.getInstance().finishActivity();
    }
}
