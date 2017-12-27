package com.hision.erp.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.random.R;

public abstract class BaseModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column
	@Comment("操作时间")
	@Prev(els = @EL("$me.now()"))
	@ColDefine(type = ColType.INT)
	private Integer opAt;

	public String toString() {
		return String.format("/*%s*/%s", super.toString(), Json.toJson(this, JsonFormat.compact()));
	}

	public Integer now() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	public Integer getOpAt() {
		return opAt;
	}

	public void setOpAt(Integer opAt) {
		this.opAt = opAt;
	}

	public String uuid() {
		return R.UU32().toLowerCase();
	}
}
