package com.hision.erp.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

import lombok.Data;
import lombok.NonNull;

/**
 * 货架表 Created by Jed on 2017/12/17.
 *
 */
@Data
@Table("tbl_shelf")
public class Shelf extends BaseModel {
	
	private static final long serialVersionUID = 1L;
	
	public Shelf() {}

	@Name
	@Comment("货架ID")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	@Prev(els = { @EL("uuid()") })
	private String id;

	@Column
	@Comment("货架编码")
	@NonNull
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String code;

	@Column
	@Comment("排序字段")
	@NonNull
	@ColDefine(type = ColType.INT)
	private Integer ordering;
}
