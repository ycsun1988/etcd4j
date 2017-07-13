package com.zhi.etcd4j;


import java.util.Map;

import com.zhi.etcd4j.core.EtcdCallback;
import com.zhi.etcd4j.core.EtcdResult;
import com.zhi.etcd4j.exception.EtcdException;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/7/13.
 */
public interface EtcdClient {

    EtcdResult get(String key) throws EtcdException;

    EtcdResult delete(String key) throws EtcdException;

    EtcdResult set(String key, String value) throws EtcdException;

    EtcdResult set(String key, String value, int ttl) throws EtcdException;

    EtcdResult set(String key, String value, int ttl, boolean prevExist) throws EtcdException;

    EtcdResult ttl(String key, int ttl) throws EtcdException;

    EtcdResult createDir(String key) throws EtcdException;

    EtcdResult createDir(String key, int ttl) throws EtcdException;

    EtcdResult createDir(String key, int ttl, boolean prevExist) throws EtcdException;

    EtcdResult listDir(String key) throws EtcdException;

    EtcdResult listDir(String key, boolean recursive) throws EtcdException;

    EtcdResult deleteDir(String key, boolean recursive) throws EtcdException;

    EtcdResult cas(String key, String value, Map<String, String> conditions) throws EtcdException;

    EtcdResult cad(String key, Map<String, String> conditions) throws EtcdException;

    void watch(String key, EtcdCallback etcdCallback);

    void watch(String key, boolean recursive, EtcdCallback etcdCallback);

    void watch(String key, long index, EtcdCallback etcdCallback);

    void watch(String key, long index, boolean recursive, EtcdCallback etcdCallback);
}
