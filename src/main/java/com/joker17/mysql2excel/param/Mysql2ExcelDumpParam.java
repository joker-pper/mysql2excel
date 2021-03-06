package com.joker17.mysql2excel.param;

import com.beust.jcommander.Parameter;

public class Mysql2ExcelDumpParam {

    /**
     * 数据源配置文件
     */
    @Parameter(names = {"-data-source"}, required = true, description = "data source properties")
    private String dataSourceProperties;

    /**
     * 输出路径
     */
    @Parameter(names = {"-out-path"}, description = "out file path")
    private String outPath;

    /**
     * 文件名称,不包含后缀
     */
    @Parameter(names = {"-file-name"}, required = true, description = "out file name")
    private String fileName;

    /**
     * excel type
     */
    @Parameter(names = {"-excel-type"},  description = "excel type, values: xls / xlsx, default value xls")
    private String excelType;


    /**
     * 要筛选的table 以空格分隔 可为空(全部)
     */
    @Parameter(names = "-filter-table", description = "optional, more should be separated by a space")
    private String filterTable;

    /**
     * 是否为排除模式
     */
    @Parameter(names = "-exclude", description = "exclude table mode")
    private boolean excludeMode;


    @Parameter(names = {"--help", "--h"}, help = true, order = 5)
    private boolean help;

    public String getDataSourceProperties() {
        return dataSourceProperties;
    }

    public void setDataSourceProperties(String dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExcelType() {
        return excelType;
    }

    public void setExcelType(String excelType) {
        this.excelType = excelType;
    }

    public String getFilterTable() {
        return filterTable;
    }

    public void setFilterTable(String filterTable) {
        this.filterTable = filterTable;
    }

    public boolean isExcludeMode() {
        return excludeMode;
    }

    public void setExcludeMode(boolean excludeMode) {
        this.excludeMode = excludeMode;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }
}
