package com.joker17.mysql2excel.core;

import com.joker17.mysql2excel.db.DataSourceUtils;
import com.joker17.mysql2excel.db.JdbcUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

public class DbTestHelper {

    private final static String TARGET_CLASS_PATH = DbTestHelper.class.getClassLoader().getResource("").getPath();

    private final static String DATA_SOURCE_PROPERTIES = "db.properties";

    public static File getDataSourcePropertiesFile() {
        return new File(TARGET_CLASS_PATH + DATA_SOURCE_PROPERTIES);
    }

    public static DataSource getDataSource() throws IOException {
        return DataSourceUtils.getDataSourceByCache(getDataSourcePropertiesFile());
    }

    public static JdbcTemplate getJdbcTemplate() throws IOException {
        return JdbcUtils.getJdbcTemplate(getDataSource());
    }
}
