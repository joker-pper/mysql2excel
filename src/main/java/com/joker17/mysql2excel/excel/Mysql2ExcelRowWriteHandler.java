package com.joker17.mysql2excel.excel;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;

import java.util.Objects;

public class Mysql2ExcelRowWriteHandler extends AbstractRowWriteHandler {

    /**
     * table name 索引位置
     */
    private final static int TABLE_NAME_INDEX = 1;

    /**
     * 总共列数
     */
    private int totalCellNum;

    /**
     * 上一个表名
     */
    private String lastTableName = null;

    /**
     * 默认行分割线样式
     */
    private CellStyle DEFAULT_CELL_ROW_STYLE = null;


    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (Boolean.TRUE == isHead) {
            //首行时获取总列数
            totalCellNum = row.getPhysicalNumberOfCells();
            return;
        }

        Cell tableNameCell = row.getCell(TABLE_NAME_INDEX);
        String tableNameCellValue = tableNameCell.getStringCellValue();
        if (lastTableName != null && !Objects.equals(lastTableName, tableNameCellValue)) {
            //当表名发生变化时,设置行分割线样式
            for (int i = 0; i < totalCellNum; i++) {
                Cell cell = i == TABLE_NAME_INDEX ? tableNameCell : row.getCell(i);
                CellStyle cellStyle = getDefaultCellStyleForRow(tableNameCell.getCellStyle(), row);
                cell.setCellStyle(cellStyle);
            }

        }

        lastTableName = tableNameCellValue;

    }

    private CellStyle getDefaultCellStyleForRow(CellStyle cellStyle, Row row) {
        if (DEFAULT_CELL_ROW_STYLE == null) {
            synchronized (this) {
                if (DEFAULT_CELL_ROW_STYLE == null) {
                    Workbook workbook = row.getSheet().getWorkbook();
                    CellStyle workbookCellStyle = workbook.createCellStyle();
                    if (cellStyle != null) {
                        //使用基础配置
                        workbookCellStyle.setAlignment(cellStyle.getAlignmentEnum());
                        workbookCellStyle.setVerticalAlignment(cellStyle.getVerticalAlignmentEnum());
                        workbookCellStyle.setFont(workbook.getFontAt(cellStyle.getFontIndex()));
                        workbookCellStyle.setWrapText(cellStyle.getWrapText());
                        workbookCellStyle.setIndention(cellStyle.getIndention());
                    }

                    workbookCellStyle.setTopBorderColor((short) IndexedColors.BLACK);
                    workbookCellStyle.setBorderTop(BorderStyle.THIN);

                    DEFAULT_CELL_ROW_STYLE = workbookCellStyle;
                }
            }
        }
        return DEFAULT_CELL_ROW_STYLE;
    }

}
