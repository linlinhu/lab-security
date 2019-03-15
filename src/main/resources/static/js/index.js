var indexCountTimer = null;
(function(){
	$('.my-msg-dp .dropdown-menu').unbind().on('click','a',function(){
		let data = $(this).data();
		
		if(data.id == 'more') {
			goModule({
				moduleName: 'my-message',
				directive: 'index'
			});
		} else {
			if(data.type == 5){
				$http.get({
		            url: "my-message/read",
		            data: {id:data.id}
		        }, function (res) {
		        	goModule({
						moduleName: 'examination',
						directive: 'index'
					});
		        })
			} else {
				goModule({
					moduleName: 'my-message',
					directive: 'detail?id='+data.id,
				});
			}
			
		}
	});
	$('.my-msg-dp').unbind();
	$('.my-msg-dp .my-msg').unbind('dropdown').on('click',function(){
		goModule({
			moduleName: 'my-message',
			directive: 'index'
		});
	});
	$('.my-msg-dp').hover(function(){
		$('.my-msg-dp .my-msg .iconfont').unbind().mouseover(function(){
			if(!$('#my-msg').parents('.my-msg-dp').hasClass('open')){
				getSimpleMessage();
			}
		})
	},function(){
		if($('#my-msg').parents('.my-msg-dp').hasClass('open')){
			$('#my-msg').parents('.my-msg-dp').removeClass('open');
		}
	})

	function getCount(){
		$http.get({
			url:"my-message/unread-message-count",
			forbidLoading: true
		},function(res){
			let total = res.result;
			if(total == 0) {
				$('.my-msg-dp .num').addClass('hide');
			} else {
				if(total > 99) {
					total = '99+'
				}
				$('.my-msg-dp .num').removeClass('hide').text(total);
			}
		})
	};
	function getSimpleMessage(){
		$http.get({
			url:"my-message/query-simple-message",
			forbidLoading: true
		},function(res){
			if(!res.success) {
				layer.msg('获取数据失败',{icon:5});
				return;
			}
			let result = res.result;
			if(result && result.length > 0) {
				let tpl = my_massage_simple_List_tpl.innerHTML,
					view = $('.my-msg-dp .dropdown-menu');
				laytpl(tpl).render(result,function(html){
					html += '<li><a href="javascript:void(0)" data-id="more" class="more">查看更多</a></li>';
					view.html(html);
					$('#my-msg').dropdown('toggle');
				})
			}
		})
	};
	getCount();
	indexCountTimer = setInterval(getCount,60*1000*2);

}())
	
