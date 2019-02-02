var SafetyChanged = function(p) {
    $.extend(this, p);
    var that = this;
    
    that.loadManageList = function() {
        var setHtml = function(elem, url) {
            $http.get({
                url: url
            }, function(res) {
                elem.html(res);
            })
        }
        console.log(that.wrapId + ' .safety-changed-group');
        $(that.wrapId + ' .safety-changed-group').each(function(i){
            var elem = $(this);
            var url = that.dataUrl[i].url;
            setHtml(elem, url);
        });
    };
    // that.bindEvent = function() {
    //     $(that.wrapId + ' ul.my-tabs a').unbind().click(function() {

    //     });
    // }
    that.init = function() {
        // that.loadManageList();
    };
    that.init();
};