package com.hision.erp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.boot.NbApp;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import com.hision.erp.bean.Material;
import com.hision.erp.bean.Order_Material;
import com.hision.erp.bean.Shelf;
import com.hision.erp.bean.Shelf_Material;
import com.hision.erp.bean.Sys_config;
import com.hision.erp.service.OrderService;
import com.hision.erp.service.ShelfService;
import com.hision.erp.service.SysConfigService;
import com.hision.erp.util.Globals;
import com.hision.erp.util.Result;
import com.hision.erp.viewmodel.RowCol;
import com.hision.erp.viewmodel.Shelf_Material_Summary;
import com.hision.erp.viewmodel.Widgets;

/**
 * Launcher Created by Jed on 2017/12/17.
 *
 */
@IocBean(create = "init", depose = "depose")
public class MainLauncher {

	private static final Log log = Logs.get();

	@Inject
	protected PropertiesProxy conf;

	@Inject
	protected Dao dao;

	@Inject
	private SysConfigService sysConfigService;
	@Inject
	private OrderService orderService;
	@Inject
	private ShelfService shelfService;

	@At("/")
	@Ok("beetl:/index.html")
	public void index() {
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/settings/get")
	@Ok("json")
	public Result getRowCol() {
		try {
			RowCol bean = sysConfigService.getDeskConfig();
			return Result.success("获取配置成功", bean);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/settings/init")
	@Ok("json")
	public Result initDesk() {
		try {
			List<List<Shelf>> widgetList = sysConfigService.getDeskWidgetList();
			if (widgetList.isEmpty()) {
				return Result.error("仓库还没有货架, 赶紧来新增一个吧^_^");
			}
			return Result.success("桌面初始化成功", widgetList);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/material/add")
	@Ok("json")
	public Result addMaterial(@Param("..") Order_Material order) {
		try {
			if (orderService.isOrderExist(order)) {
				return Result.error("订单已经存在");
			}
			orderService.addOrder(order);
			return Result.success();
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/material/list")
	@Ok("json")
	public Result listMaterial() {
		try {
			List<Material> beanList = dao.query(Material.class, Cnd.NEW());
			return Result.success("", beanList);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/material/searchShelfViaMa/?")
	@GET
	@Ok("json")
	public Result searchShelfViaMa(String code) {
		try {
			List<Shelf_Material> matchList = dao.query(Shelf_Material.class, Cnd.where("materialCode", "=", code));
			List<String> shelfCodeList = new ArrayList<>();
			for (Shelf_Material obj : matchList) {
				shelfCodeList.add(obj.getShelfId());
			}
			return Result.success("", shelfCodeList);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/shelf/add")
	@Ok("json")
	public Result addShelf(@Param("..") Shelf shelf) {
		try {
			// 判断货位是否已满
			boolean noSeats = sysConfigService.isReachLimit();
			if (noSeats) {
				return Result.error("货位已满，不能新增");
			}
			// 新增
			dao.insert(shelf);
			// 获取桌面数据
			List<List<Shelf>> widgetList = sysConfigService.getDeskWidgetList();
			return Result.success("货位增加成功", widgetList);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}
	
	@Filters(@By(type = CrossOriginFilter.class))
	@At("/shelf/delete/?")
	@Ok("json")
	public Result delShelf(String shelfId) {
		try {
			// 判断货架上是否有物料
			boolean canDelete = shelfService.canDelete(shelfId);
			if (!canDelete) {
				return Result.error("货架上有物料，不能删除");
			}
			// 删除货架
			shelfService.deleteLinks(shelfId);
			// 获取桌面数据
			List<List<Shelf>> widgetList = sysConfigService.getDeskWidgetList();
			return Result.success("货架删除成功", widgetList);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/shelf/?/ma/list")
	@Ok("json")
	public Result showShelfMaterialList(String shelfCode) {
		try {

			String str = "SELECT  s.shelf_id as shelfCode, s.material_code as code, s.store_count as totalOnShelf, m.name  FROM $shelf_material_table s,"
					+ " $material_table m  WHERE s.material_code = m.code AND shelf_id = @shelfCode ";
			Sql sql = Sqls.create(str);
			sql.vars().set("shelf_material_table", dao.getEntity(Shelf_Material.class).getTableName());
			sql.vars().set("material_table", dao.getEntity(Material.class).getTableName());
			sql.params().set("shelfCode", shelfCode);
			sql.setCallback(Sqls.callback.entities());
			sql.setEntity(dao.getEntity(Shelf_Material_Summary.class));
			dao.execute(sql); // 注意, execute是没有返回值的, 所需要的值都通过callback来获取
			List<Shelf_Material_Summary> dataList = sql.getList(Shelf_Material_Summary.class);
			return Result.success("货架列表查询成功", dataList);

		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/shelf/import")
	@Ok("json")
	public Result importShelf(@Param("code") String code, @Param("count") int count, @Param("name") String name,
			@Param("shelfCode") String shelfCode, @Param("shelfName") String shelfName) {
		try {
			boolean enough = shelfService.isCountEnoughForImport(code, count);
			if (!enough) {
				return Result.error("该物料库存不足：" + code);
			}
			shelfService.importShelf(code, count, name, shelfCode, shelfName);
			return Result.success("物料导入货架成功");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/shelf/export")
	@Ok("json")
	public Result exportShelf(@Param("code") String code, @Param("count") int count, @Param("name") String name,
			@Param("shelfCode") String shelfCode, @Param("shelfName") String shelfName) {
		try {
			boolean enough = shelfService.isCountEnoughForExport(code, shelfCode, count);
			if (!enough) {
				return Result.error("该货架上物料库存不足：" + code);
			}
			shelfService.exportShelf(code, count, name, shelfCode, shelfName);
			return Result.success("物料导出货架成功");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@Filters(@By(type = CrossOriginFilter.class))
	@At("/settings/desk")
	@Ok("json")
	public Result setDeskScale(@Param("row") int row, @Param("col") int col) {
		try {
			sysConfigService.updateDeskConfig(new RowCol(row, col));
			int _count = dao.count(Shelf.class);
			if (_count == 0) {
				return Result.success("没有查询到货架信息", Widgets.EMPTY());
			} else {
				List<List<Shelf>> widgetList = sysConfigService.getDeskWidgetList();
				// 开始组装返回列表
				return Result.success("返回成功", widgetList);
			}

		} catch (Exception e) {
			return Result.error(e.getMessage());
		}

	}

	@Filters(@By(type = CrossOriginFilter.class))
	@AdaptBy(type = UploadAdaptor.class, args = { "ioc:fileUpload" })
	@POST
	@At("/material/upload")
	@Ok("json")
	public Object uploadfile(@Param("fileData") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
		try {
			if (err != null && err.getAdaptorErr() != null) {
				return Result.error(err.getAdaptorErr().getMessage());
			} else if (tf == null) {
				return Result.error("文件内容不能为空");
			} else {
				Map<String, Integer> info = orderService.batchImportOrders(tf.getInputStream());

				int succCount = info.get(Globals.EXCEL_SUCC_COUNT);
				int totalCount = info.get(Globals.EXCEL_TOTAL_COUNT);
				int failCount = info.get(Globals.EXCEL_FAIL_COUNT);
				int ignoreCount = info.get(Globals.EXCEL_IGNORE_COUNT);

				return Result.success("订单导入完毕! 总共:" + totalCount + "条,成功:" + succCount + "条,失败:" + failCount + "条,忽略:"
						+ ignoreCount + "条");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		} catch (Throwable e) {
			return Result.error(e.getMessage());
		}
	}

	public void init() {
		// 环境检查
		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}
		Daos.createTablesInPackage(dao, "com.hision.erp.bean", false);
		Daos.migration(dao, "com.hision.erp.bean", true, false);
		int count = dao.count(Sys_config.class, Cnd.where("configKey", "=", Globals.SETTINGS));
		if (count == 0) {
			RowCol rowCol = new RowCol(4, 10);
			String json = Json.toJson(rowCol, JsonFormat.compact());
			Sys_config config = new Sys_config();
			config.setConfigKey(Globals.SETTINGS);
			config.setConfigValue(json);
			dao.insert(config);
		}
	}

	public void depose() {
	}

	public static void main(String[] args) throws Exception {
		new NbApp(MainLauncher.class).run();
	}

}
