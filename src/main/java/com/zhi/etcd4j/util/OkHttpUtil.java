package com.zhi.etcd4j.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.zhi.etcd4j.exception.EtcdException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public class OkHttpUtil {

    public static final int DEFAULT_CONNECT_TIMEOUT = 5;
    public static final int DEFAULT_WRITE_TIMEOUT = 5;
    public static final int DEFAULT_READ_TIMEOUT = 10;
    public static final String DEFAULT_CHARSET = "UTF-8";

    private OkHttpUtil() {
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

    /**
     * 构建Http Form Params
     *
     * @param nameValuePairs key-value pair. 如果为空则抛出异常。
     * @return FormBody实例
     */
    public static FormBody buildFormBody(Map<String, String> nameValuePairs) {
        if (nameValuePairs == null || nameValuePairs.isEmpty()) {
            throw new EtcdException("Illegal nameValuePairs, where nameValuePairs can't be null or empty");
        }
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> nameValuePair : nameValuePairs.entrySet()) {
            formBuilder.add(nameValuePair.getKey(), nameValuePair.getValue());
        }
        return formBuilder.build();
    }

    /**
     * build a complete http request url.
     *
     * @param baseUri  such as http://localhost:3379/v2/keys
     * @param endpoint access point, such as /someKey
     * @param params   http request parameters
     * @return access url, baseUri/endpoint?key1=val1&key2=val2
     */
    public static String buildRequestUrl(String baseUri, String endpoint, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(baseUri);
        sb.append(endpoint);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(urlEscape(entry.getKey()))
                        .append("=")
                        .append(urlEscape(entry.getValue()))
                        .append("&");
            }
        }
        String url = sb.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private static String urlEscape(String url) {
        try {
            return URLEncoder.encode(url, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
    }

}
