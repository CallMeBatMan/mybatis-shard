package com.batman.shard.demo.dao;

import com.batman.mybatis.shard.TableShard;
import com.batman.shard.demo.VestTableShardStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@TableShard(tableNames = "tbl_user", shardKey = "userFlag", strategy = VestTableShardStrategy.class)
public interface UserMapper {
    List<User> getAllUser(@Param("userFlag") String flag);
}
