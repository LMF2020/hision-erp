package com.hision.erp.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import lombok.Data;
import lombok.NonNull;

/**
 * 物料信息表 Created by Jed on 2017/12/17.
 *
 */
@Data
@Table("tbl_material")
public class Material extends BaseModel {

	private static final long serialVersionUID = 1L;

	public Material() {}
	
	@Name
	@Comment("物料编码")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String code;

	@Column
	@Comment("物料描述")
	@NonNull
	@ColDefine(type = ColType.VARCHAR, width = 255)
	private String name;

	@Column
	@Comment("物料总计/或者叫库存")
	@NonNull
	@ColDefine(type = ColType.INT)
	private Integer total;

}
