package com.hision.erp.service.impl;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.hision.erp.bean.Material;
import com.hision.erp.bean.Shelf;
import com.hision.erp.bean.Shelf_InOutHis;
import com.hision.erp.bean.Shelf_Material;
import com.hision.erp.service.ShelfService;
import com.hision.erp.util.Globals;

@IocBean
public class ShelfServiceImpl implements ShelfService {

	@Inject
	protected Dao dao;

	@Override
	public void importShelf(String code, int count, String name, String shelfCode, String shelfName) {
		// 1. 判断库存是否充足
		// 2. 更新或新增记录 => 映射表,历史表
		// 3. 更新库存
		// 4. 写历史表
		
		Trans.exec(new Atom() {
			@Override
			public void run() {
				Material material = dao.fetch(Material.class, Cnd.where("code", "=", code));
				int total = material.getTotal();

				// 查看该货架是否有当前物料
				Shelf_Material model = dao.fetchx(Shelf_Material.class, shelfCode, code);
				if (model == null) {
					// 将新物料导入货架
					model = new Shelf_Material();
					model.setMaterialCode(code);
					model.setShelfId(shelfCode);
					model.setStoreCount(count);
					dao.insert(model);
				} else {
					// 更新原有物料在货架上的数量
					int newCount = model.getStoreCount() + count;
					model.setStoreCount(newCount);
					dao.update(model);
				}

				// 更新库存 = 原始库存 - 入库数量
				int newTotal = total - count;
				material.setTotal(newTotal);
				dao.update(material);

				// 写历史表: 时间、动作、货架、物料 一并记录下来
				Shelf_InOutHis hisBean = new Shelf_InOutHis();
				hisBean.setShelfId(shelfCode);
				// hisBean.setShelfCode(shelfCode); 拿不到
				hisBean.setMaterialCode(code);
				hisBean.setType(Globals.IN_SHELF);
				hisBean.setCurCount(count);
				dao.insert(hisBean);

			}
		});

	}

	@Override
	public void exportShelf(String code, int count, String name, String shelfCode, String shelfName) {
		// 1. 判断库存是否充足
		// 2. 更新或新增记录 => 映射表,历史表
		// 4. 写历史表
		
		Trans.exec(new Atom() {
			@Override
			public void run() {
				// 更新货架物料数量
				Shelf_Material model = dao.fetchx(Shelf_Material.class, shelfCode, code);
				int newTotal = model.getStoreCount() - count;
				model.setStoreCount(newTotal);
				dao.update(model);
				// 写历史表
				Shelf_InOutHis hisBean = new Shelf_InOutHis();
				hisBean.setShelfId(shelfCode);
				// hisBean.setShelfCode(shelfCode); 拿不到
				hisBean.setMaterialCode(code);
				hisBean.setType(Globals.OUT_SHELF);
				hisBean.setCurCount(count);
				dao.insert(hisBean);
			}
		});
	}

	@Override
	public boolean isCountEnoughForImport(String code, int count) {
		Material material = dao.fetch(Material.class, Cnd.where("code", "=", code));
		int total = material.getTotal();
		if (count > total) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isCountEnoughForExport(String code, String shelfCode, int count) {

		Shelf_Material model = dao.fetchx(Shelf_Material.class, shelfCode, code);
		int shelfTotal = model.getStoreCount();
		if (count > shelfTotal) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canDelete(String shelfId) {
		List<Shelf_Material> pairList = dao.query(Shelf_Material.class, Cnd.where("shelfId", "=", shelfId));
		int count = 0;
		for (Shelf_Material pair : pairList) {
			count += pair.getStoreCount();
		}
		return count == 0;
	}

	@Override
	public void deleteLinks(String shelfId) {
		Trans.exec(new Atom() {
			@Override
			public void run() {
				dao.delete(Shelf.class, shelfId);
				// 删除货架
				List<Shelf_Material> pairList = dao.query(Shelf_Material.class, Cnd.where("shelfId", "=", shelfId));
				// 删除货架关联的空物料
				for (Shelf_Material pair : pairList) {
					dao.deletex(Shelf_Material.class, shelfId, pair.getMaterialCode());
				}
			}
		});
	}

}
