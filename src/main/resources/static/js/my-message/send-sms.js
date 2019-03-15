var SendMobileCode = function(mobile,callback){
    var _this = this;
    $.extend(this,{
        mobile:mobile
    })
    this.getMobileCode = function(callback){ //向手机发送验证码
        var url = '/my-message/getMobileCode',
            data = {},
            extendParams = {channel:"clwh",smsTemplate:"【银玺科技】尊敬的用户，您收到的短信验证码信息为:$CODE$"};
        
        extendParams.mobile = mobile;
        data.sequence = _this.getSequence();
        data.extendParams = JSON.stringify(extendParams);
        data.forbidLoading = true;
        $.extend(this,{
            sequence:data.sequence
        })
        $http.get({data:data,url:url},function(res){
            if(!res.success) {
                layer.msg(res.message,{icon:5})
            } else {
                if(typeof callback == 'function') {
                    callback(res)
                }
            }
        })
    },
    this.getSequence = function(){ //产生标识符
        return "modify-mobile-" + new Date().getTime() + "-" + Math.random().toString(36).substr(2);
    };
    this.check = function(code,callback){ //验证验证码
        let url = "/my-message/check",
            data = {
                validateValue: code,
                sequence: _this.sequence
            }
        $http.get({
            data:data,
            url:url
        },function(res){
            if(!res.success) {
                layer.msg(res.message,{icon:5});
                return false;
            }
            if(typeof callback == 'function') {
                callback(res)
            }
        })
    }
    this.init = function(callback){
        this.getMobileCode(callback);
    }
    this.init(callback);
}