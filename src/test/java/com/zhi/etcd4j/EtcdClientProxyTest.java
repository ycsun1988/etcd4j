package com.zhi.etcd4j;


import com.zhi.etcd4j.proxy.EtcdClientProxy;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/7/13.
 */
public class EtcdClientProxyTest extends AbstractEtcdClientTest {
    private static int callTimes;

    @Override
    protected EtcdClient getEtcdClient() {
        //发现每次调用单元测试类中的一个test方法，都会执行该方法一次。
        System.out.println(++callTimes);
        EtcdClientProxy.RETRY_TIMES.set(0);
        return new EtcdClientProxy("192.168.64.133:3379", "192.168.64.132:3379", "192.168.64.134:3379");
    }

}
