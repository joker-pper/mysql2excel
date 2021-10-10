package com.joker17.mysql2excel.utils;

import org.springframework.util.DigestUtils;

import java.io.*;

public class FileUtils {

    private FileUtils() {
    }

    /**
     * mkdirs
     *
     * @param parentFile
     */
    public static void mkdirs(File parentFile) {
        if (parentFile == null) {
            return;
        }
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            parentFile.mkdirs();
        }
    }

    /**
     * 获取文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean isFileAndExists(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        return true;
    }

    /**
     * 获取输出excel文件对象
     *
     * @param outPath
     * @param fileName
     * @param excelType
     * @return
     */
    public static File getOutExcelFile(String outPath, String fileName, String excelType) {
        StringBuilder sb = new StringBuilder();

        if (outPath != null && !outPath.isEmpty()) {
            sb.append(outPath);
            if (!outPath.endsWith("/") && !outPath.endsWith("\\")) {
                sb.append("/");
            }
        }

        sb.append(fileName);

        if (!fileName.contains(".")) {
            //文件名不包含.时
            sb.append(".");
            sb.append(excelType);
        }

        return new File(sb.toString());
    }

    /**
     * 获取MD5值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String md5DigestAsHex(File file) throws IOException {
        return md5DigestAsHex(new BufferedInputStream(new FileInputStream(file)));
    }

    /**
     * 获取MD5值
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String md5DigestAsHex(InputStream inputStream) throws IOException {
        try {
            return DigestUtils.md5DigestAsHex(inputStream);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

}
