package com.zhi.etcd4j;

import java.io.Serializable;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public class EtcdResult implements Serializable {

    private String action;
    private EtcdNode node;
    private EtcdNode prevNode;
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

    @Override
    public String toString() {
        return "EtcdResult{" +
                "action='" + action + '\'' +
                ", node=" + node +
                ", prevNode=" + prevNode +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                ", cause='" + cause + '\'' +
                ", index=" + index +
                '}';
    }
}
