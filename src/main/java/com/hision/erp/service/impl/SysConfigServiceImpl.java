package com.hision.erp.service.impl;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import com.google.common.collect.Lists;
import com.hision.erp.bean.Shelf;
import com.hision.erp.bean.Sys_config;
import com.hision.erp.service.SysConfigService;
import com.hision.erp.util.Globals;
import com.hision.erp.viewmodel.RowCol;

@IocBean
public class SysConfigServiceImpl implements SysConfigService {

	@Inject
	protected Dao dao;

	@Override
	public RowCol updateDeskConfig(RowCol value) throws Exception {
		String json = Json.toJson(value, JsonFormat.compact());
		Sys_config config = new Sys_config();
		config.setConfigKey(Globals.SETTINGS);
		config.setConfigValue(json);
		int status = dao.update(config);
		if (status == Globals.SUCC) {
			return value;
		}
		throw new Exception("更新桌面配置失败");
	}

	@Override
	public RowCol getDeskConfig() {
		Sys_config sysConf = dao.fetch(Sys_config.class, Cnd.where("configKey", "=", Globals.SETTINGS));
		if (sysConf != null) {
			RowCol value = Json.fromJson(RowCol.class, sysConf.getConfigValue());
			return value;
		}
		return null;
	}

	@Override
	public List<List<Shelf>> getDeskWidgetList() {
		List<Shelf> shelfList = dao.query(Shelf.class, Cnd.orderBy().asc("ordering"));
		RowCol rowCol = getDeskConfig();
		int _cols = rowCol.getCol();
		int _count = shelfList.size();
		// 计算行数
		int _rows = 1; // 小于等于一行肯定是1.
		if (_count > _cols) { // 因此只需要判断大于一行的情况
			_rows = (_count % _cols == 0) ? (_count / _cols) : (_count / _cols + 1);
		}
		if(shelfList.isEmpty()) {
			return Lists.newArrayList();
		}
		return Globals.partition(shelfList, _rows);
	}

	@Override
	public boolean isReachLimit() {
		int count = dao.count(Shelf.class);
		RowCol rowCol = getDeskConfig();
		int maxSeats = rowCol.getCol() * rowCol.getRow();
		if (count >= maxSeats) {
			return true;
		}
		return false;
	}

}
