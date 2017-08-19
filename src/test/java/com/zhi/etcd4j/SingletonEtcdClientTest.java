package com.zhi.etcd4j;


import com.zhi.etcd4j.core.DefaultEtcdClient;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/7/13.
 */
public class SingletonEtcdClientTest extends AbstractEtcdClientTest {

    @Override
    protected EtcdClient getEtcdClient() {
        return new DefaultEtcdClient("10.18.3.27:3379");
    }
}
