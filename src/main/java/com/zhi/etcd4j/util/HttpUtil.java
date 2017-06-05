package com.zhi.etcd4j.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public class HttpUtil {

    public static final int DEFAULT_CONNECT_TIMEOUT = 5;
    public static final int DEFAULT_WRITE_TIMEOUT = 5;
    public static final int DEFAULT_READ_TIMEOUT = 10;

    private HttpUtil() {
    }

    /**
     * 构建OkHttpClient
     *
     * @param timeUnit
     * @param connectTo
     * @param readTo
     * @param writeTo
     * @return OkHttpClient实例
     */
    public static OkHttpClient buildHttpClient(TimeUnit timeUnit, int connectTo, int readTo, int writeTo) {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTo, timeUnit)
                .readTimeout(readTo, timeUnit)
                .writeTimeout(writeTo, timeUnit)
                .build();
    }

    /**
     * 使用默认的参数构建OkHttpClient
     *
     * @return OkHttpClient实例
     */
    public static OkHttpClient buildHttpClient() {
        return buildHttpClient(TimeUnit.SECONDS, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }


}
