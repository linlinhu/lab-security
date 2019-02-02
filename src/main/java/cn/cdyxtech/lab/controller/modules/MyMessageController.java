package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.NotificationAPIFeign;
import cn.cdyxtech.lab.util.UserClaim;

import java.util.Map;

@Controller
@RequestMapping("/my-message")
public class MyMessageController extends HeaderCommonController{
	@Autowired
    private MelAPIFeign melApiFeign;
	@Autowired
    private NotificationAPIFeign notificationAPIFeign;
	
	UserClaim userClaim = this.validateAuthorizationToken();
    
    @GetMapping("/index")
    public String index(Map<String,Object> data,Long type){
    	userClaim = this.validateAuthorizationToken();
        Long ecmId = userClaim.getHighestEcmId();
        data.put("ecmId",ecmId);
        data.put("timestamp", System.currentTimeMillis());
        data.put("type", type);
        return "modules/my-message/manage";
    }
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String keyword, Long startTime, Long endTime,Long messageType){
        Long userId = userClaim.getId();
        if (userId == null) {
            throw new EminException("404");
        }
        String sort = "createTime";
    	String order = "desc";
        Integer limit= getPageRequestData().getLimit();
        Integer page= getPageRequestData().getCurrentPage();
        JSONObject res = notificationAPIFeign.querypage(keyword, startTime, endTime, userId, messageType, sort, order, page, limit);
        this.dealException(res);
        data.put("data",res.getJSONObject("result"));
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(0));
        return "tpl/examination/list";
    }
    
    @GetMapping("/detail")
    public String detail(Map<String,Object> data,Long id){
    	JSONObject res = notificationAPIFeign.detail(id, this.validateAuthorizationToken().getId());
    	this.dealException(res);
        data.put("id", id);
        data.put("moduleName", "我的消息");
        data.put("data", res.getJSONObject("result"));
        return "modules/my-message/detail";
    }
    
    @GetMapping("/unread-message-count")
	@ResponseBody
	public JSONObject myNotReadMessageCount() {
    	Long userId = this.validateAuthorizationToken().getId();
		JSONObject res = notificationAPIFeign.myNotReadMessageCount(userId);
		this.dealException(res);
		return res;
	}
    
    @GetMapping("/query-simple-message")
	@ResponseBody
	public JSONObject queryMySimpleMessage(Long count) {
    	userClaim = this.validateAuthorizationToken();
    	Long userId = userClaim.getId();
    	if(count == null) {
    		count = 3L;
    	}
		JSONObject res = notificationAPIFeign.queryMySimpleMessage(userId, count);
		this.dealException(res);
		JSONObject unreadObj = notificationAPIFeign.myNotReadMessageCount(userId);
		this.dealException(unreadObj);
		res.put("count", count);
		res.put("total", unreadObj.getLong("result"));
		return res;
	}
    @PostMapping("/remove")
    @ResponseBody
    public JSONObject remove(Long id){
        if (id == null) {
            throw new EminException("404");
        }
        JSONObject res = notificationAPIFeign.delMyMessage(id, this.validateAuthorizationToken().getId());
        this.dealException(res);
        return res;
    }
    @GetMapping("/read")
    @ResponseBody
    public JSONObject setRead(Long id){
        if (id == null) {
            throw new EminException("404");
        }
        JSONObject res = notificationAPIFeign.setRead(id, this.validateAuthorizationToken().getId());
        this.dealException(res);
        return res;
    }
    
    public JSONObject listTpl() {
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "my-message", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }
    
    public JSONObject formTpl(String formCode) {
        String code = "form-" + formCode;
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, code, "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
}
