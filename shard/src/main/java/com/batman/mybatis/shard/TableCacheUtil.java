package com.batman.mybatis.shard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Man
 * @description 基表池：记录所有分表的基表名
 * @date 2020/9/9 15:44
 * @return
 **/
public class TableCacheUtil {

    private static final Set<String> BaseTalNameSet = Collections.synchronizedSet(new HashSet<>());
    // 记录是否初始化
    private static boolean initFlag = false;

    /**
     * 获取初始化状态
     *
     * @return
     */
    public static boolean inited() {
        return initFlag;
    }

    /**
     * 将所有基表加入缓存
     *
     * @param tblNames
     */
    public static void init(Set<String> tblNames) {
        BaseTalNameSet.addAll(tblNames);
        initFlag = true;
    }

    /**
     * 获取全部基表
     */
    public static Set<String> getTable() {
        return BaseTalNameSet;
    }

}