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
 * 物料在货架上的出入库历史记录表
 * 
 * Created by Jed on 2017/12/17.
 *
 */
@Data
@Table("tbl_shelf_inout_his")
public class Shelf_InOutHis extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	public Shelf_InOutHis() {}

	@Name
	@Comment("ID")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	@Prev(els = { @EL("uuid()") })
	private String id;

	@Column("shelf_id")
	@Comment("货架ID")
	@NonNull
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String shelfId;

	@Column("shelf_code")
	@Comment("货架编码")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String shelfCode;

	@Column("material_code")
	@Comment("物料编码")
	@NonNull
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String materialCode;

	@Comment("类型 : 下架1,上架0")
	@ColDefine(type = ColType.INT)
	@NonNull
	private Integer type;

	@Column("count")
	@Comment("上架数量|下架数量")
	@NonNull
	@ColDefine(type = ColType.INT)
	private Integer curCount;
}
