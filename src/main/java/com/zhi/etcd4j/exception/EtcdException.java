package com.zhi.etcd4j.exception;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 *         description: 对Etcd返回的错误码进行映射 & 同时将错误消息直接映射到异常中去，而不是EtcdResult
 *         因为直接返回错误码，上层还是会进行判断！倒不如统一使用异常，这样上层不用再次进行判断，简化上层逻辑。
 */
public class EtcdException extends RuntimeException {

    public EtcdException(String message) {
        super(message);
    }

    public EtcdException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtcdException(Throwable cause) {
        super(cause);
    }
}
