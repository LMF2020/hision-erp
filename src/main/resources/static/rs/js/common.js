layui.define(['jquery'], function(exports) {

	var $ = layui.$,
		layer = layui.layer;

	// 前端页面路径
	var HTM_PATH = '/rs/form/';

	var CommJS = {

		// 后端服务路径
		CTX_PATH: '',

		// 前端模板管理
		HTM_TPL: {
			htm_settings: HTM_PATH + 'settings.html',
			htm_addShelf: HTM_PATH + 'addShelf.html',
			htm_addMaterial: HTM_PATH + 'addMaterial.html',
			htm_batchImportMaterial: HTM_PATH + 'batchImportMaterial.html',
			htm_importShelf: HTM_PATH + 'importShelf.html',
			htm_exportShelf: HTM_PATH + 'exportShelf.html',
			htm_showShelf: HTM_PATH + 'showShelf.html',
			htm_searchShelfViaMa: HTM_PATH + 'searchShelfViaMa.html'
		},

		// 读取表单模板
		readform: function(conf) {
			if(!conf || !conf.htmUrl) {
				layer.alert('配置为空或缺少模板路径', {
					icon: 2
				});
				return
			}
			$.get(conf.htmUrl, function(tplHtm) {
				var newConf = $.extend(true, {
					type: 1,
					area: ['550px', '400px'], // 默认窗口尺寸
					content: tplHtm //注意，如果str是object，那么需要字符拼接。
				}, conf);
				layer.open(newConf);
			});
		},

		error: function(msg) {
			layer.alert(msg, {
				icon: 2
			});
		},

		success: function(msg) {
			msg = msg || '操作成功';
			layer.confirm(msg + ',您是否需要關閉當前窗口？', {
				time: 20000, //20s后自动关闭
				btn: ['关闭', '继续'] //按钮
			}, function(index) {
				layer.closeAll('dialog'); //关闭信息框
				layer.closeAll('page'); //关闭所有页面层
			}, function() {

			});
		},

		alert: function(msg) {
			msg = msg || 'Hi, there!';
			layer.alert(msg, {
				time: 15000, //10秒关闭（如果不配置，默认是3秒）
				skin: 'layui-layer-lan', //样式类名
				closeBtn: 0,
				icon: 6,
				anim: 4 //0-6的动画形式，-1不开启
			});
		}

	}

	exports('CommJS', CommJS)

})