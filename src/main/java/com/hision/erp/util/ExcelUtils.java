package com.hision.erp.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import com.google.common.base.Strings;

public class ExcelUtils {
    /**
     * 获取单元格值
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null
                || (cell.getCellTypeEnum() == CellType.STRING && Strings.isNullOrEmpty(cell
                .getStringCellValue()))) {
            return null;
        }
        CellType cellType = cell.getCellTypeEnum();
            if(cellType == CellType.BLANK)
                return null;
            else if(cellType == CellType.BOOLEAN)
                return cell.getBooleanCellValue();
            else if(cellType == CellType.ERROR)
                return cell.getErrorCellValue();
            else if(cellType == CellType.FORMULA) {
                try {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue();
                    } else {
                        return cell.getNumericCellValue();
                    }
                } catch (IllegalStateException e) {
                    return cell.getRichStringCellValue();
                }
            }
            else if(cellType == CellType.NUMERIC){
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            }
            else if(cellType == CellType.STRING)
                return cell.getStringCellValue();
            else
                return null;
    }
}
