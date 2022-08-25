package de.bushnaq.abdalla.util;

import org.apache.poi.ss.usermodel.CellType;

public class ColumnHeader {
	public int		index;
	public String	name;
	boolean			optional;
	public CellType	type;

	public ColumnHeader(String name, CellType type, boolean optional) {
		this.name = name;
		this.type = type;
		this.optional = optional;
	}

}
