var TableManage = function(p, callback) {
    $.extend(this, p);
    var that  = this;
    that.bindEvent = function() {
        CommonUtil.itemsCheck({
            allSelector: that.wrapId + ' input.selectAll',
            itemSelector: that.wrapId + ' input.itemSelect'
        });
        $(that.wrapId + ' .entityOpts').unbind().click(function() {
            var opt = $(this).data("opt");
            that.optData = $(this).data();
            
            if (that.optData.trIndex >= 0) {
                var hiddenInput = $($(that.wrapId + ' tbody > tr')[that.optData.trIndex]).find('input');
                $.each(hiddenInput, function() {
                    that.optData[$(this).attr('name')] = $(this).attr('value');
                });
            } else {
                $.extend(that.optData, $(this).parent().parent().data());
            }
            console.dir(that.optData);
            if (opt != 'remove' && typeof callback == 'function') {
                callback(that.optData);
                return false;
            }
            
            eval("that." + opt + "()");
        });
    };
    that._form = function(id) {
        $http.get({
            url: that.moduleName + '/form',
            data: this.optData,
            forbidLoading: true
        }, function(res) {
            layer.open({
                title:id ? "编辑" : "新增",
                type: 1,
                area: "400px",
                btn: ["保存"],
                content:res,
                yes:function(index,layero){
                    var form = $(layero).find("form")
                    if(form.valid()){
                        new FormTpl({
                            wrapSelector: '#' + form[0].id
                        }).submitObj(function() {
                            layer.close(index);
                            that.loadList(1, 10);
                        });
                    }
                }
            })
        });
    };
    that.add = function() {
        that._form()
    },
    that.edit = function() {
        that._form()
    },
    that.remove = function() {
        $http.post({
            url: that.moduleName + '/remove',
            data: that.optData
        }, function(res) {
            that.loadList(that.curPage, that.curLimit);
        })
    }
    that.curPage = 1;
    that.curLimit = 10;
    that.loadList = function(page, limit) {
        that.curPage = page;
        that.curLimit = limit;
        var params = {
            page: page || 1,
            limit: limit,
            ecmId: that.ecmId
        }
        if (that.params) {
            $.extend(params, that.params);
        }
        var formEl = $(that.wrapId + ' form');
        if (formEl.length == 1) {
            $.extend(params, formEl.serializeObject());
        }
        if (that.showColumns && that.showColumns.length > 0) {
            params.showColumns = that.showColumns.join(',')
        }
        $http.get({
            url: that.moduleName + '/list',
            data: params,
            forbidLoading: true
        }, function(res) {
            $(that.wrapId + " .data-list").html(res);
            if (typeof that.loaded == 'function') {
                that.loaded();
            }
            that.bindEvent();
            $(that.wrapId + " .i-checks").iCheck({checkboxClass: "icheckbox_square-green", radioClass: "iradio_square-green",})
            var pagingContainer = $(that.wrapId + " .pageContainer")
            var pageData = pagingContainer.data();
            if(pageData && pageData.total && pageData.total > 0) {
            	laypage.render({
                    elem:pagingContainer[0],
                    count:pageData.total,
                    curr:pageData.page,
                    limit:limit,
                    limits:true,
                    layout:['prev', 'page', 'next','limit','skip'],
                    theme: '#55A8FD',
                    jump: function(obj, first){
                        if(!first){
                            that.loadList(obj.curr,obj.limit)
                        }
                    }
                })
            }      
        });
    };
    that.formChange = function() {
        $(that.wrapId + ' form').unbind('change').change(function() {
            that.loadList(1, 10);
        });
        $(that.wrapId + ' form button[data-opt="reloadList"]').unbind('click').click(function() {
            that.loadList(1, 10);
        });
    }
    that.init = function() {
        that.loadList(1, 10);
        that.formChange();
    };
    that.init();
};