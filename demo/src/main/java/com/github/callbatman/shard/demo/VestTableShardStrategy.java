package com.github.callbatman.shard.demo;

import com.github.callmebatman.mybatis.shard.ITableShardStrategy;
import com.github.callmebatman.mybatis.shard.TableCacheUtil;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author Man
 * @description 用户标识分表策略
 * @date 2020/9/9 15:42
 **/
public class VestTableShardStrategy implements ITableShardStrategy {

    @Override
    public String tableShard(String originSql, Map<String, Object> parameterMap, String shardKey) {
        if (!parameterMap.containsKey(shardKey)) {
            throw new RuntimeException("未指明形参[String:" + shardKey + "] parameters:" + parameterMap);
        }
        String shardKeyVal = (String) parameterMap.get(shardKey);
        if (StringUtils.isEmpty(shardKeyVal)) {
            throw new RuntimeException("参数[String:" + shardKey + "]，不可为空 parameters:" + parameterMap);
        }
        for (String tbl : TableCacheUtil.getTable()) {
            originSql = replaceTblName(originSql, shardKeyVal, tbl);
        }
        return originSql;
    }

    /**
     * 将sql中的基表，替换为分表
     *
     * @param originSql
     * @param shardKeyVal
     * @param tbl
     * @return
     */
    private String replaceTblName(String originSql, String shardKeyVal, String tbl) {
        if (originSql.contains(tbl + "\n")) {
            return originSql.replace(tbl + "\n", "`" + TableUtil.generateTableName(shardKeyVal, tbl) + "`\n");
        }
        if (originSql.contains("`" + tbl + "`\n")) {
            return originSql.replace("`" + tbl + "`\n", "`" + TableUtil.generateTableName(shardKeyVal, tbl) + "`\n");
        }
        if (originSql.contains("`" + tbl + "`")) {
            return originSql.replace("`" + tbl + "`", "`" + TableUtil.generateTableName(shardKeyVal, tbl) + "`");
        }
        if (originSql.contains(tbl + " ")) {
            return originSql.replace(tbl + " ", "`" + TableUtil.generateTableName(shardKeyVal, tbl) + "`");
        }
        if (originSql.endsWith(tbl)) {
            return originSql.replace(tbl, "`" + TableUtil.generateTableName(shardKeyVal, tbl) + "`");
        }
        return originSql;
    }

}