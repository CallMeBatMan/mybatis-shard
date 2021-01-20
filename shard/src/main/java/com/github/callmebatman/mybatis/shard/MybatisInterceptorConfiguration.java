package com.github.callmebatman.mybatis.shard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis拦截器注入配置
 */
@Configuration
public class MybatisInterceptorConfiguration {

    /**
     * 注册拦截器
     */
    @Bean
    public ShardInterceptor mybatisInterceptor() {
        return new ShardInterceptor();
    }


}
