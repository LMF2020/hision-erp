package com.hision.erp.viewmodel;

import lombok.Data;

/**
 * 单个货架的物料信息汇总
 *
 */
@Data
public class Shelf_Material_Summary {
	
	private String shelfCode;
	private String code;
	private String name;
	private Integer totalOnShelf;

}
