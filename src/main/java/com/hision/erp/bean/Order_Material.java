package com.hision.erp.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import lombok.Data;
import lombok.NonNull;

/**
 * 物料订单表 Created by Jed on 2017/12/17.
 */
@Data
@Table("tbl_order_material")
public class Order_Material extends BaseModel {

	private static final long serialVersionUID = 1L;

	public Order_Material() {}
	
	@Id(auto = false)
	@Column("order_no")
	@Comment("订单号")
	@ColDefine(type = ColType.INT)
	private Integer orderNo;

	@Column
	@Comment("物料编码")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	@NonNull
	private String code;

	@Column
	@Comment("物料描述")
	@ColDefine(type = ColType.VARCHAR, width = 255)
	private String name;

	@Column
	@Comment("物料数量")
	@NonNull
	@ColDefine(type = ColType.INT)
	private Integer count;

}
