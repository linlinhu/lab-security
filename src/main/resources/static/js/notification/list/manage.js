var NotificationList = (function(p){
	let moduleId = null,
		searchFormSelect = null,
		tableParams = null,
		tableManage = null,
		operationCodes = null;
	let init = function(p){
		if(p.wrapId) {
			moduleId = p.wrapId;
		}
		if(!moduleId){
			return false;
		}
		tableParams = p;
		searchFormSelect = $(moduleId + ' .filter-line');
		operationCodes = p.operationCodes;
		eventInit();
		if(p.type) {
			$(moduleId + ' .cates .cate-item[data-type="' + p.type + '"] a').trigger('click');
		} else {
			$(moduleId + ' .cates .cate-item[data-type="1"] a').trigger('click');
		}
	},

	renderSearchForm = function(p,callback){
		p.type = p.type || 1;
		$http.get({
			data:p,
			url:"notification-list/search-form"
		},function(res){
			searchFormSelect.html(res);
			if(p.type == 1 && operationCodes.indexOf('notification-pub-check') == -1){//检查通知发布权限
				$(moduleId + ' a.publish-notify').remove();
			}
			if(p.type == 4 && operationCodes.indexOf('notification-pub-notice') == -1){//系统公告发布权限
				$(moduleId + ' a.publish-notify').remove();
			}
			//时间控件初始化
			laydate.render({ 
			  elem: moduleId + ' input[name="time"]',
			  type: 'date',
			  max: 0,
			  range: '~',
			  done: function(value, date, endDate){
				    let startTime = '';
		    			endTime = '';
				    if(value!='') {
				    	let list = value.split(' ~ ');
				    		startTime = list[0] + ' 00:00:00';
				    		endTime = list[1] + ' 23:59:59';
				    	startTime = (new Date(startTime)).getTime();
				    	endTime = (new Date(endTime)).getTime();
				    }
				    $(moduleId + ' input[name="startTime"]').val(startTime);
				    $(moduleId + ' input[name="endTime"]').val(endTime)
				    setTimeout(search);
				  }
			});
			//搜索
			$(moduleId + ' .dr-search-form button[role="submit"]').unbind().on('click',function(){
				let keyword = $(moduleId + ' .dr-search-form input[name="keyword"]').val();
				if(keyword && $.trim(keyword).length > 0 ) {
					search();
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
				$(moduleId + ' .dr-search-form input[name="keyword"]').val('');
				$(this).addClass('hide');
				search();
			});
			//下拉框触发搜索
			$(moduleId + ' .dropdown-box').on('click','li a',function(){
				setTimeout(search);
			});
			//已发送、草稿箱切换
			$(moduleId + ' .my-tabs li').on('click',function(){
				searchFormSelect.find('input[name="isDraft"]').val($(this).find('a').attr('data-isdraft'));
				search()
			});
			if(typeof callback == 'function') {
				callback(res.result);
			}
		})
	},
	search = function(){
		tableManage.loadList(1, 10);
	},
	eventInit = function(){
		$(moduleId + ' .cates .cate-item').unbind().on('click',function(){//切换分类
			let self = $(this);
				type = self.attr('data-type');
			if(!self.hasClass('selected')) {
				$(moduleId + ' .cates .cate-item').removeClass('selected');
				self.addClass('selected');
				renderSearchForm({type:type,isDraft:tableParams.isDraft},function(){
					searchFormSelect.find('input[name="type"]').val(type);
					tableManage = new TableManage(tableParams,function(res){
						let isDraft = $(moduleId + ' input[name="isDraft"]').val();
						if(res.opt == 'detail') {
							if(isDraft == 'false') { //展示详情
								goPage('detail?id='+ res.id + '&type=' + res.type);
							} else { //编辑
								goPage('form?id='+ res.id + '&type=' + res.type);
							}
						}
					});
				})
			}
		})
		
		searchFormSelect.unbind();
		searchFormSelect.on('click','.publish-notify',function(){
			getSelectredCate(function(res){
				let type = res.type||1;
				goPage('form?type=' + res.type);
			})
			
		})
	},
	getSelectredCate = function(callback){
		let cate = $(moduleId + ' .cates .selected'),
			obj = {};
		
		if(cate.length == 1) {
			obj.type = cate.attr('data-type');
		} 
		callback(obj)
	};
	return {
		init: init
	}
}())