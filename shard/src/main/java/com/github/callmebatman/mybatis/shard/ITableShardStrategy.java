package com.github.callmebatman.mybatis.shard;

import java.util.Map;

/**
 * @author Man
 * @description 分表策略
 * @date 2020/9/9 15:42
 **/
public interface ITableShardStrategy {

    /**
     * 处理原始sql，将基表按策略替换
     *
     * @param originSql    原始sql
     * @param parameterMap 查询方法的参数
     * @param shardKey     分表依据
     * @return 处理后的sql
     * @throws Exception
     */
    String tableShard(String originSql, Map<String, Object> parameterMap, String shardKey) throws Exception;

}