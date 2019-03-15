var FormTpl = function(p) {
    // 必须传入form选择器，唯一 wrapSelector = '#?'
    
    $.extend(this, p);
    var that = this;
    /**
     * 优化单选框和复选框
     */
    that.icheck = function() {
        if ($(that.wrapSelector + ' .i-checks').length > 0) {
            $(that.wrapSelector + ' .i-checks').iCheck({
                checkboxClass: 'icheckbox_square-green',
                radioClass: 'iradio_square-green',
            });
        }
    };
    /**
     * 初始化富文本编辑器
     */
    that.summernote = function() {
    	if($(that.wrapSelector + ' .summernote').length > 0) {
    		let summernote = $(that.wrapSelector + ' .summernote').summernote({
    			
    			toolbar: [
    			          ['paragraph',['style']],
    			          ['fontname', ['fontname']],
    			          ['fontsize', ['fontsize']],
    			          ['color', ['color']],
    			          ['style', ['bold', 'italic', 'underline', 'clear']],
    			          ['para', ['ul', 'ol', 'paragraph']],
    			          ['height', ['height']],
    			          ['insert',['picture','link','table','hr']],
    			          ['misc',['codeview','help']]
    			        ],
                lang :"zh-CN",
                height: 150,
                disableDragAndDrop: true,
                callbacks: {
                    onImageUpload: function(files) {
                        var fileData = new FormData();
                        fileData.append("myType","img")
                        fileData.append("file",'')
                        for(let i = 0; i < files.length; i++) {
                        	fileData.set("file",files[i])
                        	$http.post({
                                url:"/file/upload.do",
                                contentType:false,
                                processData:false,
                                data:fileData
                            },function(res){
                                var imgSrc = res.storage[0].fileStorageUrl
                                var img = $("<img></img>").attr("src",imgSrc).addClass("img-responsive recordImg").css({"width":"144px","height":"144px"})
                                summernote.summernote("insertNode",img[0]);
                               /* console.log(summernote.data())*/
                            })
                        }  
                    },
                    onImageLinkInsert: function(link){
                    	var imgSrc = link;
                        var img = $("<img></img>").attr("src",imgSrc).addClass("img-responsive recordImg").css({"width":"144px","height":"144px"})
                        summernote.summernote("insertNode",img[0]);
                    }
                }
    		})
    	}
    	
    };
    /**
     * 嵌入选择器代码
     */
    that.embeddedSelector = function() {
        $(that.wrapSelector + ' .empty-content').each(function() {
            var self = $(this);
            var data = self.data();
            $http.get({
                url: data.url,
                data: {
                    itemToken: data.itemToken,
                    value: data.value
                },
                forbidLoading: true
            }, function(res) {
                self.html(res);
                that.icheck();
            });
        })
    };
   
    /**
     * 地图定位
     */ 
    that.mapLocation = function() {
        $(that.wrapSelector + ' .map-selection').each(function() {
            var self = $(this);
            var mapSelectionCore = function () {
                var taEl = $(self.siblings('textarea')[0]);
                var address = taEl.val();
                var itemToken = taEl.attr('name');
                new MapSelection({
                    root: that.wrapSelector,
                    mapId: 'map-container-' + itemToken,
                    value: address
                }, function(res) {
                    taEl.val(res.address);
                });	
            };
            mapSelectionCore();
            $(self.siblings('textarea')[0]).unbind('change').change(mapSelectionCore);
        });

        
        
    }
    
    that.removePic = function(picUrl, pics) {
        var removeIndex = -1;
        for (var i in pics) {
            if (pics[i] && pics[i].storage) {
                
                if (pics[i].storage[0].fileStorageUrl == picUrl) {
                    removeIndex = i;
                }
            }
        }
        if (removeIndex >= 0) {
            pics.splice(removeIndex, 1);
        }
        return pics;
    }

    that.picSelector = function() {
        var bindRemoveEvent = function() {
            $(that.wrapSelector +  ' .pic-selector a.remove').unbind().click(function() {
                var data = $(this).data();
                
                var htmlEl = $(this).parent().parent().siblings('value');
                var pics = JSON.parse(htmlEl[0].innerHTML);
                
                // 删除图片数据
                pics = that.removePic(data.url, pics);
                htmlEl[0].innerHTML = JSON.stringify(pics);

                // 删除图片对象
                $(this).parent().remove();
                $.extend(data, htmlEl.parent().data());
                console.log(data,pics)
                if (data.fileNumLimit > pics.length) {
                    htmlEl.siblings('a.upload').removeClass('hide');
                }
            });
        }
        $(that.wrapSelector + ' .pic-selector a.upload').unbind().click(function() {//上传图片
            var self = this;
            var data = $(self).data();
            $.extend(data, $(this).parent().data());
            $.extend(data, {
                title: '上传图片',
                uploadUrl: 'file/upload.do',

                /*data:{myType:"img"},
                filesType: ['image']
            });
            
            var htmlEl = $(self).siblings('value')[0];
            var pics = JSON.parse(htmlEl.innerHTML);*/
                data:{myType: "img"},
                filesType: ['img']
            });
            
            var htmlEl = $(self).siblings('value')[0];
            var pics =  [];
            if (htmlEl.innerHTML.replace(/\s*/g,"") != "") {
                pics = JSON.parse(htmlEl.innerHTML);
            }

            new EminAontherFileUpload().init(data,function(response){
                if (!response.success) {
                    layer.msg(response.message);
                    layer.closeAll();
                    return false;
                }
                var res = response.result;
                pics.push(res);
                htmlEl.innerHTML = JSON.stringify(pics);

                var html = '';
                if (res.storage) {
                    var picUrl = res.storage[0].fileStorageUrl;

                    html += '<li>' +
                        '<i class="pic" style="background-image:url(' + picUrl + ')"></i>' +
                        '<a class="remove" href="javascript:;" data-url="' + picUrl + '"><i class="fa fa-remove"></i></a>' +
                    '</li>';
                }
                $(self).siblings('ul.selected-area').append(html);
                bindRemoveEvent();

                var picLen = $(self).siblings('ul.selected-area').find('li').length;
                if (data.fileNumLimit <= picLen) {
                    $(self).addClass('hide');
                }
                layer.closeAll();
            })
        });
        bindRemoveEvent();  
    };

    that.fileSelector = function() {
        var bindRemoveEvent = function() {
            $(that.wrapSelector +  ' .file-selector a.remove').unbind().click(function() {
                var data = $(this).data();
                
                var htmlEl = $(this).parent().parent().siblings('value');
                var files = JSON.parse(htmlEl[0].innerHTML);
                
                // 删除文件数据
                pics = that.removePic(data.url, files);
                htmlEl[0].innerHTML = JSON.stringify(files);

                // 删除文件对象
                $(this).parent().remove();
                $.extend(data, htmlEl.parent().data());

                if (data.fileNumLimit > files.length) {
                    htmlEl.siblings('a.upload').removeClass('hide');
                }
            });
        }
        $(that.wrapSelector + ' .file-selector a.upload').unbind().click(function() {//上传文件
            var self = this;
            var data = $(self).data();
            var type = ["img","excel", "doc","pdf"];
            $.extend(data, $(this).parent().data());
            $.extend(data, {
                title: '上传文件',
                uploadUrl: 'file/universalUpload.do',
                filesType: type,
                data:{ecmId: that.ecmId,}
            });
            
            var htmlEl = $(self).siblings('value')[0];
            var files =  [];
            if (htmlEl.innerHTML.replace(/\s*/g,"") != "") {
                files = JSON.parse(htmlEl.innerHTML);
            }

            new EminAontherFileUpload().init(data,function(response,file){
                if (!response.success) {
                    layer.msg(response.message);
                    layer.closeAll();
                    return false;
                }
                var res = response.result;
                res.originalName = file.name
                files.push(res);
                htmlEl.innerHTML = JSON.stringify(files);

                var html = '';
                if (res.storage) {
                    var fileUrl = res.storage[0].fileStorageUrl;

                    html += '<li>' +
                        '<i class="fa '+res.viewFileType+'"></i><span class="file-name"> ' + res.originalName+ '</span>' +
                        '<a class="remove" href="javascript:;" data-url="' + fileUrl + '"><i class="fa fa-remove"></i></a>' +
                    '</li>';
                }
                $(self).siblings('ul.selected-area').append(html);
                bindRemoveEvent();

                var fileLen = $(self).siblings('ul.selected-area').find('li').length;
                if (data.fileNumLimit <= fileLen) {
                    $(self).addClass('hide');
                }
                layer.closeAll();
            })
        });
        bindRemoveEvent();  
    };

    that.relateSelector = function() {
        var bindRemoveEvent = function() {
            $(that.wrapSelector + ' .relate-selector a.remove').unbind().click(function() {
                var self = $(this);
                var tableEl = self.parent().parent().parent().parent();
                
                var rpHtml = $(tableEl.siblings('relateParams')[0]).html();
                var relateParams = JSON.parse(rpHtml);
                var dataLimit = parseInt(relateParams.dataLimit);
                
                var valueHtml = $(tableEl.siblings('value')[0]).html();
                var value = valueHtml.split(',');

                var removeId = $($(self.parent().siblings('td')[0]).find('input')[0]).attr('value');
                value = value.splice(removeId, 1)

                $(tableEl.siblings('value')[0]).html(value.join(','));
                if(value.length < dataLimit) {
                    $(tableEl.siblings('a.relate')[0]).removeClass('hide');
                }

                
                self.parent().parent().remove();
                
            });
        };
        bindRemoveEvent();
        $(that.wrapSelector + ' .relate-selector a.relate').unbind().click(function() {
            var self = $(this);
            var rpHtml = $(self.siblings('relateParams')[0]).html();
            var relateParams = JSON.parse(rpHtml);
            var modelInfo = relateParams.modelInfo;
            $http.get({
                url: relateParams.serverHost + '/dataModel/' + modelInfo.serviceId + '/' + modelInfo.code,
                data: {
                    sourceType: 'BROWSER'
                },
                forbidLoading: true
            }, function(res) {
                var openTitle = res.result.groups[0].name;
                var items = res.result.groups[0].items;
                var showCloumns = ['select'];
                for (var i in items) {
                    if (items[i].dataKey) {
                        showCloumns.push(items[i].dataKey);
                    }
                }
                $http.get({
                    url: relateParams.dataUrl + '?showColumns=' + showCloumns.join(',') + '&showOperations=null',
                    forbidLoading: true
                }, function(res) {
                    layer.open({
                        title: openTitle,
                        type:1,
                        area:"800px",
                        btn:["确定"],
                        content:res,
                        yes:function(index,layero){
                            var checks = $(layero).find("tbody input:checked");
                            var dataLimit = parseInt(relateParams.dataLimit);
                            if (dataLimit > 0 && checks.length > dataLimit) {
                                layer.msg('最多可选择' + dataLimit + '条数据');
                                return false;
                            }
                            var trs = [];
                            var relateIds = [];
                            $.each(checks, function() {
                                relateIds.push($(this).attr('value'));
                                trs.push($(this).parent().parent().parent().html());
                            });
                            for (var i in trs) {
                                self.siblings('table').find('tbody').append('<tr>' + trs[i] + '<td><a class="remove"><i class="fa fa-minus"></i></a></td></tr>');
                            }
                            self.siblings('value').html(relateIds.join(','));
                            bindRemoveEvent();
                            var relateLen = self.siblings('table').find('tr').length;
                            if (dataLimit <= relateLen) {
                                self.addClass('hide');
                            }
                            layer.close(index);
                        }
                    })
                });
            });
        });
    };

    that.init = function(callback) {
        that.icheck();
        that.embeddedSelector();
        that.mapLocation();
        that.picSelector();
        that.fileSelector();
        that.relateSelector();
        that.formSubmit(callback);
        that.summernote();
    };
    that.submitObj = function(callback,ignoreValidation) {
        var submitObj = $(that.wrapSelector).data();
        var data = that.getFormData();
        
        submitObj.data = JSON.stringify(data);
        delete submitObj['validator'];
        if(!ignoreValidation) {
            if(!that.validateFn(data)){
                return false;
            }
        }
        $http.post({
            url: 'mel/save',
            data: submitObj
        }, function(res) {
            if (res) {
                var result = JSON.parse(res);
                if (result.success) {
                    layer.msg('保存成功！');
                    if (typeof callback == 'function') {
                        callback(result);
                    } else {
                        goPage('index');
                    }
                }
            } 
        });
    };
    that.formSubmit = function(callback) {
        /*$(that.wrapSelector).validate({
            submitHandler: function (form) {
                that.submitObj(callback);
            }
        });*/
    	$(that.wrapSelector + ' a[type="submit"]').unbind().on('click',function(){
    		that.submitObj(callback);
    	})
    };
    that.getFormData = function(){
        var data = $(that.wrapSelector).serializeObject();
        $(that.wrapSelector + ' value').each(function(){
            var item = $(this);
            data[item.data().name] = item.text().replace(/\s*/g,"");
        })
        $(that.wrapSelector + ' select[multiple="multiple"]').each(function(){
            var self = $(this);
            	value = self.val();
            
        	if(value && value.length > 0) {
        		value = value.join(',');
        	} else {
        		value = '';
        	}
            data[self.attr('name')] = value;
        })
        $(that.wrapSelector + ' .summernote').each(function(){
            var self = $(this),
            	html = self.summernote("code");
            data[self.attr('name')] = html;
        })
         $(that.wrapSelector + ' input.nyrsfm').each(function(){
            var item = $(this);
            data[item.attr('name')] = item.attr('data-value');
        })
        return data;  
    };
    that.validateFn = function(submitData){
		let validateData = that.validateData || {},
			rules = validateData.rules || {},
			messages = validateData.messages || {};
		if(rules!={}){
			for(key in rules) {
				if(rules[key].required) {
					if(submitData[key] == '') {
						layer.msg(messages[key].required,{icon:5});
						return false;
						break;
					}
				}
				if(rules[key].regEx != '') {
					let reg = new RegExp(rules[key].regEx);
					if(!reg.test(submitData[key])) {
						layer.msg(messages[key].required,{icon:5});
						return false;
						break;
					}
				}
			}
		}
		return true;
	}
    that.init(that.submitCallback);
};