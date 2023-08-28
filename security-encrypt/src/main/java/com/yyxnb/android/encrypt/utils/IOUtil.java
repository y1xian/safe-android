package com.yyxnb.android.encrypt.utils;

import android.database.Cursor;

import androidx.annotation.RestrictTo;

import com.yyxnb.android.encrypt.utils.LogUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * IO操作相关的工具类
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class IOUtil {
	private static final String TAG = IOUtil.class.getSimpleName();

	/**
	 * 安全关闭Cursor对象
	 *
	 * @param cursor 接受null参数
	 */
	public static void close(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

	/**
	 * 安全关闭Reader对象
	 *
	 * @param reader 接受null参数
	 */
	public static void closeSecure(Reader reader) {
		closeSecure((Closeable) reader);
	}

	/**
	 * 安全关闭Writer对象
	 *
	 * @param writer 接受null参数
	 */
	public static void closeSecure(Writer writer) {
		closeSecure((Closeable) writer);
	}

	/**
	 * 安全关闭InputStream对象
	 *
	 * @param input 接受null参数
	 */
	public static void closeSecure(InputStream input) {
		closeSecure((Closeable) input);
	}

	/**
	 * 安全关闭OutputStream对象
	 *
	 * @param output 接受null参数
	 */
	public static void closeSecure(OutputStream output) {
		closeSecure((Closeable) output);
	}

	/**
	 * 安全关闭Closeable对象
	 *
	 * @param closeable 接受null参数, 和已经关闭的.
	 */
	public static void closeSecure(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				LogUtil.e(TAG, "closeSecure IOException");
			}
		}
	}

}
