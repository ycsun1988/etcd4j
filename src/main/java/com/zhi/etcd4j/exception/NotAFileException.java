package com.zhi.etcd4j.exception;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/6.
 *         Etcd Key not found error.
 */
public class NotAFileException extends EtcdException {

    public NotAFileException(String message) {
        super(message);
    }
}
