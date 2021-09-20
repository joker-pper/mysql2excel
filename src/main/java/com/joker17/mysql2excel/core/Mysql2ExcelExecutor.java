package com.joker17.mysql2excel.core;

import com.alibaba.excel.EasyExcel;
import com.joker17.mysql2excel.constants.Mysql2ExcelConstants;
import com.joker17.mysql2excel.db.JdbcUtils;
import com.joker17.mysql2excel.db.MysqlUtils;
import com.joker17.mysql2excel.excel.CustomLongestMatchColumnWidthStyleStrategy;
import com.joker17.mysql2excel.excel.Mysql2ExcelRowWriteHandler;
import com.joker17.mysql2excel.helper.MysqlBoostHelper;
import com.joker17.mysql2excel.model.Mysql2ExcelColumnModel;
import com.joker17.mysql2excel.param.Mysql2ExcelDumpParam;
import com.joker17.mysql2excel.utils.FileUtils;
import com.joker17.mysql2excel.helper.Mysql2ExcelHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;

public class Mysql2ExcelExecutor extends AbstractMysql2ExcelExecutor {

    public static final Mysql2ExcelExecutor INSTANCE = new Mysql2ExcelExecutor();

    @Override
    protected void doWork(Mysql2ExcelDumpParam dumpParam) throws IOException {
        Objects.requireNonNull(dumpParam, "param must be not null");
        String dataSourceProperties = dumpParam.getDataSourceProperties();
        Objects.requireNonNull(dataSourceProperties, "data source properties must be not null");

        File dataSourcePropertiesFile = new File(dataSourceProperties);
        if (!FileUtils.isFileAndExists(dataSourcePropertiesFile)) {
            throw new FileNotFoundException(String.format("data source properties %s not found", dataSourceProperties));
        }

        String outPath = dumpParam.getOutPath();
        String fileName = dumpParam.getFileName();
        Objects.requireNonNull(fileName, "file name must be not null");

        String excelType = Mysql2ExcelHelper.getExcelType(dumpParam.getExcelType());
        if (!Arrays.asList(Mysql2ExcelConstants.XLS, Mysql2ExcelConstants.XLSX).contains(excelType)) {
            throw new IllegalArgumentException("invalid excel type");
        }

        String filterTable = dumpParam.getFilterTable();
        Set<String> filterTables = new HashSet<>(Arrays.asList(filterTable == null || filterTable.length() == 0 ? new String[]{} : filterTable.split(" ")));

        Properties properties = JdbcUtils.loadProperties(new FileInputStream(dataSourcePropertiesFile));
        DataSource dataSource = JdbcUtils.getDataSource(properties);
        JdbcTemplate jdbcTemplate = JdbcUtils.getJdbcTemplate(dataSource);

        //获取数据库名称
        String database = MysqlUtils.getDatabase(jdbcTemplate);

        //获取所有的表
        List<String> tableNameList = MysqlUtils.getTableNameList(jdbcTemplate);

        List<Mysql2ExcelColumnModel> allMysql2ExcelColumnModelList = new ArrayList<>(1024 * 2);
        tableNameList.forEach(tableName -> {
            boolean isMatchResolve = MysqlBoostHelper.isMatchResolveTableName(filterTables, tableName, dumpParam.isExcludeMode());
            if (isMatchResolve) {
                allMysql2ExcelColumnModelList.addAll(Mysql2ExcelHelper.getMysqlColumnModelList(jdbcTemplate, database, tableName));
            }
        });

        //获取输出excel文件对象
        File outExcelFile = FileUtils.getOutExcelFile(outPath, fileName, excelType);
        File outExcelParentFile = outExcelFile.getParentFile();
        //初始化文件夹
        FileUtils.mkdirs(outExcelParentFile);

        //输出excel
        EasyExcel.write(new FileOutputStream(outExcelFile))
                .excelType(Mysql2ExcelHelper.getExcelTypeEnum(excelType))
                .head(Mysql2ExcelColumnModel.class)
                .registerWriteHandler(new CustomLongestMatchColumnWidthStyleStrategy(2))
                .registerWriteHandler(new Mysql2ExcelRowWriteHandler())
                .useDefaultStyle(false)
                .sheet("Sheet1")
                .doWrite(allMysql2ExcelColumnModelList);

    }

}
