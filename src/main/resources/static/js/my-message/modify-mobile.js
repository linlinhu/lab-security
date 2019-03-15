var ModifyMobile = function(params) {
    var that = this;
	$.extend(that, params);
	$.extend(that, {
		myreg:/^[1][3,4,5,7,8][0-9]{9}$/
	});
	var step1 = '#modify-mobile-step-1',
        step2 = '#modify-mobile-step-2',
		smartwizardSelector = '#modify-mobile-smartwizard',
		sendMobileCode = null,
		timeNumber = 0,
		intervalTimer;
	that.getFormData = function(id) {
		var basic = $(id + ' form').serializeObject();
		return basic;
	};
	that.save = function() { 
		let data = that.getFormData(step2)
		sendMobileCode.check(data.code,function(res){
			that.modify(data);
		});
		
	};
	that.modify = function(data,callback){ //编辑电话号码，如果成功，返回成功界面
		let url = "/my-message/modify-user-info"
		$http.post({
            data:data,
            url:url
        },function(res){
            $http.get({
				data:{mobile:data.mobile},
				url:'/my-message/modify-success'
			},function(res){
				$(smartwizardSelector).parents('.modify-mobile').html(res)
				
			})
			if(typeof callback == 'function'){
				callback(res)
			}
            
        })
	}
	that.setBtnName = function (el,timer) {//发送按钮的状态设置
		if(timeNumber > 0) {
			timeNumber -= 1;
			el.html(timeNumber + '秒');
			el.attr('disabled','disabled')
		} else {
			el.html('获取短信验证码');
			el.removeAttr('disabled');
			clearInterval(timer);
			timer = null;	
		}
	}
	
    that.init = function() {
        var btnSubmit = $('<button type="button"></button>').html('确定')
                .addClass('btn btn-primary submit hide')
                .on('click', function(){
					if($(step2 + ' form').valid()) {
						that.save()
					}
                });
        $(smartwizardSelector).smartWizard({//插件初始化
            selected: 0,
            keyNavigation: false,
            theme: 'circles',
            transitionEffect:'fade',
            autoAdjustHeight:false,
            showStepURLhash: false,
            useURLhash:true,
            lang: { // Language variables for button
                next: '下一步 <i class="fa fa-chevron-right"></i>',
                previous: '<i class="fa fa-chevron-left"></i> 上一步'
            },
            toolbarSettings: {
                toolbarButtonPosition: 'end',
                toolbarExtraButtons: [btnSubmit]
            },
            anchorSettings: {
                markDoneStep: true, // add done css
                markAllPreviousStepsAsDone: true, // When a step selected by url hash, all previous steps are marked done
                removeDoneStepOnNavigateBack: true, // While navigate back done step after active step will be cleared
                enableAnchorOnDoneStep: true // Enable/Disable the done steps navigation
            }
		});
		$(".sw-btn-prev").remove();
		$(".sw-theme-circles .btn-toolbar .btn").attr('disabled','disabled');
		$(smartwizardSelector + ' .sw-btn-group').append('<span class="cover"></span>');
        $(smartwizardSelector).on("showStep", function(e, anchorObject, stepNumber, stepDirection) {
            if(stepNumber==1){
                $(".sw-btn-group-extra .btn").removeClass("hide");
				$(".sw-btn-next").hide();
            }else{
                $(".sw-btn-group-extra .btn").addClass("hide");
                $(".sw-btn-next").show()
            }
        });
        $(smartwizardSelector).on("leaveStep", function(e, anchorObject, stepNumber, stepDirection) {//离开当前页
			e.stopPropagation();
        	if(stepNumber === 0) {
				if(!$(step1 + ' form').valid()) {
					return false;
				}
			}
			return true;
		});
		$(smartwizardSelector + ' .sendSMS').unbind().on('click',function(){//验证码按钮绑定事件
			let self = $(this),
				link_btn = self.attr('link-btn'),
				parent = self.parents('form'),
				mobile = parent.find('input[name="mobile"]').val();
			if (!that.myreg.test(mobile)) {
				layer.msg('请输入11位有效的手机号码',{icon:5});
				return false;
			}
			sendMobileCode = new SendMobileCode(mobile);
			timeNumber = 60;
			if(intervalTimer) {
				clearInterval(intervalTimer);
			}
			intervalTimer = setInterval(function(){
				that.setBtnName(self,intervalTimer)
			},1000)
			$(smartwizardSelector + ' .' + link_btn).removeAttr("disabled")
		});
		$(smartwizardSelector + ' .sw-btn-group .cover').unbind().on('click',function(e) {//调用验证码验证方法，如果通过，就触发‘下一步’
			e.stopPropagation();
			let parent = $(this).parent(),
				btn = parent.find('.btn');
			if(btn.attr('disabled') != 'disabled') {
				if(!$(step1 + ' form').valid()) {
					return false;
				}
				let formData = that.getFormData(step1)
				sendMobileCode.check(formData.code,function(res){
					$(this).remove();
					btn.trigger('click');
				});
				
			}	
		});
    }
    that.init();
};