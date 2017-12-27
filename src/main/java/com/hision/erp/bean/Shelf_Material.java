package com.hision.erp.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

import lombok.Data;
import lombok.NonNull;

/**
 * 货架-物料-映射表 Created by Jed on 2017/12/17.
 *
 */
@Data
@Table("tbl_shelf_material")
@PK({ "shelfId", "materialCode" })
public class Shelf_Material extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	public Shelf_Material() {}

	@Column("shelf_id")
	@Comment("货架ID")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String shelfId;

	@Column("material_code")
	@Comment("物料编码")
	@NonNull
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String materialCode;

	@Column("store_count")
	@Comment("库存")
	@NonNull
	@ColDefine(type = ColType.INT)
	private Integer storeCount;

}
