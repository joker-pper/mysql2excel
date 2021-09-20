package com.joker17.mysql2excel.helper;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.joker17.mysql2excel.constants.Mysql2ExcelConstants;
import com.joker17.mysql2excel.enums.ColumnKeyTypeEnum;
import com.joker17.mysql2excel.model.Mysql2ExcelColumnModel;
import com.joker17.mysql2excel.utils.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class Mysql2ExcelHelper {

    private Mysql2ExcelHelper() {
    }

    /**
     * 获取excelType
     *
     * @param excelType
     * @return
     */
    public static String getExcelType(String excelType) {
        excelType = StringUtils.trimToNull(excelType);
        if (excelType == null) {
            return Mysql2ExcelConstants.XLS;
        }
        return StringUtils.toLowerCase(excelType);
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
     * 获取table中column list
     *
     * @param jdbcTemplate
     * @param database
     * @param tableName
     * @return
     */
    public static List<Mysql2ExcelColumnModel> getMysqlColumnModelList(JdbcTemplate jdbcTemplate, String database, String tableName) {
        String sql = String.format("SHOW FULL FIELDS FROM `%s`", tableName);
        List<Map<String, Object>> resultMapList = jdbcTemplate.queryForList(sql);
        List<Mysql2ExcelColumnModel> resultList = new ArrayList<>(64);
        //待处理column key的map
        Map<String, Mysql2ExcelColumnModel> toResolveColumnKeyTypeMap = new HashMap<>(8);
        resultMapList.forEach(resultMap -> {
            Mysql2ExcelColumnModel mysql2ExcelColumnModel = new Mysql2ExcelColumnModel();
            mysql2ExcelColumnModel.setTableSchema(database);
            mysql2ExcelColumnModel.setTableName(tableName);
            mysql2ExcelColumnModel.setColumnName(StringUtils.convertString(resultMap.get("Field")));
            mysql2ExcelColumnModel.setColumnType(StringUtils.convertString(resultMap.get("Type")));
            mysql2ExcelColumnModel.setColumnDefaultValue(StringUtils.convertString(resultMap.get("Default")));
            mysql2ExcelColumnModel.setColumnNotNull(MysqlBoostHelper.getConvertColumnNotNull(StringUtils.convertString(resultMap.get("Null"))));
            mysql2ExcelColumnModel.setColumnComment(StringUtils.convertString(resultMap.get("Comment")));
            mysql2ExcelColumnModel.setColumnExtra(MysqlBoostHelper.getFilteredColumnExtra(StringUtils.convertString(resultMap.get("Extra"))));

            String columnKeyType = MysqlBoostHelper.getConvertColumnKeyType(StringUtils.convertString(resultMap.get("Key")));
            ColumnKeyTypeEnum columnKeyTypeEnum = MysqlBoostHelper.getColumnKeyType(columnKeyType);
            if (columnKeyTypeEnum == ColumnKeyTypeEnum.PRIMARY_KEY) {
                toResolveColumnKeyTypeMap.put(mysql2ExcelColumnModel.getColumnName(), mysql2ExcelColumnModel);
            }
            mysql2ExcelColumnModel.setColumnKeyType(columnKeyType);
            resultList.add(mysql2ExcelColumnModel);
        });

        if (!toResolveColumnKeyTypeMap.isEmpty()) {
            //处理PRI返回的字段实际类型是UK/PK/PK且UK?
            // (表中只存在唯一索引(包含复合唯一索引)不存在主键时通过 desc table / SHOW FULL FIELDS FROM table 返回的 Key -> PRI)
            List<Map<String, Object>> indexResultMapList = jdbcTemplate.queryForList(String.format("SHOW INDEX FROM `%s`", tableName));
            Map<String, List<Map<String, Object>>> columnName2IndexResultMapListMap = indexResultMapList.stream().collect(Collectors.groupingBy(it -> StringUtils.convertString(it.get("Column_name"))));
            Map<String, List<Map<String, Object>>> keyName2IndexResultMapListMap = indexResultMapList.stream().collect(Collectors.groupingBy(it -> StringUtils.convertString(it.get("Key_name"))));

            toResolveColumnKeyTypeMap.forEach((columnName, mysqlColumnModel) -> {
                List<Map<String, Object>> columnName2IndexResultMapList = columnName2IndexResultMapListMap.get(columnName);
                if (columnName2IndexResultMapList != null) {
                    boolean hasPrimaryKey = false;
                    boolean hasUniqueKey = false;
                    for (Map<String, Object> columnName2IndexResultMap : columnName2IndexResultMapList) {
                        if (StringUtils.equals((String) columnName2IndexResultMap.get("Key_name"), "PRIMARY")) {
                            hasPrimaryKey = true;
                        } else {
                            if (StringUtils.equals(StringUtils.convertString(columnName2IndexResultMap.get("Non_unique")), "0")) {
                                //是Unique约束时
                                if (keyName2IndexResultMapListMap.get(StringUtils.convertString(columnName2IndexResultMap.get("Key_name"))).size() == 1) {
                                    //该列存在单独的Unique约束时
                                    hasUniqueKey = true;
                                }
                            }
                        }

                        if (hasPrimaryKey && hasUniqueKey) {
                            break;
                        }
                    }

                    if (hasPrimaryKey && hasUniqueKey) {
                        mysqlColumnModel.setColumnKeyType(ColumnKeyTypeEnum.Constants.PRIMARY_AND_UNIQUE_KEY);
                    } else {
                        if (hasPrimaryKey) {
                            mysqlColumnModel.setColumnKeyType(ColumnKeyTypeEnum.Constants.PRIMARY_KEY);
                        } else if (hasUniqueKey) {
                            mysqlColumnModel.setColumnKeyType(ColumnKeyTypeEnum.Constants.UNIQUE_KEY);
                        } else {
                            //e.g: 表中只存在一组  UNIQUE KEY `uk_name_code` (`name`,`code`)
                            mysqlColumnModel.setColumnKeyType(null);
                        }
                    }
                }
            });
        }

        return resultList;
    }
}
