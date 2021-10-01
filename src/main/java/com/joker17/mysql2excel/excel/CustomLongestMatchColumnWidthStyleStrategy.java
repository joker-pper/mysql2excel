package com.joker17.mysql2excel.excel;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.write.handler.AbstractCellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLongestMatchColumnWidthStyleStrategy extends AbstractCellWriteHandler {

    private static final int MAX_COLUMN_WIDTH = 255;

    private static final int WIDTH_UNIT = 256;

    private Map<Integer, Map<Integer, Integer>> cache = new HashMap<>(8);

    /**
     * 增加列宽值 (用于调整自适应宽度)
     */
    private int raiseColumnWidth;

    /**
     * 是否一直重置列宽
     */
    private boolean alwaysResetColumnWidth;

    public CustomLongestMatchColumnWidthStyleStrategy() {
        this(false);
    }

    public CustomLongestMatchColumnWidthStyleStrategy(boolean alwaysResetColumnWidth) {
        this(alwaysResetColumnWidth, 2);
    }

    public CustomLongestMatchColumnWidthStyleStrategy(boolean alwaysResetColumnWidth, int raiseColumnWidth) {
        this.alwaysResetColumnWidth = alwaysResetColumnWidth;
        this.raiseColumnWidth = raiseColumnWidth;
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        this.setColumnWidth(writeSheetHolder, cellDataList, cell, head, relativeRowIndex, isHead);
    }

    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> cellDataList, Cell cell, Head head,
                                  Integer relativeRowIndex, Boolean isHead) {
        boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
        if (!needSetWidth) {
            return;
        }

        Map<Integer, Integer> maxColumnWidthMap = cache.get(writeSheetHolder.getSheetNo());
        if (maxColumnWidthMap == null) {
            maxColumnWidthMap = new HashMap<>(16);
            cache.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
        }

        int columnWidth = (int) (dataLength(cellDataList, cell, isHead) * 1.2F);
        if (columnWidth < 0) {
            return;
        }

        //增加列宽
        columnWidth += raiseColumnWidth;

        if (columnWidth > MAX_COLUMN_WIDTH) {
            columnWidth = MAX_COLUMN_WIDTH;
        }

        int columnIndex = cell.getColumnIndex();
        Integer maxColumnWidth = maxColumnWidthMap.get(columnIndex);
        if (maxColumnWidth == null) {
            Sheet sheet = writeSheetHolder.getSheet();
            int cellDefaultWidth = sheet.getColumnWidth(columnIndex);
            int cellDefaultColumnWidth = cellDefaultWidth / WIDTH_UNIT;
            if (!alwaysResetColumnWidth && cellDefaultColumnWidth > columnWidth) {
                maxColumnWidthMap.put(columnIndex, cellDefaultColumnWidth);
            } else {
                maxColumnWidthMap.put(columnIndex, columnWidth);
                sheet.setColumnWidth(columnIndex, columnWidth * WIDTH_UNIT);
            }
        } else {
            if (columnWidth > maxColumnWidth) {
                Sheet sheet = writeSheetHolder.getSheet();
                maxColumnWidthMap.put(columnIndex, columnWidth);
                sheet.setColumnWidth(columnIndex, columnWidth * WIDTH_UNIT);
            }
        }

    }

    private int dataLength(List<CellData> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        }

        CellData cellData = cellDataList.get(0);
        CellDataTypeEnum type = null;
        if (cellData != null) {
            type = cellData.getType();
        }

        if (type == null) {
            return -1;
        }
        switch (type) {
            case STRING:
                return cellData.getStringValue().getBytes().length;
            case BOOLEAN:
                return cellData.getBooleanValue().toString().getBytes().length;
            case NUMBER:
                return cellData.getNumberValue().toString().getBytes().length;
            default:
                return -1;
        }
    }

}
