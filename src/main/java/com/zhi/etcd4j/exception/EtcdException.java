package com.zhi.etcd4j.exception;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public class EtcdException extends RuntimeException {

    public EtcdException(String message) {
        super(message);
    }

    public EtcdException(String message, int statusCode) {
        super(message + ", http status code: " + statusCode);
    }

    public EtcdException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtcdException(Throwable cause) {
        super(cause);
    }

}
