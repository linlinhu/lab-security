
var	SecurityTree = function(options){
	options = options ? options : {};
	if(!(options.treeTableSelecter && $(options.treeTableSelecter).length == 1)){
		layer.msg('treeTable容器有且只能有一个')
		return false;
	}
	if(!options.moduleId){
		layer.msg('请传入moduleId');
		return false;
	}
	var treeTableSelecter = options.treeTableSelecter,//treeTable的容器
		mel = options.mel ? options.mel : [],//mel模版
		moduleId = options.moduleId,//模块id，带“#”
		hasCheckbox = options.hasCheckbox, //是否有多选框
		rightMenu = options.rightMenu, //文件及文件夹的右键菜单方法，是一个function
		tableHeaderTpl, //表和头部的tpl
		tableBodyTpl,
		tableUrl,//table数据请求地址
		_this = this;
	var renderHeader = function(callback) {
		let tpl = tableHeaderTpl||tree_table_header_tpl.innerHTML,
			view = treeTableSelecter,
			renderDate = mel;
		laytpl(tpl).render(renderDate, function(html){
			view.html(html);
			if(typeof callback == 'function') {
				callback()
			}
		})
	},
	renderTable = function(p,nodeId) {
		p = p ? p : {};
		let tpl = tableBodyTpl||tree_table_body_tpl.innerHTML,
			view = treeTableSelecter.find('tbody'),
			renderDate = p.data;
		renderDate = renderDate ? renderDate : [];
		laytpl(tpl).render({data:renderDate,mel:mel,hasCheckbox:hasCheckbox}, function(html){
			view.html(html);
			let nowDate = new Date();
				tableId = 'tree-table' + nowDate.getTime();
			
			treeTableSelecter.find('table').attr('id',tableId);
			if(renderDate.length > 0) {
				$('#' + tableId).removeClass('treetable');
				treeTableInit($('#' + tableId),function(res){
					/*if(nodeId && nodeId != '') {
						openNode($('#' + tableId),[nodeId])
					} else {
						let roots = res.roots ? res.roots :[];
						_this.roots = roots;
						roots.forEach(function(value,index) {
							if(index < 2) {
								let tempId = value.id
								openNode($('#' + tableId),[tempId]);
							}
						})
					}*/
				});
			}
		})
	},
	treeTableInit = function(el,callback){//树形表格初始化
		let tableId = el.selector;
		el.treetable({  
			expandable: true,
			onInitialized: function(){//树初始化完毕的回调函数
				if(typeof callback == 'function') {
					treeRoots = this.roots;
					callback(this)
				}
			},
			onNodeInitialized: function(){//节点始化完毕的回调函数
			},
			onNodeCollapse: function(){//节点收起时的回调,
				
			},
		    onNodeExpand: function () {//节点展开时的回调
		       
		    }
        });	
	},
	// 查询
	getList = function(p,callback) {
		p = p ? p : {};
		_this.params = p;
		$http.get({
			data: p,
			url: tableUrl||"document/getPage"
		},function(res){
			if(res.success) {
				if(typeof callback == 'function') {
					callback(res.result);
				}
			} else {
				layer.msg('获取数据失败',{icon: 5});
				treeTableSelecter.find('tbody').html('<tr><td colspan="5" style="text-align:center;">获取数据失败</td></tr>');
			}
		})
	},
	search = function(){
		_this.params.keyword= $(moduleId + ' input[name="keyword"]').val();
		getList(_this.params,function(res){
    		renderTable({data:res})
    	})
	},
    init = function(p){
		p = p || {};
		tableHeaderTpl = p.tableHeaderTpl;
		tableBodyTpl = p.tableBodyTpl;
		tableUrl = p.tableUrl;
		if(p.mel) {
			mel = p.mel;
		}
    	renderHeader();
    	getList(p.params,function(res){
    		renderTable({data:res})
    	})
    };
    this.getList = getList;
    this.renderTable = renderTable;
    this.init = init;
    this.search = search;
};