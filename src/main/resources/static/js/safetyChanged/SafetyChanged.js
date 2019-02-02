var SafetyChanged = function(p) {
    $.extend(this, p);
    var that = this;
    
    that.loadTablelist = function(elemData) {
        var formParams = {
            ecmId: that.ecmId
        }
        if ($(that.wrapId + ' form').length > 0) {
            $.extend(formParams, $($(that.wrapId + ' form')[0]).serializeObject());
        }
        $http.get({
            url: elemData.loadUrl,
            data: formParams
        }, function(res) {
            $(that.wrapId + ' #changed-items_' + elemData.code).html(res);
        })
    };

    that.bindEvent = function() {
        $(that.wrapId + ' ul.my-tabs a').unbind().click(function() {
            that.loadTablelist($(this).data());
        });
    }
    that.init = function() {
        var elemData = $(that.wrapId + ' ul.my-tabs > li.active > a').data();
        that.loadTablelist(elemData);
        that.bindEvent();
    };


    that.init();
};