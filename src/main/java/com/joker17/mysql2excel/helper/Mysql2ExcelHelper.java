package com.joker17.mysql2excel.helper;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.joker17.mysql2excel.constants.Mysql2ExcelConstants;
import com.joker17.mysql2excel.model.Mysql2ExcelModel;
import com.joker17.mysql2excel.utils.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

public class Mysql2ExcelHelper {

    /**
     * 获取excelType
     *
     * @param excelType
     * @return
     */
    public static String getExcelType(String excelType) {
        excelType = excelType == null || excelType.trim().isEmpty() ? Mysql2ExcelConstants.XLS : excelType.trim().toLowerCase(Locale.ROOT);
        return excelType;
    }

    /**
     * 获取excelTypeEnum
     *
     * @param excelType
     * @return
     */
    public static ExcelTypeEnum getExcelTypeEnum(String excelType) {
        return Objects.equals(Mysql2ExcelConstants.XLS, excelType) ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX;
    }


    /**
     * 是否为匹配到要处理的表名
     *
     * @param filterTableNames 过滤的表名
     * @param tableName        表名
     * @param exclude          true: 排除, false: 包含
     * @return
     */
    public static boolean isMatchResolveTableName(Collection<String> filterTableNames, String tableName, boolean exclude) {
        if (filterTableNames == null || filterTableNames.isEmpty()) {
            return true;
        }
        boolean contains = filterTableNames.contains(tableName);
        return exclude ? !contains : contains;
    }


    public static List<Mysql2ExcelModel> getMysql2ExcelModelList(JdbcTemplate jdbcTemplate, String database, String tableName) {
        String sql = String.format("SHOW FULL FIELDS FROM `%s`", tableName);

        List<Map<String, Object>> resultMapList = jdbcTemplate.queryForList(sql);
        List<Mysql2ExcelModel> resultList = new ArrayList<>(64);
        resultMapList.forEach(resultMap -> {
            Mysql2ExcelModel mysql2ExcelModel = new Mysql2ExcelModel();
            mysql2ExcelModel.setTableSchema(database);
            mysql2ExcelModel.setTableName(tableName);
            mysql2ExcelModel.setColumnName(StringUtils.convertString(resultMap.get("Field")));
            mysql2ExcelModel.setColumnType(StringUtils.convertString(resultMap.get("Type")));
            mysql2ExcelModel.setColumnDefaultValue(StringUtils.convertString(resultMap.get("Default")));
            mysql2ExcelModel.setColumnNotNull(getConvertColumnNotNull(StringUtils.convertString(resultMap.get("Null"))));
            mysql2ExcelModel.setColumnComment(StringUtils.convertString(resultMap.get("Comment")));
            mysql2ExcelModel.setColumnExtra(StringUtils.convertString(resultMap.get("Extra")));
            mysql2ExcelModel.setColumnKeyType(getConvertColumnKeyType(StringUtils.convertString(resultMap.get("Key"))));
            resultList.add(mysql2ExcelModel);
        });

        return resultList;
    }

    /**
     * 获取列是否不为空
     *
     * @param nullText
     * @return
     */
    public static String getConvertColumnNotNull(String nullText) {
        if (nullText == null || nullText.contains("Y") || nullText.contains("y")) {
            return "N";
        }
        return "Y";
    }

    /**
     * 获取列类型
     *
     * @param text
     * @return
     */
    public static String getConvertColumnKeyType(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        if (Objects.equals("PRI", text)) {
            //PRIMARY KEY
            return "PK";
        }
        if (Objects.equals("UNI", text)) {
            //UNIQUE KEY
            return "UK";
        }
        return null;
    }
}
