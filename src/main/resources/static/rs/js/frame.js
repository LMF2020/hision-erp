layui.config({
	base: '/rs/js/'
}).extend({
	// 加載自定義模塊
	CommJS: 'common'
})

layui.use(['jquery', 'layer', 'laytpl', 'form', 'table', 'upload', 'element', 'CommJS'], function() {

	var $ = layui.$,
		layer = layui.layer,
		form = layui.form,
		table = layui.table,
		element = layui.element,
		laytpl = layui.laytpl,
		upload = layui.upload,
		CommJS = layui.CommJS;



	// 根据物料查询所在货架
	$('#searchShelfViaMa').click(function(e){
		e.preventDefault();
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_searchShelfViaMa, // 模板路径
			title: false,
			shadeClose: true, //点击遮罩关闭
			closeBtn: 0, // 不显示右上角关闭按钮
			skin: 'layer-unvisiable', //自定义class
			area: ['550px', '80px'],
			success: function(layero, index) {
				form.on('submit(searchShelfViaMa)', function(data) {
					$('#searchShelfViaMa .lay-submit').addClass('layui-disabled')
					$.ajax({
							method: "GET",
							url: CommJS.CTX_PATH + "/material/searchShelfViaMa/" + data.field.code
						})
						.done(function(resp) {
							$('#searchShelfViaMa .lay-submit').removeClass('layui-disabled')
							if(resp.code == 1) {
								CommJS.error(resp.msg);
							} else {
								// 查询成功后后高亮货架
								console.log('查询成功',resp)
								if(resp.data.length == 0){
									// TODO: 没有判断物料是否存在
									CommJS.success('该物料不存在或者未上架');
								}else{
									showSelectedShelf(resp.data);
								}
								
							}
						});

					return false;
				});
			}
		}
		CommJS.readform(conf);
		
	})
	
	// 批量导入
	$('#batchImportMaterial').click(function(e) {
		e.preventDefault();
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_batchImportMaterial, // 模板路径
			title: '物料批量导入',
			area: ['550px', '450px'],
			success: function(layero, index) {
				// 开始上传
				var uploadInst = upload.render({
					elem: '#batchUpload', //绑定元素
					accept: 'file', // 支持所有文件
					exts: 'xls|xlsx', // 支持的格式
					auto: false, // 不自动上传需要bindAction
					bindAction: '#confirmBtn', //配合auto使用
					field: 'fileData', // 文件域的字段名与后端接口定义保持一致
					url: CommJS.CTX_PATH + '/material/upload/', //上传接口
					done: function(resp) {
						console.log('上传完毕!', resp);
						//上传完毕回调
						if(resp.code == 1) {
							CommJS.error(resp.msg);
						} else {
							CommJS.success(resp.msg);
						}
					},
					error: function() {
						//请求异常回调
						CommJS.error("网络或其他原因上传失败!");
					}
				});
			}

		}
		CommJS.readform(conf);
	})
	// 增加物料
	$('#addMaterial').click(function(e) {
		e.preventDefault();
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_addMaterial, // 模板路径
			title: '物料手工錄入',
			area: ['550px', '450px'],
			success: function(layero, index) {
				//console.log(layero, index);
				form.on('submit(formAddMaterial)', function(data) {
					console.log('form-data', data.field) //当前容器的全部表单字段，名值对形式：{name: value}
					$('#formAddMaterial .lay-submit').addClass('layui-disabled')
					$.ajax({
							method: "POST",
							url: CommJS.CTX_PATH + "/material/add",
							data: data.field
						})
						.done(function(resp) {
							$('#formAddMaterial .lay-submit').removeClass('layui-disabled')
							if(resp.code == 1) {
								CommJS.error(resp.msg);
							} else {
								console.log("新增物料成功: ", resp);
								CommJS.success('新增物料成功');
							}
						});

					return false;
				});
			}

		}
		CommJS.readform(conf);
	})

	// 增加貨位
	$('#addShelf').click(function(e) {
		e.preventDefault();
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_addShelf, // 模板路径
			title: '新增貨架',
			area: ['550px', '300px'],
			success: function(layero, index) {

				form.on('submit(formAddShelf)', function(data) {
					console.log('form-request', data.field)
					$('#formAddShelf .lay-submit').addClass('layui-disabled')
					$.ajax({
							method: "POST",
							url: CommJS.CTX_PATH + "/shelf/add",
							data: data.field
						})
						.done(function(resp) {
							$('#formAddShelf .lay-submit').removeClass('layui-disabled')
							if(resp.code == 1) {
								CommJS.error(resp.msg);
							} else {
								console.log("新增货架成功: ", resp);
								// 更新桌面
								inner_updateDesktop({
									rowList: resp.data
								});
								CommJS.success('新增货架成功');
							}
						});

					return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
				});
			}
		}
		CommJS.readform(conf);

	})

	// 設置窗口
	$('#settings').click(function(e) {
		e.preventDefault();
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_settings, // 模板路径
			title: '基本設置',
			area: ['550px', '300px'],
			success: function(layero, index) {
				// 读取配置
				$('#formSettings .lay-submit').addClass('layui-disabled')
				$.ajax({
						method: "GET",
						url: CommJS.CTX_PATH + "/settings/get"
					})
					.done(function(resp) {
						// 激活提交按钮
						$('#formSettings .lay-submit').removeClass('layui-disabled')
						if(resp.code == 1) {
							CommJS.error(resp.msg);
						} else {
							console.log("获取桌面配置: ", resp);
							// 更新表单
							$('#formSettings input[name="row"]').val(resp.data.row);
							$('#formSettings input[name="col"]').val(resp.data.col);
						}
					});
				// 表单提交
				form.on('submit(formSettings)', function(data) {
					// console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
					// console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
					console.log('form-data', data.field) //当前容器的全部表单字段，名值对形式：{name: value}

					$.ajax({
							method: "POST",
							url: CommJS.CTX_PATH + "/settings/desk",
							data: data.field
						})
						.done(function(resp) {
							if(resp.code == 1) {
								CommJS.error(resp.msg);
							} else {
								console.log("桌面数据更新: ", resp);
								// 更新桌面
								inner_updateDesktop({
									rowList: resp.data
								});
								CommJS.success('桌面数据已更新');
							}
						});

					return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
				});

			}
		}
		CommJS.readform(conf);
	})

	// 綁定事件
	$('#clearShelvesData').click(function(e) {
		e.preventDefault();
		alert('清空貨架');
	})

	$('#clearMaterialData').click(function(e) {
		e.preventDefault();
		alert('清空物料');
	})

	// 更新桌面
	function inner_updateDesktop(data) {
		var getTpl = desktpl.innerHTML,
			deskview = document.getElementById('desk');
		laytpl(getTpl).render(data, function(html) {
			deskview.innerHTML = html;
		});
	}

	/**
	 * 查看货架信息
	 */
	$(document).on('click', '.op-search', function(e) {
		e.preventDefault();
		var shelfCode = $(this).parent().attr('data-id');
		var shelfName = $(this).parent().attr('data-name');
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_showShelf, // 模板路径
			title: '正在查看物料列表, 当前货架：' + shelfName,
			area: ['800px', '600px'],
			success: function(layero, index) {
				$('#showShelf em').html('当前货架：' + shelfName + ' (编号:' + shelfCode + ')')
				// 初始化表格
				table.render({
					elem: '#showShelfGrid',
					height: 420,
					url: CommJS.CTX_PATH + '/shelf/' + shelfCode + '/ma/list' //数据接口
						//,page: true //开启分页
						,
					cols: [
						[ //表头
							{
								field: 'code',
								title: '物料编码',
								sort: true,
								fixed: 'left'
							}, {
								field: 'name',
								title: '物料名称'
							}, {
								field: 'totalOnShelf',
								title: '货架库存',
								sort: true
							}
						]
					]
				});
			}
		}

		CommJS.readform(conf);
	})

	/**
	 * 货架导入物料
	 */
	$(document).on('click', '.op-import', function(e) {
		e.preventDefault();
		var shelfCode = $(this).parent().attr('data-id');
		var shelfName = $(this).parent().attr('data-name');
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_importShelf, // 模板路径
			title: '物料上架, 当前货架：' + shelfName,
			area: ['1200px', '600px'],
			success: function(layero, index) {
				// 货架编码 + // 货架名称
				$('#importShelfForm input[name="shelfCode"]').val(shelfCode);
				$('#importShelfForm input[name="shelfName"]').val(shelfName);

				// 初始化表格
				table.render({
					elem: '#importShelfGrid',
					height: 420,
					url: CommJS.CTX_PATH + '/material/list' //数据接口
						//,page: true //开启分页
						,
					cols: [
						[ //表头
							{
								field: 'code',
								title: '物料编码',
								sort: true,
								fixed: 'left'
							}, {
								field: 'name',
								title: '物料名称'
							}, {
								field: 'total',
								title: '物料库存',
								sort: true
							}, {
								fixed: 'right',
								align: 'center',
								toolbar: '#selectGridRow'
							} //这里的toolbar值是模板元素的选择器
						]
					]
				});

				// 选取表格行，同时给表单赋值
				table.on('tool(importShelfGrid)', function(obj) {
					var data = obj.data; //获得当前行数据
					console.log(data)
					$('#importShelfForm input[name="code"]').val(data.code);
					$('#importShelfForm textarea[name="name"]').val(data.name);
				});

				// 表单导入提交接口
				form.on('submit(importShelfForm)', function(data) {
					console.log('form-request', data.field)
					$('#importShelfForm .lay-submit').addClass('layui-disabled')
					$.ajax({
							method: "POST",
							url: CommJS.CTX_PATH + "/shelf/import",
							data: data.field
						})
						.done(function(resp) {
							$('#importShelfForm .lay-submit').removeClass('layui-disabled')
							if(resp.code == 1) {
								CommJS.error(resp.msg);
							} else {
								// 表格重载
								table.reload('importShelfGrid');
								console.log("物料导入成功: ", resp);
								CommJS.success('物料导入成功:' + data.field.code + "," + data.field.count + '(件)');
							}
						});

					return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
				});
			}
		}
		CommJS.readform(conf);

	})

	/**
	 * 货架导出物料
	 */
	$(document).on('click', '.op-export', function(e) {
		e.preventDefault();
		var shelfCode = $(this).parent().attr('data-id');
		var shelfName = $(this).parent().attr('data-name');
		var conf = {
			htmUrl: CommJS.HTM_TPL.htm_exportShelf, // 模板路径
			title: '物料下架, 当前货架：' + shelfName,
			area: ['1200px', '600px'],
			success: function(layero, index) {
				// 货架编码 + // 货架名称
				$('#exportShelfForm input[name="shelfCode"]').val(shelfCode);
				$('#exportShelfForm input[name="shelfName"]').val(shelfName);

				// 初始化表格
				table.render({
					elem: '#exportShelfGrid',
					height: 420,
					url: CommJS.CTX_PATH + '/shelf/' + shelfCode + '/ma/list' //数据接口
						//,page: true //开启分页
						,
					cols: [
						[ //表头
							{
								field: 'code',
								title: '物料编码',
								sort: true,
								fixed: 'left'
							}, {
								field: 'name',
								title: '物料名称'
							}, {
								field: 'totalOnShelf',
								title: '货架库存',
								sort: true
							}, {
								fixed: 'right',
								align: 'center',
								toolbar: '#selectGridRow'
							} //这里的toolbar值是模板元素的选择器
						]
					]
				});

				// 选取表格行，同时给表单赋值
				table.on('tool(exportShelfGrid)', function(obj) {
					var data = obj.data; //获得当前行数据
					console.log(data)
					$('#exportShelfForm input[name="code"]').val(data.code);
					$('#exportShelfForm textarea[name="name"]').val(data.name);
				});

				// 表单导入提交接口
				form.on('submit(exportShelfForm)', function(data) {
					console.log('form-request', data.field)
					$('#exportShelfForm .lay-submit').addClass('layui-disabled')
					$.ajax({
							method: "POST",
							url: CommJS.CTX_PATH + "/shelf/export",
							data: data.field
						})
						.done(function(resp) {
							$('#exportShelfForm .lay-submit').removeClass('layui-disabled')
							if(resp.code == 1) {
								CommJS.error(resp.msg);
							} else {
								// 表格重载
								table.reload('exportShelfGrid');
								console.log("物料导出成功: ", resp);
								CommJS.success('物料从货架导出成功:' + data.field.code + "," + data.field.count + '(件)');
							}
						});

					return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
				});
			}
		}
		CommJS.readform(conf);

	})

		// 删除货架
	$(document).on('click', '.imgDiv .delete', function(e) {
		e.preventDefault();
		var shelfId = $(this).attr('data-id');
		var shelfName = $(this).attr('data-name');

		layer.confirm('您确定删除货架-' + shelfName + '吗？', {
			btn: ['确定', '取消'] //按钮
		}, function() {
			$.ajax({
					method: "GET",
					url: CommJS.CTX_PATH + "/shelf/delete/" + shelfId
				})
				.done(function(resp) {
					if(resp.code == 1) {
						CommJS.error(resp.msg);
					} else {
						console.log("货架删除成功: ", resp);
						// 更新桌面
						inner_updateDesktop({
							rowList: resp.data
						});
						CommJS.success('货架删除成功');
					}
				});

		}, function() {
			// 取消操作
		});
	})
	
	// 重置查询后 (被选中的货架)
	$('#clearShelfSelected').click(function(e){
		e.preventDefault();
		clearSelectedShelf()
	})
	
	/**
	 * 选中货架
	 */
	function showSelectedShelf(shelfCodes) {
		clearSelectedShelf()
		if(shelfCodes) {
			for(var i = 0; i < shelfCodes.length; i++) {
				$('#' + shelfCodes[i]).addClass('select-me');
			}
		}
	}

	/**
	 * 清除选中货架
	 */
	function clearSelectedShelf() {
		$('#desk li').removeClass('select-me');
	}

	// 初始化桌面

	// 调试模式启用
	//	$.ajax({
	//		dataType: "json",
	//		url: 'test/desk.json',
	//		data: "",
	//		success: function(data) {
	//			inner_updateDesktop(data);
	//		}
	//	});
	// 服务端请求
	$.ajax({
			method: "GET",
			url: CommJS.CTX_PATH + "/settings/init"
		})
		.done(function(resp) {
			if(resp.code == 1) {
				CommJS.error(resp.msg);
			} else {
				console.log("桌面数据更新: ", resp);
				// 更新桌面
				inner_updateDesktop({
					rowList: resp.data
				});
				CommJS.alert('桌面数据更新成功<br>欢迎访问海天物料管理系统!<br>窗口15s后自动关闭');
			}
		});

});