package com.zhi.etcd4j.core;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.zhi.etcd4j.exception.EtcdException;
import com.zhi.etcd4j.exception.EtcdExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/6.
 *         职责是解析Etcd 返回json数据，并抛出相应的异常
 */
public class MessageParser {

    private static final Logger LOG = LoggerFactory.getLogger(MessageParser.class);
    private Gson gson;

    public MessageParser(Gson gson) {
        this.gson = gson;
    }

    /**
     * Parsing http response body string to EtcdResult instance.
     *
     * @param statusCode http response status code.
     * @param bodyStr    http response body string.
     * @return the EtcdResult instance.
     */
    public com.zhi.etcd4j.core.EtcdResult parseResponse(int statusCode, String bodyStr) {
        EtcdResult tempResult;
        try {
            tempResult = gson.fromJson(bodyStr, EtcdResult.class);
        } catch (JsonParseException e) {
            LOG.debug("Parse bodyStr [{}] error.", bodyStr, e);
            throw new EtcdException("Parse bodyStr [" + bodyStr + "] error.", e);
        }
        checkThrowError(tempResult);
        return buildEtcdResult(tempResult);
    }

    private void checkThrowError(EtcdResult etcdResult) {
        int errorCode = etcdResult.errorCode;
        //must throw error.
        if (errorCode > 0) {
            throw EtcdExceptionUtil.newSpecificException(errorCode, gson.toJson(etcdResult));
        }
    }

    private com.zhi.etcd4j.core.EtcdResult buildEtcdResult(EtcdResult etcdResult) {
        //program go here means Etcd's response has no error.
        com.zhi.etcd4j.core.EtcdResult rtResult = new com.zhi.etcd4j.core.EtcdResult();
        rtResult.setAction(etcdResult.action);
        rtResult.setIndex(etcdResult.index);
        rtResult.setNode(etcdResult.node);
        rtResult.setPrevNode(etcdResult.prevNode);
        return rtResult;
    }

    /**
     * 仅在MessageParser内部使用!
     */
    private static class EtcdResult {
        //General values
        private String action;
        private EtcdNode node;
        private EtcdNode prevNode;

        //For errors
        private int errorCode;
        private String message;
        private String cause;
        private int index;
    }

}
