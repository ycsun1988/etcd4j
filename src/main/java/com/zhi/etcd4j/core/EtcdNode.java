package com.zhi.etcd4j.core;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public class EtcdNode implements Serializable {

    private String key;
    private long createdIndex;
    private long modifiedIndex;
    private String value;
    private String expiration;
    private int ttl;
    private boolean dir;
    private List<EtcdNode> nodes;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCreatedIndex() {
        return createdIndex;
    }

    public void setCreatedIndex(long createdIndex) {
        this.createdIndex = createdIndex;
    }

    public long getModifiedIndex() {
        return modifiedIndex;
    }

    public void setModifiedIndex(long modifiedIndex) {
        this.modifiedIndex = modifiedIndex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public boolean isDir() {
        return dir;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public List<EtcdNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<EtcdNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "EtcdNode{" +
                "key='" + key + '\'' +
                ", createdIndex=" + createdIndex +
                ", modifiedIndex=" + modifiedIndex +
                ", value='" + value + '\'' +
                ", expiration='" + expiration + '\'' +
                ", ttl=" + ttl +
                ", dir=" + dir +
                ", nodes=" + nodes +
                '}';
    }
}
