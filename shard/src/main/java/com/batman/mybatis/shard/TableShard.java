package com.batman.mybatis.shard;

import java.lang.annotation.*;

/**
 * @author Man
 * @description 分表注解:查询方法的形参参必须使用注解[@Param]（org.apache.ibatis.annotations.Param）
 * @date 2020/9/9 9:59
 **/
@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableShard {

    /**
     * @return java.lang.String
     * @author Man
     * @description 基表
     * @date 2020/9/9 9:58
     **/
    String[] tableNames();

    /**
     * @return java.lang.String
     * @author Man
     * @description 设置方法中的形参，作为分表依据
     * @date 2020/9/9 11:09
     **/
    String shardKey();

    /**
     * @return java.lang.Class<? extends com.ddmh.videocenterbiz.core.base.ITableShardStrategy>
     * @author Man
     * @description 分表策略, 该注解在方法上使用时，优先使用方法上指定的策略
     * @date 2020/9/9 9:58
     **/
    Class<? extends ITableShardStrategy> strategy();


}