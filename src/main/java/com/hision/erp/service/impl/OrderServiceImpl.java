package com.hision.erp.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.hision.erp.bean.Material;
import com.hision.erp.bean.Order_Material;
import com.hision.erp.service.OrderService;
import com.hision.erp.util.ExcelUtils;
import com.hision.erp.util.Globals;

@IocBean
public class OrderServiceImpl implements OrderService {

	private static final Log LG = Logs.get();

	@Inject
	protected Dao dao;

	@Override
	public boolean isOrderExist(Order_Material order) {
		int orderNo = order.getOrderNo();
		Order_Material bean = dao.fetch(Order_Material.class, Cnd.where("orderNo", "=", orderNo));
		return bean != null;
	}

	@Override
	public void addOrder(Order_Material order) {
		// 订单号唯一, 不能重复新增
		Trans.exec(new Atom() {
			@Override
			public void run() {
				// 先新增订单
				dao.insert(order);
				// 判断物料是否存在
				Material material = dao.fetch(Material.class, Cnd.where("code", "=", order.getCode()));
				if (material == null) { // 新增物料
					material = new Material();
					material.setCode(order.getCode());
					material.setName(order.getName());
					material.setTotal(order.getCount());
					dao.insert(material);
				} else {
					// 增加库存量
					int count = material.getTotal() + order.getCount();
					material.setTotal(count);
					dao.update(material);
				}
			}

		});

	}

	@Override
	public Map<String, Integer> batchImportOrders(InputStream inStream) throws Exception {
		Workbook workBook;

		int totalCount = 0;
		int succCount = 0;
		int failCount = 0;
		int igCount = 0;

		try {
			workBook = WorkbookFactory.create(inStream);
		} catch (Exception e) {
			LG.error("load excel file error", e);
			throw new Exception("表格文件错误");
		}
		Sheet sheet = workBook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0 || row.getRowNum() == 1) {
				// 跳过标题
				continue;
			}
			Order_Material order = new Order_Material();
			boolean oneColIsNull = false;
			try {
				for (int index = 0; index < 4; index++) {
					Cell cell = row.getCell(index);
					Object cellValue = ExcelUtils.getCellValue(cell);
					if (cellValue == null) {
						oneColIsNull = true;
						break;
					}
					if (index == 0) { // 订单号
						BigDecimal vbd = new BigDecimal(cellValue.toString());
						order.setOrderNo(vbd.intValue());
					}
					if (index == 1) { // 物料编号
						order.setCode(cellValue.toString());
					}
					if (index == 2) { // 物料描述
						order.setName(cellValue.toString());
					}
					if (index == 3) { // 数量
						BigDecimal vbd = new BigDecimal(cellValue.toString());
						order.setCount(vbd.intValue());
					}
				}
			} catch (Exception e) {
				LG.warn("Ignore excel row " + row.getRowNum() + " where one cell is error!");
				totalCount++;
				failCount++;
				continue;
			}

			// 验证: 整行有一列为空，就跳过
			if (oneColIsNull) {
				LG.warn("Ignore excel row " + row.getRowNum() + " where one cell is null!");
				totalCount++;
				igCount++;
				continue;
			}

			// 判断订单是否存在
			int count = dao.count(Order_Material.class, Cnd.where("orderNo", "=", order.getOrderNo()));
			if (count == 1) {
				LG.warn("Ignore excel row " + row.getRowNum() + " where order: " + order.getOrderNo() + " exists!");
				totalCount++;
				igCount++;
				continue;
			}

			// 插入订单
			try {
				addOrder(order);
			} catch (Exception e) {
				LG.warn("Ignore excel row " + row.getRowNum() + " where order: " + order.getOrderNo() + " has error!");
				totalCount++;
				failCount++;
			}

			totalCount++;
			succCount++;

		}

		Map<String, Integer> ret = new HashMap<String, Integer>();
		ret.put(Globals.EXCEL_TOTAL_COUNT, totalCount);
		ret.put(Globals.EXCEL_SUCC_COUNT, succCount);
		ret.put(Globals.EXCEL_FAIL_COUNT, failCount);
		ret.put(Globals.EXCEL_IGNORE_COUNT, igCount);

		return ret;
	}
}
