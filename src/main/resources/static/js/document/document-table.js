var DocumentTableManage = function(options){
	
	if(!(options.treeTableSelecter && $(options.treeTableSelecter).length == 1)){
		layer.msg('treeTable容器有且只能有一个')
		return false;
	}
	if(!options.moduleCode){
		layer.msg('请传入moduleCode');
		return false;
	}
	let path = [],
		moduleCode = options.moduleCode,
		operationCodes = options.operationCodes||'',
		moduleId = '#' + moduleCode + '-manage',
		treeTableSelecter = options.treeTableSelecter,//treeTable的容器
		mel = options.mel ? options.mel : [],//mel模版
		hasCheckbox = options.hasCheckbox, //是否有多选框
		rightMenu = options.rightMenu, //文件及文件夹的右键菜单方法，是一个function
		tableHeaderTpl, //表和头部的tpl
		tableBodyTpl,
		tableUrl,//table数据请求地址
		rootId,//根节点的id
		rootNodeDomain,
		tableId,
		_this = this;
		_this.securityTree = options.securityTree;
	let init = function(data){
		rootId = data.rootId;
		rootNodeDomain = data.nodeDomain;
		tableHeaderTpl = data.tableHeaderTpl ||tree_table_header_tpl.innerHTML;
		tableBodyTpl = data.tableBodyTpl || tree_table_body_tpl.innerHTML;
		tableUrl = data.tableUrl||"document/getPage";
		if(data.mel) {
			mel = mel;
		}
		renderHeader(function(){
			getList({pid:rootId,nodeDomain:rootNodeDomain});
			syncDownAviable();
			tableEventInit();
		})
		
		
	},
	renderHeader = function(callback) {
		let tpl = tableHeaderTpl||tree_table_header_tpl.innerHTML,
			view = treeTableSelecter,
			renderDate = mel;
		laytpl(tpl).render(renderDate, function(html){
			var nowDate = new Date();
			view.html(html);
			tableId = 'tree-table' + nowDate.getTime();
			view.find('table').attr('id',tableId);
			view.find('.table-page').attr('id',tableId + '-page')
			tableId = '#' + tableId;
			if(typeof callback == 'function') {
				callback()
			}
		})
	},
	getList = function(p) {
		p = p ? p : {};
		p.limit = p.limit||10;
		p.page = p.page ||1;
		if(!p.nodeDomain) {
			p.nodeDomain = rootNodeDomain;
		}
		$http.get({
			data: p,
			url: tableUrl||"document/getPage"
		},function(res){
			if(res.success) {
				_this.params = p;
				res.result.name = res.name;
				res.result.order = res.order;
				path = res.path;
				pathText({name:res.name,path:path});
				renderTbody(res.result);
			}
		})
		
		function renderTbody(data,el) {
			data = data ? data : {};
			let tpl = tableBodyTpl,
				view = $(tableId).find('tbody'),
				renderData = {
					name: data.name ? data.name: null,
					resultList: data.resultList ? data.resultList : [],
					order: data.order ? data.order: null
				};
			renderData = {
				data: data.resultList ? data.resultList : [],
				mel: mel,
				hasCheckbox: hasCheckbox
			};
			
			laytpl(tpl).render(renderData, function(html){
				view.html(html);
				$(moduleId + ' .batch-oper').addClass('hide');
				if(data.totalCount && data.totalCount > 0) {
					$('.i-checks').iCheck({
					    checkboxClass: 'icheckbox_square-green',
					    radioClass: 'iradio_square-green',
					});
					$(tableId + '-page').show();
					$(moduleId + ' .batch-oper').removeClass('hide');
					laypage.render({
					    elem: $(tableId + '-page').attr('id'),
					    curr: data.currentPage,
					    count: data.totalCount,
					    layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
						theme: '#55A8FD',
					    jump: function(obj, first){
						    if(!first){
						    	let p = _this.params;
						    		p = obj.curr;
						    	if(obj.limit != p.limit) {
						    		p.limit = obj.limit
						    	}
						    	getList(p);
						    };
					    }
					});
					
					//选中及不选中
					CommonUtil.itemsCheck({
						allSelector: moduleId + ' input[name="selected-all"]',
						itemSelector: moduleId + ' input[name="file-item"]'
					});
					
					
				} else {
					$(tableId + '-page').hide();
				}
			});
		};	
	}, 
	getSelect = function(){
		let allIds = [],
			allNames = [],
			folderIds = [];
		$(moduleId + ' td input.nodeType-50:checked').each(function() {
			parents = $(this).parents('tr');
			allIds.push($(this).val());
			allNames.push(parents.find('.filename').text());
		});
		$(moduleId + ' td input.nodeType-10:checked').each(function() {
			parents = $(this).parents('tr');
			allIds.push($(this).val());
			folderIds.push($(this).val());
			allNames.push(parents.find('.filename').text());
		});
		return {
			allIds: allIds,
			folderIds: folderIds,
			allNames: allNames
		};
	},
	pathText = function(p){
		let data = p ? p : {},
			tempIndex = null,
			tpl = document_path_tpl.innerHTML,
			view = $(moduleId + ' ul.path');
		
		laytpl(tpl).render(data, function(html){
			view.html(html);
			$(moduleId + ' ul.path li').unbind().on('click',function(){//路径改变
				let id = $(this).attr('data-id');
				pid  = id ? id : rootId;
				search({pid: pid});
			});
		})
	},
	documendUpload = function(data){
		let fileUpload = new EminAontherFileUpload();
		setTimeout(function() {
			fileUpload.init({
				title: '上传本地文件',
				confirmMsg: '您确定要将选中文件上传至<strong> ' + data.name + ' </strong>吗',
				fileNumLimit: 1,
				fileSingleSizeLimit: 10,
				uploadUrl: '/file/universalUpload.do',
				filesType: ['all'],
				data:{id:data.id}
			}, function(res) {
				if(res.success){
					let params = res.result.storage[0];
					params.name = $('#uploadInterface .filelist .title').html();
					params.fileType = res.result.originalMimeType;
					params.contentType = res.result.originalContentType;
					params.pid = $('#document-searchform input[name="pid"]').val();
					params.storePath = params.fileStorageUrl;
					params.storeHost = params.destinationStorageHost;
					params.fileStoreId = params.fileId;
					delete params.fileStorageUrl;
					delete params.destinationStorageHost;
					delete params.fileId;
					CommonUtil.operation({
						moduleName: 'document',
						oper: 'fileCreateOrUpdate',
						params: {fileDto:JSON.stringify(params)},
						forbidConfirm: true
					}, function(res) {
						if(res.success) {
							layer.closeAll();
							layer.msg('上传文件成功', {icon: 6});
							$('#document-searchform input[name="name"]').val($(moduleId).attr('data-name'));
							search();
						} else {
							layer.msg(res.message?res.message:'上传文件失败', {icon: 5});
						}
					});
				} else {
					layer.msg(res.message?res.message:'上传文件失败', {icon: 5});
				};
				layer.closeAll();
			});
		},300)
	},
	search = function(data){
		data = data ? data : {};
		let params = $(moduleId + ' .document-searchform').serializeObject();
		params.limit = data.limit ? data.limit : 20;
		params.nodeDomain = rootNodeDomain;
		params.pid = data.pid || $(moduleId + ' ul.path li').last().attr('data-id');
		getList(params);	
	},
	//在打包下载之前进行验证
	checkBeforePackage = function(data,callback) {
		CommonUtil.operation({
			moduleName: 'file',
			oper: 'document/'+data.dirId+'/check',
			type: 'get',
			params: data.params,
			forbidConfirm: true,
			forbidLoading: false,
		}, function(res) {
			if(res.success) {
				if(typeof callback == 'function') {
					callback()
				}
			} else {
				layer.msg(res.message,{icon:5})
			}
			
		})
	},
	package_download = function(data) {
		
		checkBeforePackage({dirId: data.dirId},function(res){
			let url = base  + 'file/document/' + data.dirId + '/package?packageFileName=' + data.packageFileName;
			if(data.available <= _this.syncDownAviable) {
				document.getElementById("ifile").src = url;
			} else {
				let syncDownAviableM = (_this.syncDownAviable/1024/1024).toFixed(2);
				layer.confirm('该压缩包为超大文件(大于' + syncDownAviableM + 'M)，是否确认下载？', {
					btn: ['确认'] //按钮
				}, function(){
					document.getElementById("ifile").src = url;
				});
			}
		})
	},
	syncDownAviable = function() {
		CommonUtil.operation({
			moduleName: 'file',
			oper: 'document/syncDownAviable',
			type: 'get',
			params: {},
			forbidConfirm: true
		}, function(res) {
			_this.syncDownAviable = res.result;
		});
	},
	selectdInit = function(data){
		data = data ? data : [];
		data.forEach(function(value){
			$(moduleId + ' input[value="'+ value +'"]').iCheck('check');
		});
	},
	tableEventInit = function(){
		$(tableId).unbind('contextmenu');
		$(tableId).on('contextmenu','span.box',function(e) {//文件、目录的右键菜单
			let self = $(this).find('span.filename'),
				parent = self.parents('tr'),
				id = parent.attr('data-tt-id'),
				type = 'file',
				name = self.text();
			if($(this).hasClass('folder')) {
				type = 'folder';
			}
			if(rightMenu) {
				rightMenu({id:id,type:type,e:e,el:self,name: name});
			}
			
		});
		//排序
		$(tableId).unbind('click');
		$(tableId).on('click', '.footable-sort-indicator', function(){
			let self = $(this),
				order = null;
			if(self.hasClass('sort-asc')) {
				self.addClass('sort-desc');
				self.removeClass('sort-asc');
				search({order:'desc'})
			} else {
				self.removeClass('sort-desc');
				self.addClass('sort-asc');
				search({order:'asc'})
			}
		});
		//进入子文件夹
		$(tableId).on('click', '.box.folder',function(){
			let self = $(this),
				parent = self.parents('tr'),
				id = parent.attr('data-tt-id');
			getList({pid:id,nodeDomain:rootNodeDomain});
		});
		//下载文件 
		$(tableId).on('click', '.file-download',function(){
			let self = $(this),
				parent = self.parents('tr'),
				fileName = parent.find('.filename').html(),
				id = parent.attr('data-tt-id'),
				available = parent.find('td[data-available]').attr('data-available'),
				nodeType = self.attr('data-nodeType'),
				url = base  + "file/download?url=" + self.attr('data-url') + '&fileName=' + fileName;
			
			if(nodeType == 10) {
				package_download({dirId: id,available: available,packageFileName: fileName});
			} else {
				document.getElementById("ifile").src = url;
			}
		});
		//文件预览
		$(tableId).on('click', ".preview",function(){
			let self = $(this),
				parent = self.parents('tr'),
				fileName = parent.find('.filename').text(),
				pathUrl = parent.find('.file-download').attr('data-url'),
				filetype = self.attr('data-filetype'),
				tempList = ['gif','jpg','jpeg','png','pdf'],
				isTempType = false;
			
			
			for(let i = 0; i < tempList.length; i++) {
				if(tempList[i] == filetype) {
					isTempType = true;
				}
			}
				
			if(isTempType) {
				openPanel();
			} else {
				$http.get({
					data: {
						viewChannel: 'yozosoft',
						filePath:pathUrl,
						previewFileName: fileName
					},
					url: "document/supportPreview"
				},function(res){	
					
					if(res.result == true) {
						$http.post({
							data: {
								viewChannel: 'yozosoft',
								filePath:pathUrl,
								previewFileName: fileName
							},
							url: "document/preview"
						},function(res){	
							
							pathUrl = res;
							openPanel();
						})
					} else {
						layer.msg('不支持预览',{icon:5});
					}
				})
			}
			function openPanel() {
				layer.open({
					type : 1,
					title :'【' + fileName + '】 在线预览',
					shadeClose: false,
					closeBtn: 1, 
					anim: 2,
					skin : 'layui-layer-rim', //加上边框
					area : [ '800px', '600px' ], //宽高
					btn: ['全屏'],
					content : '<div class="filer-filepreview-open" style="background:#F3F5F5">' + "<iframe src='" + pathUrl + "' width='100%' height='485px' frameborder='0'>This is an embedded <a target='_blank' href='http://office.com'>Microsoft Office</a> document, powered by <a target='_blank' href='http://office.com/webapps'>Office Online</a>.</iframe>" + '</div>',
					yes: function(oneOpenIndex){
						let oneOpen = $('#layui-layer' + oneOpenIndex);
							H = window.innerHeight - 120;
						oneOpen.addClass('hide');
						let twoOpen = layer.open({
							type : 1,
							title :'【' + fileName + '】 在线预览',
							shadeClose: false,
							closeBtn: 1, 
							anim: 2,
							skin : 'layui-layer-rim', //加上边框
							btn: ['还原'],
							area : [ '100%', '100%' ], //宽高
							content : '<div class="filer-filepreview-open" style="background:#F3F5F5">' + "<iframe src='" + pathUrl + "' width='100%' height='" + H + "px' frameborder='0'>This is an embedded <a target='_blank' href='http://office.com'>Microsoft Office</a> document, powered by <a target='_blank' href='http://office.com/webapps'>Office Online</a>.</iframe>" + '</div>',
							yes: function(fullSIndex){
								oneOpen.removeClass('hide');
								layer.close(fullSIndex)
							}
						});
						$('#layui-layer' + twoOpen + ' .layui-layer-close').on('click',function(){
							layer.close(oneOpenIndex)
						});
					}
				});
			}
		
		});
		//搜索
		$(moduleId + ' .dr-search-form button[role="submit"]').unbind().on('click',function(){
			let keyword = $(moduleId + ' .dr-search-form input[name="keyword"]').val();
			if(keyword && $.trim(keyword).length > 0 ) {
				if($(moduleId + ' .cates .selected').attr('data-nodedomain') == "null") {
					_this.securityTree.search()
				} else {
					search();
				}
				
			} else {
				layer.msg('搜索字段不能为空',{icon: 5});
			}
		});
		$(moduleId + ' .dr-search-form input[name="keyword"]').unbind('keyup').keyup('.form-control',function(){
			let keyword = $(moduleId + ' .dr-search-form input[name="keyword"]').val();
			if(keyword && $.trim(keyword).length > 0 ) {
				$(moduleId + ' .dr-search-form .reset').removeClass('hide');
			} else {
				$(moduleId + ' .dr-search-form .reset').addClass('hide');
			}
		})
		//搜索框内的回车事件
		$(moduleId + ' .dr-search-form').unbind('keydown').keydown('.form-control',function(){
			if(event.keyCode==13){
				$('#document-searchform button[role="submit"]').trigger('click');
				return false;
		    }
		});
		//重置
		$(moduleId + ' .dr-search-form .reset').on('click',function(){
			$(moduleId + ' .dr-search-form input[name="page"]').val(1);
			$(moduleId + ' .dr-search-form input[name="keyword"]').val('');
			$(this).addClass('hide');
			if($(moduleId + ' .cates .selected').attr('data-nodedomain') == "null") {
				_this.securityTree.search()
			} else {
				search();
			}
		});
		//单选框动作时，关闭右键菜单
		$(moduleId + ' tr ins').on('click',function(){
			$('#document-right-memu').addClass('hide');
		});
	};
	this.init = init;
	this.getList = getList;
	this.getSelect = getSelect;
};