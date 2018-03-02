package com.yyx.yyxframe.utils.http;


import com.yyx.yyxframe.utils.handler.YHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class YHttp implements IHttpEngine {

    private static IHttpEngine httpEngine;
    private static YHttp mHttp;
    public static YHandler handler = new YHandler();

    public static void init(IHttpEngine engine) {
        httpEngine = engine;
    }

    public static YHttp obtain() {
        if (httpEngine == null) {
            throw new NullPointerException("Call YFrame.initXHttp(IHttpEngine httpEngine) within your Application onCreate() method." +
                    "Or extends YApplication");
        }
        if (mHttp == null) {
            mHttp = new YHttp();
        }
        return mHttp;
    }

    /**
     * 获取实体类的类型
     *
     * @param obj
     * @return
     */
    public static Class<?> analysisClassInfo(Object obj) {
        Type genType = obj.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<?>) params[0];
    }

    @Override
    public void get(String url, Map<String, Object> params, HttpCallBack callBack) {
        httpEngine.get(url, params, callBack);
    }

    @Override
    public void post(String url, Map<String, Object> params, HttpCallBack callBack) {
        httpEngine.post(url, params, callBack);
    }
}
