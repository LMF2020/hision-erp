package com.hision.erp.viewmodel;

import lombok.Data;

@Data
public class RowCol {

	private int row;
	private int col;

	public RowCol() {
	}

	public RowCol(int row, int col) {
		this.row = row;
		this.col = col;
	}

}
