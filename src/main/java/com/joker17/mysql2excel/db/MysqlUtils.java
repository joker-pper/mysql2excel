package com.joker17.mysql2excel.db;

import com.joker17.mysql2excel.utils.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MysqlUtils {

    /**
     * 获取数据库名称
     *
     * @param jdbcTemplate
     * @return
     */
    public static String getDatabase(JdbcTemplate jdbcTemplate) {
        String sql = "SELECT DATABASE()";
        List<String> resultList = jdbcTemplate.queryForList(sql, String.class);
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    /**
     * 获取表名列表
     *
     * @param jdbcTemplate
     * @return
     */
    public static List<String> getTableNameList(JdbcTemplate jdbcTemplate) {
        String sql = "SHOW TABLE STATUS WHERE 1=1";
        List<Map<String, Object>> resultMapList = jdbcTemplate.queryForList(sql);
        List<String> resultList = new ArrayList<>(32);
        resultMapList.forEach(resultMap -> {
            resultList.add(StringUtils.convertString(resultMap.get("Name")));
        });
        return resultList;
    }


    /**
     * execute sql
     *
     * @param jdbcTemplate
     * @param sql
     */
    public static void execute(JdbcTemplate jdbcTemplate, String sql) {
        jdbcTemplate.execute(sql);
    }

}
