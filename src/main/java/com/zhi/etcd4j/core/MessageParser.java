package com.zhi.etcd4j.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
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

    public MessageParser() {
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
            tempResult = JSON.parseObject(bodyStr, EtcdResult.class);
        } catch (JSONException e) {
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
            throw EtcdExceptionUtil.newSpecificException(errorCode, JSON.toJSONString(etcdResult));
        }
    }

    private com.zhi.etcd4j.core.EtcdResult buildEtcdResult(EtcdResult etcdResult) {
        //program go here means Etcd's response has no error.
        com.zhi.etcd4j.core.EtcdResult rtResult = new com.zhi.etcd4j.core.EtcdResult();
        rtResult.setAction(etcdResult.action);
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

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public EtcdNode getNode() {
            return node;
        }

        public void setNode(EtcdNode node) {
            this.node = node;
        }

        public EtcdNode getPrevNode() {
            return prevNode;
        }

        public void setPrevNode(EtcdNode prevNode) {
            this.prevNode = prevNode;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

}
