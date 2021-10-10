package com.joker17.mysql2excel.core;

import com.joker17.mysql2excel.constants.Mysql2ExcelConstants;
import com.joker17.mysql2excel.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mysql2ExcelExecutorTest {

    private final String targetClassPath = this.getClass().getClassLoader().getResource("").getPath();

    @Test
    public void executeWithHelpArg() throws IOException {
        Mysql2ExcelExecutor.INSTANCE.execute(new String[]{"--help"});
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
    public void executeWithFilterTableAndExcludeMore() throws IOException {
        //排除user role
        String outPath = "target/out-excel/filter-exclude-more";
        String fileName = "test";
        String filterTable = "user role";
        for (String excelType : Arrays.asList(Mysql2ExcelConstants.XLS, Mysql2ExcelConstants.XLSX)) {
            execute(outPath, fileName, excelType, "", new String[]{"-filter-table", filterTable, "-exclude"});
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

    @Test
    public void executeWithFilterMoreTable() throws IOException {
        //只包含user role
        String outPath = "target/out-excel/filter-more";
        String fileName = "test";
        String filterTable = "user role";
        for (String excelType : Arrays.asList(Mysql2ExcelConstants.XLS, Mysql2ExcelConstants.XLSX)) {
            execute(outPath, fileName, excelType, "", new String[]{"-filter-table", filterTable});
        }
    }

    private void execute(String outPath, String fileName, String excelType, String appendText, String... appendArgs) throws IOException {
        String line = String.format("-data-source %s -out-path %s -file-name %s -excel-type %s %s",
                targetClassPath + "db.properties", outPath, fileName, excelType, appendText == null ? "" : appendText);
        String[] args = line.split(" ");

        String[] invokeArgs;
        if (appendArgs == null || appendArgs.length == 0) {
            invokeArgs = args;
        } else {
            List<String> invokeArgList = Stream.of(Arrays.asList(args), Arrays.asList(appendArgs))
                    .flatMap(List::stream).collect(Collectors.toList());
            invokeArgs = invokeArgList.toArray(new String[invokeArgList.size()]);
        }

        File outExcelFile = FileUtils.getOutExcelFile(outPath, fileName, excelType);

        Mysql2ExcelExecutor.INSTANCE.execute(invokeArgs);

        Assert.assertTrue(String.format("excel build fail : %s", outExcelFile.getPath()), outExcelFile != null && outExcelFile.exists() && outExcelFile.length() > 0);
    }
}