package com.yyxnb.android.intent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 安全的广播接收器
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public abstract class SafeBroadcastReceiver extends BroadcastReceiver {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onReceive(Context context, Intent intent) {
		if (IntentUtils.hasIntentBomb(intent)) {
			return;
		}
		onReceiveMsg(context, new SafeIntent(intent));
	}

	/**
	 * [处理消息，无需对intent和action判空，无需强转SafeIntent]<BR>
	 *
	 * @param context 上下文
	 * @param intent  安全的intent，可以直接获取内容
	 */
	public abstract void onReceiveMsg(Context context, Intent intent);
}
