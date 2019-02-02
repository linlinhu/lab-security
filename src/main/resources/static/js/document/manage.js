var DocumentManage = function(p){
	p = p ? p : {};
	let _this = this,
		mels= '',
		moduleCode = p.moduleCode,
		moduleId = '#' + moduleCode + '-manage',
		treeTableSelecter = $(moduleId + ' .table-box'),
		securityTree = null,//treeTable
		documentTable = '',
		cateEdit = p.cateEdit,//是否编辑分类，默认false
		cateAdd = p.cateAdd,//是否添加分类，默认false
		folderEdit = p.folderEdit,//是否编辑文件夹，默认false
		hasCheckbox = p.hasCheckbox||false,//是否有多选框，默认false
		domainIsSelected = p.domainIsSelected, //true查询公有，false查询私有，不传查询全部
		ecmId = p.ecmId,
		selectedCateId = null,
		init = function(){
			getCates(function(){
				getMel(function(){
					documentTable = new DocumentTableManage({
						treeTableSelecter: treeTableSelecter,
						mel: mels[0].items,
						hasCheckbox: hasCheckbox,
						moduleCode: moduleCode,
						rightMenu: folderEdit ? rightMenu: null,
					})
					setSelectedCate();
				});
			});
			eventInit();
		},
		
		//获取mel模板
		getMel = function(callback) {
			$http.get({
				data:{
					serviceId:'lab-security-web',
					code: 'document'
				},
				url:"document/get-mel"
			},function(res){
				if(!res.success) {
					layer.msg(res.message,{icon:5});
					return false;
				}
				mels = res.result.groups;
				if(typeof callback == 'function') {
					callback(res.result);
				}
			})
		},
		//新建分类
		createCate = function(p,callback){
			$http.post({
				data: p,
				url: "document/createDomainRoot"
			},function(res){
				if(typeof callback == 'function') {
					callback(res);
				}
			})
		},
		//获取分类
		getCates = function(callback){
			let cates=[];
			
			$http.get({
				data:{
					domainIsSelected: domainIsSelected
				},
				url:"document/queryRoots"
			},function(res){
				cates = res.result;
				if(!cateEdit) {
					cates.push({
						id:2,
						nodeDomain: null,
						name: '安全责任人体系'
					})
				}
				_this.cates = cates;
				renderCates();
				if(typeof callback == "function"){
					callback();
				}
			})
			
		},
		//渲染分类
		renderCates = function(){
			let data =_this.cates ? _this.cates: [],
				html = '';
			data.forEach(function(value){
				html += ' <li class="cate-item" data-id="'+value.id+'" data-nodeDomain="'+value.nodeDomain +'">'+
               	    		'<a data-toggle="tab" href="#" aria-expanded="false">'+value.name+'</a>'+
               	    	'</li>'
			})
			$(moduleId + ' .cate-area .nav-tabs').html(html);
		},
		//设置选中的分类
		setSelectedCate = function(){
			if(_this.selectedCateId) {
				console.log(2)
				$(moduleId + ' .cate-item[data-id="'+_this.selectedCateId+'"] a').trigger('click');
			} else {
				console.log(2)
				if(_this.cates && _this.cates.length > 0) {
					$($(moduleId + ' .cate-item')[0]).find('a').trigger('click');
				}
			}
		},
		//获取已经选中的分类
		getSelectedCate = function(callback){
			let selectedCate = $(moduleId + ' .cate-item.active'),
				data = selectedCate.data();
			data.name = selectedCate.text();
			if(typeof callback == 'function') {
				callback(data)
			}
		},
		openCatePanel = function(data){
			data = data ? data : {};
			let catePanelOpen,
				renderData = data.renderData ? data.renderData : {};
			setTimeout(function(){
				catePanelOpen = layer.open({
					type : 1,
					title : data.title ? data.title : '添加分类',
					shadeClose: false,
					closeBtn: 1, 
					anim: 2,
					skin : 'layui-layer-rim', //加上边框
					area : [ '400px', '140px' ], //宽高
					content : '<div class="'+ moduleCode + '-cate-create-or-edit-open"></div>'
				});
				let view = $('.' + moduleCode + '-cate-create-or-edit-open'),
					tpl = cate_panel_tpl.innerHTML;
				laytpl(tpl).render(renderData, function(html){
					view.html(html);
					view.find('button').unbind().on('click',function(){
						let oper = $(this).attr('oper');
						if(oper == 'save') {
							name = view.find('input[name="name"]').val(),
							rootModifyDto = {};
									
							name = name.trim();
							if(name == '') {
								layer.msg('分类名称不能为空',{icon:5});
								view.find('input[name="name"]').focus();
								return false;
							}
							if(name.length > 20) {
								layer.msg('分类名称的长度不能超过20个字符',{icon:5});
								view.find('input[name="name"]').focus();
								return false;
							};
							
							rootModifyDto.name = name;
							if(data.type == 'cate') {
								if(data.id) {
									rootModifyDto.id = data.id;
									saveFolder({dirDto: JSON.stringify(rootModifyDto),isRoot: true},function(res){
										layer.close(catePanelOpen);
										layer.msg('保存成功',{icon:6});
										_this.selectedCateId = res.id;
										getCates(function(){
											setSelectedCate();
										});
									})
								} else {
									createCate({rootModifyDto: JSON.stringify(rootModifyDto)},function(res){
										layer.close(catePanelOpen);
										layer.msg('保存成功',{icon:6});
										_this.selectedCateId = res.id;
										getCates(function(){
											setSelectedCate();
										});
									})
								}
								
							} else {
								if(data.id) {
									rootModifyDto.id = data.id;
								}
								if(data.pid) {
									rootModifyDto.pid = data.pid;
								}
								
								rootModifyDto.nodeDomain = data.nodeDomain;
								
								saveFolder({dirDto: JSON.stringify(rootModifyDto),isRoot: false},function(res){
									layer.close(catePanelOpen);
									layer.msg('保存成功',{icon:6});
									documentTable.getList({pid:res.pid,nodeDomain: res.nodeDomain});
								})
							}
							
							
						} else {
							layer.close(catePanelOpen)
						}
						
					})
				})
				
			})
		},
		saveFolder = function(p,callback){
			$http.post({
				data: p,
				url: "document/folderCreateOrUpdate"
			},function(res){	
				if(typeof callback == 'function') {
					callback(res);
				}
			})
		},
		remove = function(data,confirmMsg,callback) {//删除文件或者目录
			$http.post({
				data: data,
				url: "document/delete",
				confirmMsg:(confirmMsg ? confirmMsg : '确认删除文件？')
			},function(res){
				if(typeof callback == 'function') {
					callback(res);
				}
			})
		},
		move = function(data,callback){
			$http.post({
				data: data.data,
				url: "document/move",
				confirmMsg:(data.confirmMsg||'确认移动？')
			},function(res){
				if(typeof callback == 'function') {
					callback(res);
				}
			})
		}
		rightMenu = function(p){//右键菜单
			let menu = p.menu ? p.menu : $(moduleId + ' .right-menu'),
				id = p.id,
				confirmMsg = "确认删除？"
				html = null,
				e = p.e,
				pTop = p.top ? p.top : 0,
				pLeft = p.left ? p.left : 0;
			
			e.preventDefault();
			if(p.type == "folder") {
				html = 	'<li role="file-upload">上传</li>' +
						'<li role="edit">重命名</li>' +
						'<li role="move">移动</li>' +
						'<li role="remove">删除</li>';
				confirmMsg = "目录中的内容也会被删除,确认删除？"
			} else if(p.type == "cate") {
				html = 	'<li role="edit">重命名</li>'+
						'<li role="remove">删除</li>';
				confirmMsg = "该分类下的所有文件都会被删除,确认删除？"
			} else {
				html = '<li role="move">移动</li>'+
						'<li role="remove">删除</li>';
			}
			menu.html(html);
			/* if(p.type == "folder") {
				if(operationCodes.indexOf('edit-folder') == -1) { // 目录重命名权限
					menu.find('li[role="edit"]').remove();
				};
				if(operationCodes.indexOf('remove-folder') == -1) { // 目录删除权限
					menu.find('li[role="remove"]').remove();
				};
			} else if(p.type == 'file') {
				if(operationCodes.indexOf('move-file') == -1) { // 文件移动权限
					menu.find('li[role="move"]').remove();
				};
				if(operationCodes.indexOf('remove-file') == -1) { // 文件删除权限
					menu.find('li[role="remove"]').remove();
				};
			} */
			
		
				//根据事件对象中鼠标点击的位置，进行定位
				menu.css('left', e.clientX - pLeft + 'px');
				menu.css('top', e.clientY - pTop + 'px');
				menu.css('width', '100px');
				menu.removeClass('hide');
				lockContextMenu = true;
				document.onclick=function(){  
					menu.addClass('hide');
					lockContextMenu = false;
				};  
				
				menu.find('li').unbind('click').on('click', function(event) {
					let role = $(this).attr('role');
					// 移动
					if (role === 'move') {
						let tempEl  = $(moduleId + ' tr[data-tt-id="'+id+'"]'),
							fileNames = tempEl.find('.filename').text(),
							changeRouteFileIds = {allIds:[id],allNames:[fileNames]};
						
						if(p.type == 'folder') {
							changeRouteFileIds.folderIds = [id]
						}
						openChosenFolderPanel({renderData: _this.cates,btnName:'确定',changeRouteFileIds:changeRouteFileIds},function(res){
							let mvPid = res.id,
								nodeDomain = res.nodeDomain;
							move({
								data:{
									ids:changeRouteFileIds.allIds.join(','),
									mvPid:mvPid,
								},
								confirmMsg: '确认将选中项移动到【' + res.name + '】？'
							},function(res){
								layer.msg('移动成功',{icon: 6});
								setTimeout(function(){
									getSelectedCate(function(res){
										if(res.nodedomain != nodeDomain) {
											
											$(moduleId + ' .nav-tabs .cate-item').removeClass('active');
											$(moduleId + ' .nav-tabs .cate-item[data-nodedomain="'+ nodeDomain +'"]').addClass('active');
										} 
										documentTable.getList({pid:mvPid,nodeDomain:nodeDomain});
									})
									
								},100)
								
							})
						})
					}
					// 移除
					if (role === 'remove') {
						
						if(p.type == 'cate') {//删除分类
							remove({ids:p.id}, confirmMsg, function(){
								getCates(function(){
									if(p.id == _this.selectedCateId) {
										_this.selectedCateId = null;
									}
									setSelectedCate();
								});
								
							})
						} else {//删除文件
							let pid = $(moduleId + ' tr[data-tt-id="' + p.id + '"]').attr('data-tt-parent-id');
							remove({ids:p.id},confirmMsg,function(res){
								$(moduleId + ' ul.path li').last().trigger('click');
							})
						}
					}
					
					// 编辑
					if (role === 'edit') {
						if(p.type == 'cate') {//编辑分类
							openCatePanel({id:p.id,title:'编辑分类',type:'cate',renderData:{name:p.name}})
						} else {//编辑目录
							/*editFolder({el:p.el});*/
							getSelectedCate(function(res){
								openCatePanel({title:'编辑文件夹',type:'folder',id:p.id,nodeDomain:res.nodedomain,renderData:{name:p.name}});
							})
							
						}
					}
					//上传file-upload
					if (role === 'file-upload') {
						getSelectedCate(function(res){
							documendUpload({id:p.id,nodeDomain:res.nodedomain,name:p.name});
						})
					}
				});
			
		},
		documendUpload = function(data){
			let fileUpload = new EminAontherFileUpload(),
				pid = data.id;
			setTimeout(function() {
				fileUpload.init({
					title: '上传本地文件',
					forbidConfirm: true,
					confirmMsg: '您确定要将选中文件上传至<strong> ' + data.name + ' </strong>吗',
					fileNumLimit: 1,
					fileSingleSizeLimit: 10,
					uploadUrl: 'file/universalUpload.do',
					filesType: ['all'],
					data:{pid:pid,ecmId:ecmId}
				}, function(res) {
					if(res.success){
						let params = res.result.storage[0],
						viewFileType = res.result.viewFileType;
						params.name = $('#uploadInterface .filelist .title').html();
						params.fileType = res.result.originalMimeType;
						params.contentType = res.result.originalContentType;
						params.viewFileType = res.result.viewFileType;
						params.storePath = params.fileStorageUrl;
						params.storeHost = params.destinationStorageHost;
						params.fileStoreId = params.fileId;
						params.pid = pid;
						delete params.fileStorageUrl;
						delete params.destinationStorageHost;
						delete params.fileId;
						if(!(viewFileType && viewFileType != '')) {
							viewFileType = 'fa-file-o';
						}
						$http.get({
							data:{fileDto:JSON.stringify(params)},
							url:"document/fileCreateOrUpdate"
						},function(res){
							if(res.success) {
								layer.closeAll();
								layer.msg('上传文件成功', {icon: 6});
								documentTable.getList({pid:res.result.pid,nodeDomain: res.result.nodeDomain});
							} else {
								layer.msg(res.message?res.message:'上传文件失败', {icon: 5});
							}
						})
						
					} else {
						layer.msg(res.message?res.message:'上传文件失败', {icon: 5});
					};
				});
			},100)
		},
		openChosenFolderPanel = function(data,callback) {
			data = data ? data : {};
			
			let	btnName = data.btnName ? data.btnName : '下一步',
				renderData = data.renderData ? data.renderData : [],
				changeRouteFileIds = data.changeRouteFileIds,
				chosenFolderPanelOpen,
				className = moduleCode + '-folders-open';
			setTimeout(function(){
				chosenFolderPanelOpen = layer.open({
					type: 1,
					title: '选择目录',
					shadeClose: false,
					closeBtn: 1, 
					anim: 2,
					skin : 'layui-layer-rim', //加上边框
					area : [ '500px', '400px' ], //宽高
					btn: [btnName,'取消'],
					content : '<div class="' + className + '">'+$('.document-change-panel').html()+'</div>',
					yes: function(){
						let selectedEl = $('.' + className + ' .selected'),
							id = selectedEl.parent().attr('data-id'),
							name = selectedEl.find('span').text(),
							nodeDomain = selectedEl.attr('data-nodeDomain');
						if(!nodeDomain) {
							nodeDomain = selectedEl.parents('li[data-nodeDomain]').attr('data-nodeDomain')
						}
						
						if(id) {
							setTimeout(function(){
								layer.close(chosenFolderPanelOpen);
							},100);
							$http.post({
								data: {targetDirId: id, nodeNames:changeRouteFileIds.allNames.join(',')},
								url: "document/existNodeFirLvlName"
							},function(res){
								if(res) {
									layer.msg('文件夹或者文件名称重复',{icon:5})
								} else {
									callback({id:id,name:name,nodeDomain:nodeDomain});
								}
							})
							
						} else {
							layer.msg('请选择目录',{icon:5})
						}
					}
				});
				renderfolders({d:renderData,el:$('.'+ className+ ' .below .folders'),changeRouteFileIds: changeRouteFileIds});
				$('.opration-info-folders-open .item').unbind().on('click',function(){
					$('.opration-info-folders-open .item').removeClass('selected');
					$(this).addClass('selected');
				})
				
			})
		},
		renderfolders = function(p,callback){
			let tempView = p.el,
				tpl = document_change_folder_tpl.innerHTML,
				changeRouteFileIds = p.changeRouteFileIds;
			laytpl(tpl).render(p.d, function(html){
				tempView.html(html);
				let panelEL = $('.'+moduleCode + '-folders-open');
				panelEL.find('.folders').unbind().on('click','.item',function(){
					let self = $(this),
						tempId = self.parent().attr('data-id');
					if(!self.hasClass('selected')) {
						panelEL.find('.folders .item').removeClass('selected');
						self.addClass('selected');
					};
					if(self.hasClass('open')) {
						self.next('ul').addClass('hide');
						self.removeClass('open');
						self.addClass('retracted');
						self.find('i').removeClass('fa-folder-open');
						self.find('i').addClass('fa-folder');
					} else {
						let nodeDomain = self.parents('li[data-nodeDomain]').attr('data-nodeDomain');
						getChildren({pid:tempId,nodeDomain:nodeDomain},function(res){
							duplicateRemoval({data:res,el:self});
						});
					}
				});
				
			});
			function getChildren(data,callback){
				data = data ? data : {};
				data.pid = data.pid ? data.pid : rootId;
				data.limit = 1000;
				data.nodeType = 10;
				
				$http.get({
					data: data,
					url: "document/getPage"
				},function(res){
					if(typeof callback == 'function') {
						callback(res.result.resultList)
					}
				})
			};
			function duplicateRemoval(data) {//去重
				let d = data.data,
					el = data.el,
					folderIds = changeRouteFileIds.folderIds,
					len = d.length,
					isSame = "false",
					tempList = [];
				for(let i = 0; i < len; i++) {
					isSame = "false";
					for(let j = 0; j < folderIds.length; j++) {
						if(folderIds[j] == d[i].id) {
							isSame = "true";
						}
					}
					if(isSame == "false") {
						tempList.push(d[i]);
					}
				}
				if(tempList.length > 0) {
					renderfolders({el: el.next('ul'),d:tempList,changeRouteFileIds: changeRouteFileIds});
					el.next('ul').removeClass('hide');
					el.removeClass('retracted');
					el.addClass('open');
					el.find('i').removeClass('fa-folder');
					el.find('i').addClass('fa-folder-open');
				}
			};
		},
		eventInit = function(){
			//切换分类
			$(moduleId + ' .nav-tabs').unbind().on('click','.cate-item',function(){
				let self = $(this),
					pid = self.attr('data-id'),
					nodeDomain = self.attr('data-nodeDomain');
				_this.selectedCateId = pid;
				$(moduleId + ' .dr-search-form input[name="keyword"]').val('');
				if(!self.hasClass('selected')) {
					$(moduleId + ' .nav-tabs .cate-item').removeClass('selected');
					self.addClass('selected');
					
					if(nodeDomain != 'null') {
						$(moduleId + ' input[name="keyword"]').attr('placeholder','搜索文档名');
						$(moduleId + ' .path').removeClass('hide');
						documentTable.init({
							rootId: pid,
							nodeDomain: nodeDomain,
						})
					} else {
						$(moduleId + ' input[name="keyword"]').attr('placeholder','搜索责任人');
						$(moduleId + ' .path').addClass('hide');
						if(securityTree == null) {
							securityTree = new SecurityTree({
								treeTableSelecter: treeTableSelecter,
								mel: mels[2].items,
								hasCheckbox: hasCheckbox,
								moduleId: moduleId,
								rightMenu: folderEdit ? rightMenu: null
							});
							documentTable.securityTree = securityTree;
						}
						securityTree.init({
							tableHeaderTpl:user_tree_table_header_tpl.innerHTML,
							tableBodyTpl:user_tree_table_body_tpl.innerHTML,
							tableUrl: '/security-system/get-securityTree',
							params:{
								universityId: 21
							}
						});
					}
				}
			});
			//添加分类
			if(cateAdd) {
				$(moduleId + ' .cate-area .add-cate').unbind().on('click',function(){//添加分类
					openCatePanel({title:'添加分类',type:'cate'});
				});
			}
			//分类的右键菜单
			if(cateEdit) {
				$(moduleId + ' .cates').unbind('contextmenu').on('contextmenu','.cate-item',function(e){
					let self = $(this),
						id = self.attr('data-id'),
						nodeDomain = self.attr('data-nodeDomain');
					if(!(nodeDomain == 2 || nodeDomain == 3 || nodeDomain == 4)){
						e.preventDefault();
						rightMenu({id:id,nodeDomain:nodeDomain,type:'cate',e:e,name:self.text()});
					}
				});
			}
			//新建文件夹
			$(moduleId + ' a[oper="folder-create"]').unbind().on('click',function(){
				let pid = $(moduleId + ' ul.path li').last().attr('data-id');
			
				getSelectedCate(function(res){
					openCatePanel({title:'新建文件夹',type:'folder',pid:pid,nodeDomain:res.nodedomain});
				})
			});
			
			///文件上传
			$(moduleId + ' a[oper="file-upload"]').unbind().on('click',function(){
				let currentFolder = $(moduleId + ' ul.path li').last(),
				pid = currentFolder.attr('data-id'),
				name = currentFolder.text();
			
				getSelectedCate(function(res){
					documendUpload({id:pid,nodeDomain:res.nodedomain,name:name});
				})
			});
			//批量处理
			$(moduleId + ' .batch-oper a').unbind().on('click',function(){
				let oper = $(this).attr('oper'),
					selectedData = documentTable.getSelect();
				
				if(selectedData.allIds == 0) {
					layer.msg('未选中任何文件或文件夹',{icon:5});
					return false;
				}
				if(oper == 'move') {
					openChosenFolderPanel({renderData: _this.cates,btnName:'确定',changeRouteFileIds:selectedData},function(res){
						let mvPid = res.id,
							nodeDomain = res.nodeDomain;
						move({
							data:{
								ids:selectedData.allIds.join(','),
								mvPid:mvPid,
							},
							confirmMsg: '确认将选中项移动到【' + res.name + '】？'
						},function(res){
							layer.msg('移动成功',{icon: 6});
							setTimeout(function(){
								getSelectedCate(function(res){
									if(res.nodedomain != nodeDomain) {
										$(moduleId + ' .nav-tabs .cate-item').removeClass('active');
										$(moduleId + ' .nav-tabs .cate-item[data-nodedomain="'+ nodeDomain +'"]').addClass('active');
									} 
									documentTable.getList({pid:mvPid,nodeDomain:nodeDomain});
								})
								
							},100)
							
						})
					})
				} else {
					remove({ids:selectedData.allIds.join(',')},'确认删除选中项?',function(res){
						$(moduleId + ' ul.path li').last().trigger('click');
					})
				}
			})
			
		};
	return init();	
}