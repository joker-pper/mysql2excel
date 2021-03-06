package com.joker17.mysql2excel;

import com.joker17.mysql2excel.core.Mysql2ExcelExecutor;
import java.io.IOException;

public class Mysql2ExcelMain {

    public static void main(String[] args) throws IOException {
        Mysql2ExcelExecutor.INSTANCE.execute(args);
    }
}
