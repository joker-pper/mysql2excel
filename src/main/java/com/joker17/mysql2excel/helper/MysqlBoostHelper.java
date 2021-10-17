package com.joker17.mysql2excel.helper;

import com.joker17.mysql2excel.enums.ColumnKeyTypeEnum;
import com.joker17.mysql2excel.utils.StringUtils;

import java.util.Collection;
import java.util.Objects;

public class MysqlBoostHelper {

    private MysqlBoostHelper() {
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
        boolean contains = isMatches(filterTableNames, tableName);
        return exclude ? !contains : contains;
    }

    /**
     * 获取是否匹配到text
     *
     * @param filterTexts
     * @param text
     * @return
     */
    public static boolean isMatches(Collection<String> filterTexts, String text) {
        if (filterTexts.contains(text)) {
            //直接包含时
            return true;
        }
        for (String filterText : filterTexts) {
            if (text.matches(filterText)) {
                //正则匹配时
                return true;
            }
        }
        return false;
    }

    /**
     * 获取列是否不为空结果
     *
     * @param nullText
     * @return
     */
    public static String getConvertColumnNotNull(String nullText) {
        if (nullText == null || nullText.contains("Y") || nullText.contains("y")) {
            //当null text包含y时为 Null
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
            return ColumnKeyTypeEnum.Constants.PRIMARY_KEY;
        }
        if (Objects.equals("UNI", text)) {
            //UNIQUE KEY
            return ColumnKeyTypeEnum.Constants.UNIQUE_KEY;
        }
        return null;
    }


    /**
     * 是否为PK
     *
     * @param text
     * @return
     */
    public static boolean isPrimaryKey(String text) {
        text = StringUtils.toUpperCase(StringUtils.trimToEmpty(text));
        return Objects.equals(ColumnKeyTypeEnum.Constants.PRIMARY_KEY, text) || Objects.equals("PRI", text) || Objects.equals("PRIMARY KEY", text);
    }

    /**
     * 是否为UK
     *
     * @param text
     * @return
     */
    public static boolean isUniqueKey(String text) {
        text = StringUtils.toUpperCase(StringUtils.trimToEmpty(text));
        return Objects.equals(ColumnKeyTypeEnum.Constants.UNIQUE_KEY, text) || Objects.equals("UNI", text) || Objects.equals("UNIQUE KEY", text);
    }

    /**
     * 是否为PK+UK
     *
     * @param text
     * @return
     */
    public static boolean isPrimaryAndUniqueKey(String text) {
        text = StringUtils.toUpperCase(StringUtils.trimToEmpty(text));
        return Objects.equals(ColumnKeyTypeEnum.Constants.PRIMARY_AND_UNIQUE_KEY, text);
    }


    /**
     * 获取ColumnKeyTypeEnum
     *
     * @param columnKeyType
     * @return
     */
    public static ColumnKeyTypeEnum getColumnKeyType(String columnKeyType) {
        if (StringUtils.isEmpty(columnKeyType)) {
            return ColumnKeyTypeEnum.OTHER;
        }

        if (isPrimaryAndUniqueKey(columnKeyType)) {
            return ColumnKeyTypeEnum.PRIMARY_AND_UNIQUE_KEY;
        }

        if (isPrimaryKey(columnKeyType)) {
            return ColumnKeyTypeEnum.PRIMARY_KEY;
        }

        if (isUniqueKey(columnKeyType)) {
            return ColumnKeyTypeEnum.UNIQUE_KEY;
        }

        return ColumnKeyTypeEnum.OTHER;
    }

    /**
     * 获取过滤后的columnExtra
     *
     * @param columnExtra
     * @return
     */
    public static String getFilteredColumnExtra(String columnExtra) {
        return getFilteredColumnExtra(columnExtra, false);
    }

    /**
     * 获取过滤后的columnExtra
     *
     * @param columnExtra
     * @param toUpperCase
     * @return
     */
    public static String getFilteredColumnExtra(String columnExtra, boolean toUpperCase) {
        return StringUtils.trimToNull(StringUtils.removeStart(toUpperCase ? StringUtils.toUpperCase(columnExtra) : columnExtra, "DEFAULT_GENERATED"));
    }

}
