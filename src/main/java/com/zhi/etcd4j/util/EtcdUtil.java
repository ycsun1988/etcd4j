package com.zhi.etcd4j.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zhi.etcd4j.EtcdNode;

/**
 * @author zhimeng
 *         email: zhimeng@douyu.tv
 *         weichat: mengzhi825
 *         date: 2017/6/5.
 */
public class EtcdUtil {

    private EtcdUtil() {
    }

    /**
     *
     * @param nodes
     * @return
     */
    public static List<EtcdNode> flatEtcdNodes(List<EtcdNode> nodes) {
        if (CollectionUtil.isNotEmpty(nodes)) {
            List<EtcdNode> rtNodes = new ArrayList<EtcdNode>(64);
            List<EtcdNode> tmpNodes;
            for (EtcdNode node : nodes) {
                tmpNodes = node.getNodes();
                if (!node.isDir() || CollectionUtil.isEmpty(tmpNodes)) {
                    continue;
                }
                rtNodes.addAll(tmpNodes);
            }
            return rtNodes;
        }
        return Collections.emptyList();
    }
}
