package com.zhi.etcd4j;

import com.zhi.etcd4j.exception.EtcdException;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public interface EtcdCallback {

    void onFailure(EtcdException e);

    void onResponse(EtcdResult result);
}
