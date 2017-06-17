package com.zhi.etcd4j.util;

import com.zhi.etcd4j.exception.EtcdException;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/6.
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static void assertEmpty(String str, String message) {
        if (isEmpty(str)) {
            throw new EtcdException(message);
        }
    }
}
