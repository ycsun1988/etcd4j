package com.zhi.etcd4j.exception;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/7/13.
 */
public class ConnectException extends EtcdException {

    public static final ConnectException NO_AVAILABLE_ETCD_SERVER = new ConnectException(
            "No available etcd server error.");

    public ConnectException(String message) {
        super(message);
    }

    public ConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
