package com.hision.erp.service;

import java.io.InputStream;
import java.util.Map;

import com.hision.erp.bean.Order_Material;

public interface OrderService {

	/**
	 * 订单是否存在
	 * 
	 * @return
	 */
	public boolean isOrderExist(Order_Material order);

	/**
	 * 新增订单 保证事务：
	 * 
	 * 订单表和物料表必须同时更新，订单表只记录订单，物料表根据编码进行物料数量的加法计算
	 * 
	 * @param order
	 */
	public void addOrder(Order_Material order);

	/**
	 * 批量导入订单
	 * 
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Integer> batchImportOrders(InputStream inStream) throws Exception;
}
