package de.bushnaq.abdalla.util;

import org.apache.poi.ss.usermodel.CellType;

public class ColumnHeader {
    public int index;
    public final String name;
    public final CellType type;
    final boolean optional;

    public ColumnHeader(String name, CellType type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

}
