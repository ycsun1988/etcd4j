package com.zhi.etcd4j.core;

import java.io.IOException;

import com.zhi.etcd4j.exception.EtcdException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/9.
 *         adapter Callback to EtcdCallback
 */
public class OkHttp3CallbackAdapter implements Callback {

    private static final Logger LOG = LoggerFactory.getLogger(OkHttp3CallbackAdapter.class);
    /**
     * DefaultEtcdClient callback which used for developers.
     */
    private EtcdCallback etcdCallback;
    /**
     * MessageParser used for parsing response string to EtcdResult.
     */
    private MessageParser messageParser;

    public OkHttp3CallbackAdapter(EtcdCallback etcdCallback, MessageParser messageParser) {
        this.etcdCallback = etcdCallback;
        this.messageParser = messageParser;
    }

    public void onFailure(Call call, IOException e) {
        LOG.error("Execute http request error, please check network connection.", e);
        etcdCallback.onFailure(new EtcdException("Execute http request error, please check network connection.", e));
    }

    public void onResponse(Call call, Response response) throws IOException {
        String bodyStr;
        try {
            bodyStr = response.body().string();
        } catch (IOException e) {
            LOG.error("Read http response body error.", e);
            etcdCallback.onFailure(new EtcdException("Read http response body error.", e));
            return;
        }
        int statusCode = response.code();
        LOG.debug("Http response code [{}], body [{}]", statusCode, bodyStr);
        EtcdResult etcdResult;
        try {
            etcdResult = messageParser.parseResponse(statusCode, bodyStr);
            etcdCallback.onResponse(etcdResult);
        } catch (Exception e) {
            LOG.error("Parse response body [{}] error.", bodyStr, e);
            etcdCallback.onFailure(new EtcdException("", e));
        }
    }
}
