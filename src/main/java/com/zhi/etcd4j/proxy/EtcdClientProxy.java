package com.zhi.etcd4j.proxy;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zhi.etcd4j.EtcdClient;
import com.zhi.etcd4j.core.DefaultEtcdClient;
import com.zhi.etcd4j.core.EtcdCallback;
import com.zhi.etcd4j.core.EtcdResult;
import com.zhi.etcd4j.exception.ConnectException;
import com.zhi.etcd4j.exception.EtcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/7/13.
 *         线程安全改造
 */
public class EtcdClientProxy implements EtcdClient {

    private static final Logger LOG = LoggerFactory.getLogger(EtcdClientProxy.class);
    private final List<EtcdClient> etcdClients = new ArrayList<>();
    private int availableIndex;
    public static final ThreadLocal<Integer> RETRY_TIMES = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     * @param ipPorts ["ip1:port1", "ip2:port2", "ip3:port3"]
     */
    public EtcdClientProxy(String... ipPorts) {
        if (ipPorts == null) {
            LOG.error("IpPorts can't be null.");
            throw new IllegalArgumentException();
        }
        for (String ipPort : ipPorts) {
            etcdClients.add(new DefaultEtcdClient(ipPort));
        }
    }

    //delegate to DefaultEtcdClient with try policy when meet with connect exception.
    @Override
    public synchronized EtcdResult get(String key) throws EtcdException {
        try {
            return availableClient().get(key);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return get(key);
        }
    }

    @Override
    public synchronized EtcdResult delete(String key) throws EtcdException {
        try {
            return availableClient().delete(key);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return delete(key);
        }
    }

    @Override
    public synchronized EtcdResult set(String key, String value) throws EtcdException {
        try {
            return availableClient().set(key, value);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return set(key, value);
        }
    }

    @Override
    public synchronized EtcdResult set(String key, String value, int ttl) throws EtcdException {
        try {
            return availableClient().set(key, value, ttl);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return set(key, value, ttl);
        }
    }

    @Override
    public synchronized EtcdResult set(String key, String value, int ttl, boolean prevExist) throws EtcdException {
        try {
            return availableClient().set(key, value, ttl, prevExist);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return set(key, value, ttl, prevExist);
        }
    }

    @Override
    public synchronized EtcdResult ttl(String key, int ttl) throws EtcdException {
        try {
            return availableClient().ttl(key, ttl);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return ttl(key, ttl);
        }
    }

    @Override
    public synchronized EtcdResult createDir(String key) throws EtcdException {
        try {
            return availableClient().createDir(key);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return createDir(key);
        }
    }

    @Override
    public synchronized EtcdResult createDir(String key, int ttl) throws EtcdException {
        try {
            return availableClient().createDir(key, ttl);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return createDir(key, ttl);
        }
    }

    @Override
    public synchronized EtcdResult createDir(String key, int ttl, boolean prevExist) throws EtcdException {
        try {
            return availableClient().createDir(key, ttl, prevExist);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return createDir(key, ttl, prevExist);
        }
    }

    @Override
    public synchronized EtcdResult listDir(String key) throws EtcdException {
        try {
            return availableClient().listDir(key);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return listDir(key);
        }
    }

    @Override
    public synchronized EtcdResult listDir(String key, boolean recursive) throws EtcdException {
        try {
            return availableClient().listDir(key, recursive);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return listDir(key, recursive);
        }
    }

    @Override
    public synchronized EtcdResult deleteDir(String key, boolean recursive) throws EtcdException {
        try {
            return availableClient().deleteDir(key, recursive);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return deleteDir(key, recursive);
        }
    }

    @Override
    public synchronized EtcdResult cas(String key, String value, Map<String, String> conditions) throws EtcdException {
        try {
            return availableClient().cas(key, value, conditions);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return cas(key, value, conditions);
        }
    }

    @Override
    public synchronized EtcdResult cad(String key, Map<String, String> conditions) throws EtcdException {
        try {
            return availableClient().cad(key, conditions);
        } catch (ConnectException e) {
            nextClientOrNoAvailableEtcdServerException();
            return cad(key, conditions);
        }
    }


    //底层依赖OkHttpClient 是否是单线程回调Callback，如果是多线程存在问题
    //异步机制暂不支持自动切换可用Etcd服务器。
    @Override
    public synchronized void watch(final String key, final EtcdCallback etcdCallback) {
        availableClient().watch(key, etcdCallback);
    }

    @Override
    public synchronized void watch(final String key, final boolean recursive, final EtcdCallback etcdCallback) {
        availableClient().watch(key, recursive, etcdCallback);
    }

    @Override
    public synchronized void watch(final String key, final long index, final EtcdCallback etcdCallback) {
        availableClient().watch(key, index, etcdCallback);
    }

    @Override
    public synchronized void watch(final String key, final long index, final boolean recursive,
            final EtcdCallback etcdCallback) {
        availableClient().watch(key, index, recursive, etcdCallback);
    }

    private EtcdClient availableClient() {
        return etcdClients.get(availableIndex);
    }

    private void nextClientOrNoAvailableEtcdServerException() {
        int etcdClientSize = etcdClients.size();
        availableIndex = (++availableIndex) % etcdClientSize;
        //update retry times.
        int retryTimes = RETRY_TIMES.get();
        RETRY_TIMES.set(++retryTimes);

        if (RETRY_TIMES.get() == etcdClientSize) {
            RETRY_TIMES.set(0);
            throw ConnectException.NO_AVAILABLE_ETCD_SERVER;
        }
    }
}
