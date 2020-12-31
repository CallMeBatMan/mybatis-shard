package com.batman.mybatis.shard;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Man
 * @description
 * @date 2020/9/2
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        ),
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        )
})
public class ShardInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        System.out.println("MybatisInterceptor intercept:" + invocation.getTarget().getClass());
        if (!(invocation.getTarget() instanceof Executor)) {
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if (!TableCacheUtil.inited()) {
            // TODO 改为启动初始化
            doInit(mappedStatement);
        }

        TableShard tableShard = getTableShardAnnotation(mappedStatement);
        if (tableShard == null) {
            // 未使用注解，不分表
            return invocation.proceed();
        }
        // 获取原sql
        BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
        // 改变sql语句
        Map<String, Object> parameterMap = (HashMap<String, Object>) boundSql.getParameterObject();
        String originSql = boundSql.getSql();
        String newSql = tableShard.strategy().newInstance().tableShard(originSql, parameterMap, tableShard.shardKey());
        invocation.getArgs()[0] = copyMappedStatement(
                mappedStatement,
                new BoundSqlSqlSource(
                        new BoundSql(
                                mappedStatement.getConfiguration(), newSql,
                                boundSql.getParameterMappings(), boundSql.getParameterObject()
                        )
                )
        );
        // 执行sql
        return invocation.proceed();
    }

    /**
     * 将需要分表的表名“缓存”起来
     * // TODO 改为启动初始化
     *
     * @param mappedStatement
     */
    private void doInit(MappedStatement mappedStatement) {
        Collection<Class<?>> mappers = mappedStatement.getConfiguration().getMapperRegistry().getMappers();
        Set<String> tbls = new HashSet<>();
        for (Class<?> one : mappers) {
            for (Method method : one.getMethods()) {
                if (!method.isAnnotationPresent(TableShard.class)) {
                    continue;
                }
                TableShard annotation = method.getAnnotation(TableShard.class);
                if (annotation.tableNames() != null) {
                    tbls.addAll(Arrays.asList(annotation.tableNames()));
                }
            }
            // 检测注解
            if (!one.isAnnotationPresent(TableShard.class)) {
                continue;
            }
            TableShard annotation = one.getAnnotation(TableShard.class);
            if (annotation.tableNames() != null) {
                tbls.addAll(Arrays.asList(annotation.tableNames()));
            }
        }
        tbls.removeIf(one -> StringUtils.isEmpty(one));
        TableCacheUtil.init(tbls);
    }

    @Override
    public Object plugin(Object target) {
        // 返回代理类
//        System.out.println("MybatisInterceptor plugin" + target.getClass());
        return target instanceof Executor ? Plugin.wrap(target, this) : target;
    }

    @Override
    public void setProperties(Properties properties) {
//        System.out.println("MybatisInterceptor setProperties");
    }

    /**
     * 复制原始MappedStatement
     *
     * @param ms
     * @param newSqlSource
     * @return
     */
    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType()
        ).resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .statementType(ms.getStatementType())
                .keyGenerator(ms.getKeyGenerator())
                .timeout(ms.getTimeout())
                .parameterMap(ms.getParameterMap())
                .resultMaps(ms.getResultMaps())
                .cache(ms.getCache())
                .useCache(ms.isUseCache());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    /**
     * 获取方法上的TableShard注解
     *
     * @param mappedStatement MappedStatement
     * @return TableShard注解
     */
    private TableShard getTableShardAnnotation(MappedStatement mappedStatement) {
        try {
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            Class<?> aClass = Class.forName(className);
            String methodName = id.substring(id.lastIndexOf(".") + 1);
            for (Method method : aClass.getMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(TableShard.class)) {
                    return method.getAnnotation(TableShard.class);
                }
            }
            if (aClass.isAnnotationPresent(TableShard.class)) {
                return aClass.getAnnotation(TableShard.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
