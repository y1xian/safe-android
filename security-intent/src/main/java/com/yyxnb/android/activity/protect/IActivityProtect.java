package com.yyxnb.android.activity.protect;

import android.os.Message;

/**
 * 回调接口
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public interface IActivityProtect {

	void finishLaunchActivity(Message message);

	void finishResumeActivity(Message message);

	void finishPauseActivity(Message message);

	void finishStopActivity(Message message);

}
