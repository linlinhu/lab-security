package cn.cdyxtech.lab.controller.modules.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.feign.ECMAPIFeign;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.NotificationAPIFeign;
import cn.cdyxtech.lab.filter.MenuOperationFilter;
import cn.cdyxtech.lab.util.UserClaim;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/notification-list")
public class NotificationListController extends HeaderCommonController{
	private Logger logger = LoggerFactory.getLogger(NotificationListController.class);
	@Autowired
    private MelAPIFeign melApiFeign;
	@Autowired
    private NotificationAPIFeign notificationAPIFeign;
	@Autowired
	private ECMAPIFeign ecmAPIFeign;
	@Autowired
	private ConfigFacade configFacade;
	@Autowired
	MenuOperationFilter menuOperationFilter;
	
    
    @GetMapping("/index")
    public String index(Map<String,Object> data,Long type, String isDraft){
		Long ecmId = this.validateAuthorizationToken().getHighestEcmId();
		try {
			String operationCodes = menuOperationFilter.menuOperations("notification-list");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("通知列表界面跳转，加载权限出现异常->" + e.getMessage());
		}
        data.put("ecmId",ecmId);
        data.put("timestamp", System.currentTimeMillis());
		data.put("type", type);
		if(isDraft == null) {
			isDraft = "false";
		}
		data.put("isDraft", isDraft);
        return "modules/notification/list/manage";
    }
    
