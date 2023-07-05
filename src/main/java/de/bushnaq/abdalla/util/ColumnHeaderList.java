package de.bushnaq.abdalla.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.Map;

public class ColumnHeaderList {
    public static final String BORN_COLUMN = "Born";
    public static final String COMMENT_COLUMN = "Comment";
    public static final String COMMENT_COLUMN_ORIGINAL_LANGUAGE = "Comment (OL)";
    public static final String DIED_COLUMN = "Died";
    public static final String FAMILY_LETTER_COLUMN = "Family Letter";
    public static final String FATHER_COLUMN = "Father";
    public static final String FIRST_NAME_COLUMN = "First Name";
    public static final String FIRST_NAME_COLUMN_ORIGINAL_LANGUAGE = "First Name (OL)";
    public static final String ID_COLUMN = "ID";
    public static final String LAST_NAME_COLUMN = "Last Name";
    public static final String LAST_NAME_COLUMN_ORIGINAL_LANGUAGE = "Last Name (OL)";
    public static final String MOTHER_COLUMN = "Mother";
    public static final String SEX_COLUMN = "Sex";

    private final Map<Integer, ColumnHeader> indexMap = new HashMap<>();
    private final Map<String, ColumnHeader> nameMap = new HashMap<>();

    public ColumnHeaderList() {
        put(ID_COLUMN, CellType.FORMULA, false, 0);
        put(SEX_COLUMN, CellType.STRING, false, 1);
        put(FIRST_NAME_COLUMN, CellType.STRING, false, 2);
        put(FIRST_NAME_COLUMN_ORIGINAL_LANGUAGE, CellType.STRING, false, 3);
        put(FAMILY_LETTER_COLUMN, CellType.STRING, false, 4);
        put(LAST_NAME_COLUMN, CellType.STRING, false, 5);
        put(LAST_NAME_COLUMN_ORIGINAL_LANGUAGE, CellType.STRING, false, 6);
        put(BORN_COLUMN, CellType.NUMERIC, false, 7);
        put(DIED_COLUMN, CellType.NUMERIC, false, 8);
        put(FATHER_COLUMN, CellType.FORMULA, false, 9);
        put(MOTHER_COLUMN, CellType.FORMULA, false, 10);
        put(COMMENT_COLUMN, CellType.STRING, false, 11);
        put(COMMENT_COLUMN_ORIGINAL_LANGUAGE, CellType.STRING, false, 12);
    }

    public ColumnHeader get(int columnIndex) {
        return indexMap.get(columnIndex);
    }

    public ColumnHeader get(String columnName) {
        return nameMap.get(columnName);
    }

    public String getExcelColumn(String columnName) {
        return ExcelUtil.columnIndexToExcelColumnName(nameMap.get(columnName).index);
    }

    public int getExcelColumnIndex(String columnName) {
        ColumnHeader columnHeader = nameMap.get(columnName);
        if (columnHeader != null) {
            return columnHeader.index;
        } else {
            return 0;
        }
    }

    private void put(String columnName, CellType cellType, boolean optional) {
        nameMap.put(columnName, new ColumnHeader(columnName, cellType, optional));
    }

    private void put(String columnName, CellType cellType, boolean optional, int index) {
        nameMap.put(columnName, new ColumnHeader(columnName, cellType, optional, index));
        indexMap.put(index, nameMap.get(columnName));
    }

    public void register(ExcelErrorHandler geh, Row row, int columnIndex, String columnName) {
        ColumnHeader columnHeader = nameMap.get(columnName);
        if (geh.isNotNull(String.format(ErrorMessages.ERROR_102_UNKNOWN_HEADER, columnName, ExcelUtil.columnIndexToExcelColumnName(columnIndex)), row, columnIndex, columnHeader)) {
            columnHeader.index = columnIndex;// ---The column index we found the column at
            indexMap.put(columnIndex, columnHeader);
        }
    }

    public int size() {
        return nameMap.keySet().size();
    }

    public void testForMissingColumns(ExcelErrorHandler geh, Row row) {
        for (String columnName : nameMap.keySet()) {
            if (!nameMap.get(columnName).optional) {
                if (!geh.isTrue(String.format(ErrorMessages.ERROR_101_COLUMN_S_IS_MISSING, columnName), row, 0, indexMap.containsValue(nameMap.get(columnName)))) {
                    break;
                }
            }
        }
    }

}
