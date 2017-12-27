package com.hision.erp.viewmodel;

import java.util.Collections;
import java.util.List;

import com.hision.erp.bean.Shelf;

import lombok.Data;

@Data
public class Widgets {

	private List<List<Shelf>> rowList;
	
	public static List<List<Shelf>>  EMPTY(){
		return Collections.emptyList();
	}
}
