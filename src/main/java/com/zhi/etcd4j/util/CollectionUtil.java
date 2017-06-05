package com.zhi.etcd4j.util;

import java.util.Collection;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    public static <E> boolean isEmpty(Collection<E> coll) {
        return coll == null || coll.isEmpty();
    }

    public static <E> boolean isNotEmpty(Collection<E> coll) {
        return !isEmpty(coll);
    }
}
