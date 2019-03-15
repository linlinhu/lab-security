/***
 * 界面菜单跳转的基本方法
 */
function loadContentData(p, callback) {
	var _index = p._index ? p._index : 0,//菜单的data-index属性值
		directive = p.directive ? p.directive : 'index',//操作指令
		flag = p.flag ? p.flag : 0,
		params = p.params ? p.params : null,
		menuItem,//当前选中的菜单
		iframeIndex,
		jumpUrl = '';//菜单跳转的路径
    layer.closeAll()

	menuItem = $('.J_menuItem[data-index=' + _index + ']');
	if (menuItem.length == 1) {
		menuItem = $(menuItem[0]);
	} else {
		console.log('ERR: J_menuItem->data-index=' + _index + ' is not exist or has multiple instances');
		return false;
	}
	$.each($('.J_menuItem'), function() {
		$(this).removeClass('selected');
	})
	menuItem.addClass('selected');

    
    jumpUrl = menuItem.attr('href');
    if (!jumpUrl || jumpUrl.length == 0) {
		console.log('ERR: J_menuItem->data-index=' + _index + ' lost attribute[href]');
		return false;
    }
	iframeIndex = jumpUrl.split('/')[0] + '/';
	jumpUrl = iframeIndex + directive;

    if (directive == 'index') {
    	if (params == null) {
    		if (!$('#content-main').attr('index-params')) {
    			params = {};
    			params.limit = 10;
    			$('#content-main').attr('index-params', JSON.stringify(params));
    		}
        	params = JSON.parse($('#content-main').attr('index-params'));
    	} else {
    		$('#content-main').attr('index-params', JSON.stringify(params));
    	}
    }

    //浏览器控制前进后退，不作hash记录
    if (flag != 3) {
    	window.history.pushState(p, null, location.href.split("#")[0] + "#" + jumpUrl);
    }
   
    $http.get({
    	url: base + jumpUrl,
    	data: params
    }, function(res) {
    	if (!res) {
			layer.msg('登录超时，将跳转至登录界面');
			setTimeout(function(){
    			location.href = "login";
			},3000)
		} else {
        	$('#content-main').html(res);
		}
		if (typeof callback == 'function') {
			callback(res);
		}
    }, function() {
		$http.get({
            url: 'no-page'
        },function(res){
        	$('#content-main').html(res);
        });
    	
    })
}
/***
 * 监听浏览器前进后退
 */
window.onpopstate = function(event) {
	var jumUrl = window.location.hash.substring(1),
		p = event.state;
	
	$('.J_menuTab').each(function() {
		var mii = $(this).attr('iframe-index');
		if (jumUrl.indexOf(mii) >= 0) {
			p._index = $(this).attr('data-index');
			p.directive = jumUrl.substring(mii.length);
			return false;
		}
	})
	
	p.flag = 3;

	layer.closeAll();
	loadContentData(p);
};

//计算元素集合的总宽度
function calSumWidth(elements) {
    var width = 0;
    $(elements).each(function () {
        width += $(this).outerWidth(true);
    });
    return width;
}

$(function () {
    //通过遍历给菜单项加上data-index属性
    $(".J_menuItem").each(function (index) {
        if (!$(this).attr('data-index')) {
            $(this).attr('data-index', index);
        }
    });
    //给每一个大的分类设定高度
    $('.left-cate').each(function(index) {
    	var childrenNum = 0;
    		perandEl= $(this).parent();
    		childrenNum = perandEl.find('.fl_menus').children().length;
    	
    	$(this).css({'height':childrenNum*40+'px'});
    })
    function initIndex(){
    	var locationHref = window.location.href,
    		directPurpose,
    		moduleName,
    		moduleOper,
    		params;
        
    	if (locationHref.split('#').length > 1) {
    		directPurpose =  locationHref.split('#')[1],
    		moduleName = directPurpose.split('/')[0],
    		moduleOper = directPurpose.split('/')[1];
    		if (moduleOper.split('?').length > 1) {
        		params = moduleOper.split('?')[1].split('&');
        		for (var i = 0; i < params.length; i++) {
        			var p = params[i];
        			if (p.split('=')[0] == 'fullScreen') {
        				$('#wrapper > nav').addClass('hide');
        				$('#wrapper > div#page-wrapper').css('margin-left', '0');
        				$('#wrapper > div#page-wrapper > div#content-main').css('height', 'calc(100% - 34px)');
        			}
        		}
    		}

    		loadContentData({
        		_index: $('.J_menuItem[href=' + moduleName + ']').attr('data-index'),
        		directive: moduleOper
        	})
    	} else {
            var index = 0;
            $('.J_menuItem').each(function(){
            	var parentUL = $(this).parents("ul")
            	if(parentUL.hasClass("nav-second-level")){
                    if(parentUL.prev().is(":visible")){
                        index = $(this).data("index")
                        return false;
                    }
				}else{
                    if($(this).parent().is(":visible")){
                        index = $(this).data("index")
                        return false;
                    }
				}
            })

            loadContentData({

        		_index: index
        	})
    	}
		return false;
    }
    function menuItem() {
    	$('.layui-laydate').remove();
    	$('.J_menuItem').removeClass('selected');
    	$('.navbar-static-side li').removeClass('selected');
    	if($(this).parent().hasClass('top-menu-tab')){
    		$(this).addClass('selected')
    	} else {
    		if($(this).parent().parent().hasClass('sec_menus')) {
    			$(this).parent().parent().parent().parent().addClass('selected');
    		}
			$('.J_menuItem').parent().removeClass('selected');
    		$(this).parent().addClass('selected');
    		
    		
    	}
    	
        loadContentData({
        	_index: $(this).data('index'),
        	flag: 1,
        	params: {
        		limit: 10
        	}
        });
		return false;
    }

    $('.J_menuItem').on('click', menuItem);
    initIndex()
});


/***
 * 页面操作跳转
 * @param directive 动作指令
 * @param params 参数
 * @returns {Boolean}
 */
function goPage(directive, params, callback) {
	params = params ? params : {};
	$.extend(params, {
		moduleName: location.href.split('/')[3].split('#')[1],
		directive: directive
	})
	goModule(params, callback);

}
/***
 * 跨模块跳转
 * @param params 参数 包含moduleName（模块名称）、directive（指令）
 * @param callback 回调函数
 */
function goModule(params, callback){
	var _index = $('a[href="'+ params.moduleName +'"]').attr('data-index');
	params.limit = 10
	loadContentData({
    	_index: _index,
    	flag: 2,
    	directive:params.directive ? params.directive : 'index',
    	params: params
    },function(res) {
    	if (typeof callback == 'function') {
        	callback(res);
    	}
    });
}

Array.prototype.contains = function ( needle ) {
	for (i in this) {
		if (this[i] == needle) return true;
	}
	return false;
}