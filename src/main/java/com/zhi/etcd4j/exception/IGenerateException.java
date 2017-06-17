package com.zhi.etcd4j.exception;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/14.
 */
public interface IGenerateException {

    EtcdException generate(String message);
}
