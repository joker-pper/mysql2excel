package com.joker17.mysql2excel.excel;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLongestMatchColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

    private static final int MAX_COLUMN_WIDTH = 255;

    private Map<Integer, Map<Integer, Integer>> cache = new HashMap<Integer, Map<Integer, Integer>>(8);

    /**
     * 增加列宽值 (用于调整自适应宽度)
     */
    private int raiseColumnWidth = 0;

    public CustomLongestMatchColumnWidthStyleStrategy() {
    }

    public CustomLongestMatchColumnWidthStyleStrategy(int raiseColumnWidth) {
        this.raiseColumnWidth = raiseColumnWidth;
    }

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> cellDataList, Cell cell, Head head,
                                  Integer relativeRowIndex, Boolean isHead) {
        boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
        if (!needSetWidth) {
            return;
        }
        Map<Integer, Integer> maxColumnWidthMap = cache.get(writeSheetHolder.getSheetNo());
        if (maxColumnWidthMap == null) {
            maxColumnWidthMap = new HashMap<Integer, Integer>(16);
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
        Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
        if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
            maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
            writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
        }
    }

    private int dataLength(List<CellData> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        }

        CellDataTypeEnum type = null;
        CellData cellData = null;
        if (cellDataList != null && !cellDataList.isEmpty()) {
            cellData = cellDataList.get(0);
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