    UserClaim userClaim = this.validateAuthorizationToken();
    @GetMapping("/list")
    public String list(Map<String,Object> data, String[] showColumns, String keyword,Long startTime, Long endTime,Integer type,Long ecmId,String isDraft){
    	JSONObject res = new JSONObject();
    	if(ecmId == null) {
    		ecmId = this.validateAuthorizationToken().getHighestEcmId();
    	}
        data.put("ecmId",ecmId);
        String sort = "createTime";
    	String order = "desc";
        Integer limit= getPageRequestData().getLimit();
		Integer page= getPageRequestData().getCurrentPage();
		data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(type-1));
        if(type.equals(2)) { // 整改通知
        	res = notificationAPIFeign.pageModification(keyword, startTime, endTime, type, sort, order, page, limit, ecmId);
        } else if(type.equals(3)){ //复查通知
        	res = notificationAPIFeign.pageReview(keyword, startTime, endTime, type, sort, order, page, limit, ecmId);
        } else { //检查通知或系统公告
			 Boolean status = false;
			 if(isDraft.equals("true")) {
				if(type.equals(1)) {
					data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(4));
				} else {
					data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(5));
				}
				status = true;
			 }
			 res = notificationAPIFeign.pageMessage(keyword, startTime, endTime, type, sort, order, page, limit, ecmId,status);
        }
		this.dealException(res);
        data.put("data",res.getJSONObject("result"));
        data.put("timestamp", System.currentTimeMillis());
        /* data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(type-1)); */
        return "tpl/examination/list";
    }
    @GetMapping("/search-form")
    public String searchForm(Map<String,Object> data, Integer type,String isDraft){
		data.put("type",type);
		data.put("isDraft",isDraft);
        data.put("timestamp", System.currentTimeMillis());
        return "tpl/notification/search-form";
    }
    @GetMapping("/form")
    public String form(Map<String,Object> data,int type,Long id){
		JSONObject info = new JSONObject();
		if(id != null) {
			JSONObject res = notificationAPIFeign.detail(id, this.validateAuthorizationToken().getId(),true);
    		this.dealException(res);
			info = res.getJSONObject("result");
		}
	
    	UserClaim userClaim = this.validateAuthorizationToken();
    	JSONObject obj = type2obj(type);
    	String formCode = obj.getString("formCode");
		String formName = obj.getString("formName");
		
		try {
			String operationCodes = menuOperationFilter.menuOperations("notification-list");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error(formName + "表单界面跳转，加载权限出现异常->" + e.getMessage());
		}
    	info.put("type", type);
    	info.put("opUserId", userClaim.getId());
    	info.put("opUserPhone", userClaim.getMobile());
    	info.put("opUserName", userClaim.getRealName());
    	info.put("ecmId", userClaim.getHighestEcmId());
    	
    	data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", formTpl(formCode));
        data.put("submitName", "发布");
        data.put("pageSubmit", 1);
        data.put("formName", formName);
        
		data.put("info", info);
        data.put("hasPersonalFunction", "true");
        return "modules/notification/list/form";
    }
    @GetMapping("/selection-officer")
    public String selectionOfficer(Map<String,Object> data){
    	data.put("ecmId", this.validateAuthorizationToken().getHighestEcmId());
        return "modules/notification/list/selection-officer";
    }
    @GetMapping("/detail")
    public String detail(Map<String,Object> data,Long id,Integer type){
    	JSONObject res = notificationAPIFeign.detail(id, this.validateAuthorizationToken().getId(),true);
		this.dealException(res);
    	JSONObject obj = type2obj(type);
    	String formName = obj.getString("formName");
        data.put("moduleName", formName);
        data.put("type", type);
		data.put("data", res.getJSONObject("result"));
        return "modules/my-message/detail";
    }
    /**
     * 获取岗位角色
     */
    @GetMapping("/get-all-job-roles")
	@ResponseBody
	public JSONObject getAllJobRoles(String keyword) {
		JSONObject res = notificationAPIFeign.getAllJobRoles(keyword);
		this.dealException(res);
		JSONArray path = getPath(null);
		res.put("path", path);
		return res;
	}
    /**
     * 查询消息通知学校架构数据
     */
    @GetMapping("/get-security-tree")
	@ResponseBody
	public JSONObject getSecurityTree(String keyword) {
		JSONObject res = notificationAPIFeign.getSecurityTree(keyword);
		this.dealException(res);
		JSONArray path = getPath(null);
		res.put("path", path);
		return res;
	}
    /**
     * 查询下级实验室
     * @param collegeId 学院主体ID
     * 
     */
    @GetMapping("/get-lower-level-labs")
	@ResponseBody
	public JSONObject getLowerLevelLabs(Long collegeId,String keyword) {
		JSONObject res = notificationAPIFeign.getLowerLevelLabs(collegeId,keyword);
		this.dealException(res);
		JSONArray path = getPath(collegeId);
		res.put("path", path);
		return res;
	}
    
    /**
     * 获取学院列表
     * @param ecmId 学校主体编号
     */
    
    @GetMapping("/check-types")
    public String getCollegeList(Map<String,Object> data, String itemToken, String value){
		List<ConfigOption.ConfigItem> datas = configFacade.getConfigItems(ConfigOption.ConfigGroup.SAFETY_INSPECTION_CATEGORY);
        data.put("data", datas);
        data.put("itemToken", itemToken);
        data.put("value", value);
        data.put("max_selected_options", 1);
        return "tpl/combo";
    }
    
    @PostMapping("/remove")
	@ResponseBody
	public JSONObject deleteNotification(Long id) {
		JSONObject res = notificationAPIFeign.deleteNotification(id);
		this.dealException(res);
		return res;
	}
    public JSONObject listTpl() {
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "notification-list", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
    
    public JSONObject formTpl(String formCode) {
        String code = "form-" + formCode;
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, code, "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
    public JSONArray getPath(Long ecmId) {
    	JSONArray path = new JSONArray();
    	JSONObject obj = new JSONObject();
    	Long schoolId = this.validateAuthorizationToken().getHighestEcmId();
    	JSONObject res = new JSONObject();
    	if(ecmId == null) {
    		ecmId = schoolId;
    	}
    	if(ecmId == null) {
    		return path;
    	}
    	if(!schoolId.equals(ecmId)) {
    		res = ecmAPIFeign.detail(schoolId);
        	this.dealException(res);
        	obj.put("name", res.getJSONObject("result").getString("name"));
        	obj.put("id", schoolId);
        	path.add(obj);
    	}
    	obj = new JSONObject();
    	res = ecmAPIFeign.detail(ecmId);
    	this.dealException(res);
    	obj.put("name", res.getJSONObject("result").getString("name"));
    	obj.put("id", ecmId);
    	path.add(obj);
    	return path;
    }
    public JSONObject type2obj(Integer type) {
    	JSONObject res = new JSONObject();
		String formCode = new String();
		String formName = new String();
		switch (type) {
		case 1:
			formCode = "inspection";
			formName = "检查通知";
			break;
		case 2:
			formCode = "modification";
			formName = "整改通知";
			break;
		case 3:
			formCode = "review";
			formName = "复查通知";
			break;
		case 4:
			formCode = "notice";
			formName = "系统公告";
			break;
		default:
			break;
		}
		res.put("formCode", formCode);
		res.put("formName", formName);
		return res;
	}
}
