package com.joker17.mysql2excel.core;

import com.beust.jcommander.JCommander;
import com.joker17.mysql2excel.constants.Mysql2ExcelConstants;
import com.joker17.mysql2excel.param.Mysql2ExcelDumpParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractMysql2ExcelExecutor {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 业务逻辑
     *
     * @param dumpParam
     * @throws IOException
     */
    protected abstract void doWork(Mysql2ExcelDumpParam dumpParam) throws IOException;

    public void execute(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            args = new String[]{"--help"};
        }

        //解析param
        Mysql2ExcelDumpParam dumpParam = new Mysql2ExcelDumpParam();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(dumpParam)
                .build();
        jCommander.parse(args);

        if (dumpParam.isHelp()) {
            jCommander.usage();
            return;
        }

        LocalDateTime start = LocalDateTime.now();
        Mysql2ExcelConstants.LOG.info("start: {}", start.format(DATE_TIME_FORMATTER));

        //执行业务逻辑
        doWork(dumpParam);

        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        BigDecimal result = BigDecimal.valueOf(duration.toMillis()).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.HALF_UP);

        Mysql2ExcelConstants.LOG.info("end: {}", end.format(DATE_TIME_FORMATTER));
        Mysql2ExcelConstants.LOG.info("time consuming: {} s", result);
    }


}
