package com.joker17.mysql2excel.core;

import com.joker17.mysql2excel.constants.Mysql2ExcelConstants;
import com.joker17.mysql2excel.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Mysql2ExcelExecutorTest {

    private final String targetClassPath = this.getClass().getClassLoader().getResource("").getPath();

    @Test
    public void executeWithHelpArg() throws IOException {
        Mysql2ExcelExecutor.INSTANCE.execute(new String[] {"--help"});
    }

    @Test
    public void execute() throws IOException {
        String outPath = "target/out-excel/default";
        String fileName = "test";
        for (String excelType : Arrays.asList(Mysql2ExcelConstants.XLS, Mysql2ExcelConstants.XLSX)) {
            execute(outPath, fileName, excelType, "");
        }
    }


    @Test
    public void executeWithFilterTableAndExclude() throws IOException {
        //排除user
        String outPath = "target/out-excel/filter-exclude";
        String fileName = "test";
        String filterTable = "user";
        for (String excelType : Arrays.asList(Mysql2ExcelConstants.XLS, Mysql2ExcelConstants.XLSX)) {
            execute(outPath, fileName, excelType, String.format("-filter-table %s -exclude", filterTable));
        }
    }

    @Test
    public void executeWithFilterTable() throws IOException {
        //只包含user
        String outPath = "target/out-excel/filter";
        String fileName = "test";
        String filterTable = "user";
        for (String excelType : Arrays.asList(Mysql2ExcelConstants.XLS, Mysql2ExcelConstants.XLSX)) {
            execute(outPath, fileName, excelType, String.format("-filter-table %s", filterTable));
        }
    }

    private void execute(String outPath, String fileName, String excelType, String appendText) throws IOException {
        String line = String.format("-data-source %s -out-path %s -file-name %s -excel-type %s %s",
                targetClassPath + "db.properties", outPath, fileName, excelType, appendText == null ? "" : appendText);
        String[] args = line.split(" ");

        File outExcelFile = FileUtils.getOutExcelFile(outPath, fileName, excelType);

        Mysql2ExcelExecutor.INSTANCE.execute(args);

        Assert.assertTrue(String.format("excel build fail : %s", outExcelFile.getPath()), outExcelFile != null && outExcelFile.exists() && outExcelFile.length() > 0);
    }
}