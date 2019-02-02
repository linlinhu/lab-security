function CodesManage(p) {
    $.extend(this, p);
    var that = this;

    function openCodes(sns, title) {
        var temp = sns.split(',');
        var fw = 400;
        var fh = 420;
        if (temp.length > 1 ) {
            fw = 740;
        }
        
        if (temp.length > 2 ) {
            fh = 740;
        }
    
        layer.open({
            type: 2,
            title: title,
            shadeClose: true,
            shade: 0.8,
            area: [fw + 'px', fh + 'px'],
            content: 'qrcode/print?sns=' + sns
        }); 
    }

    that.recordPrint = function(ids, callback) {
        $http.post({
            url: 'qrcode/recordPrint',
            data: {
                applyIds: ids
            }
        }, function(res) {
            callback(res);
        })
    }

    that.noticePrinted = function(ids) {
        $http.post({
            url: 'qrcode/noticePrinted',
            data: {
                applyIds: ids
            }
        }, function() {
            layer.msg('已成功通知打印完成')
        })
    }

    that.codesPrint = function(sns) {
        openCodes(sns, "打印二维码");
    }
    
    that.codesShow = function(sns) {
        openCodes(sns, "查阅二维码");
        
    }
}