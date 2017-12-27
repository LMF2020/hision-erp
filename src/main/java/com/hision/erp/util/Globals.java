package com.hision.erp.util;

import java.util.List;

import com.google.common.collect.Lists;

public class Globals {

	// 行列基础配置
	public static final String SETTINGS = "settings";

	public static final int SUCC = 1;

	public static final int FAIL = 0;
	
	public static final int OUT_SHELF = 1;
	
	public static final int IN_SHELF = 0;
	
	public static final String EXCEL_TOTAL_COUNT = "total";
	public static final String EXCEL_SUCC_COUNT = "succ";
	public static final String EXCEL_FAIL_COUNT = "fail";
	public static final String EXCEL_IGNORE_COUNT = "ignore";
	/**
	 * 切割list
	 * 
	 * @param list
	 * @param buckets
	 * @return
	 */
	public static <T> List<List<T>> partition(List<T> list, int buckets) {
		int divide = list.size() / buckets;
		if (list.size() % buckets != 0) {
			divide++;
		}
		return Lists.partition(list, divide);
	}
}
