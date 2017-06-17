package com.zhi.etcd4j.exception;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/14.
 */
public class EtcdErrorCodes {

    private EtcdErrorCodes() {
    }

    public static final int ERROR_CODE_KEY_NOT_FOUND = 100;
    public static final int ERROR_CODE_COMPARE_FAILED = 101;
    public static final int ERROR_CODE_NOT_A_FILE = 102;
    public static final int ERROR_CODE_KEY_ALREADY_EXISTS = 105;
    public static final int ERROR_CODE_DIRECTORY_NOT_EMPTY = 108;
    public static final int ERROR_CODE_INDEX_NOT_NUMBER = 203;

}
