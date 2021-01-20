package com.github.callbatman.shard.demo.dao;

import com.github.callbatman.shard.demo.VestTableShardStrategy;
import com.github.callmebatman.mybatis.shard.TableShard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@TableShard(tableNames = "tbl_user", shardKey = "userFlag", strategy = VestTableShardStrategy.class)
public interface UserMapper {
    List<User> getAllUser(@Param("userFlag") String flag);
}