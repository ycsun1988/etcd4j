package com.zhi.etcd4j.core;

import java.io.Serializable;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 *         EtcdResult对应Etcd服务器返回的成功的消息，而Etcd返回的错误消息会映射成Exception抛出
 */
public class EtcdResult implements Serializable {

    private String action;
    private EtcdNode node;
    private EtcdNode prevNode;
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
                ", index=" + index +
                '}';
    }
}
