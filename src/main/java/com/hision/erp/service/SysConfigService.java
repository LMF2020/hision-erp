package com.hision.erp.service;

import java.util.List;

import com.hision.erp.bean.Shelf;
import com.hision.erp.viewmodel.RowCol;

public interface SysConfigService {

	/**
	 * 更新桌面配置 row|col
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public RowCol updateDeskConfig(RowCol value) throws Exception;

	/**
	 * 获取桌面配置 row|col
	 * 
	 * @return
	 */
	public RowCol getDeskConfig();

	/**
	 * 获取桌面最新数据
	 * 
	 * @return
	 */
	public List<List<Shelf>> getDeskWidgetList();
	
	/**
	 * 判断货位是否已满
	 * 
	 * @return
	 */
	public boolean isReachLimit();
}
