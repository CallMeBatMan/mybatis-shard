package com.batman.shard.demo;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Man
 * @description 分表
 * @date 2020/8/31 14:15
 **/
public final class TableUtil {

    private static final Set<String> ForbiddenSet = new HashSet<>();

    static {
        ForbiddenSet.add("delete ");
        ForbiddenSet.add("DELETE ");
        ForbiddenSet.add("update ");
        ForbiddenSet.add("UPDATE ");
        ForbiddenSet.add("drop ");
        ForbiddenSet.add("DROP ");
        ForbiddenSet.add("create ");
        ForbiddenSet.add("CREATE ");
        ForbiddenSet.add("alter ");
        ForbiddenSet.add("ALTER ");
        ForbiddenSet.add("rename ");
        ForbiddenSet.add("RENAME ");
        ForbiddenSet.add("truncate ");
        ForbiddenSet.add("TRUNCATE ");
        ForbiddenSet.add("grant ");
        ForbiddenSet.add("GRANT ");
        ForbiddenSet.add("revoke ");
        ForbiddenSet.add("REVOKE ");
        ForbiddenSet.add("audit ");
        ForbiddenSet.add("AUDIT ");
        ForbiddenSet.add("noaudit ");
        ForbiddenSet.add("NOAUDIT ");
        ForbiddenSet.add("comment ");
        ForbiddenSet.add("COMMENT ");
    }

    /**
     * @param shardKeyVal 马甲id
     * @param baseName    源表名
     * @return java.lang.String
     * @author Man
     * @description 获取分表后的表名
     * @date 2020/8/31 16:48
     **/
    public static String generateTableName(String shardKeyVal, String baseName) {
        // for (String forbidden : ForbiddenSet) {
        //     if (shardKeyVal.contentEquals(forbidden) || baseName.contains(forbidden)) {
        //         throw new RuntimeException(
        //                 MessageFormat.format("sql ddl风险! shardKeyVal:{0}, baseName:{1}", shardKeyVal, baseName)
        //         );
        //     }
        // }
        String newTblName = baseName + "_" + shardKeyVal;
        if (newTblName.length() > 64) {
            throw new RuntimeException(
                    MessageFormat.format("表名超长:max=64 newTblName:{0}", newTblName)
            );
        }
        return newTblName;
    }
}