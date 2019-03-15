package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.ExaminationAPIFeign;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.filter.MenuOperationFilter;
import cn.cdyxtech.lab.util.UserClaim;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/examination")
public class ExaminationController extends HeaderCommonController{
    private Logger logger = LoggerFactory.getLogger(SchoolController.class);
	@Autowired
    private MelAPIFeign melApiFeign;
	@Autowired
    private ExaminationAPIFeign examinationAPIFeign;
    @Autowired
	MenuOperationFilter menuOperationFilter;

	
	
    
    @GetMapping("/index")
    public String index(Map<String,Object> data){
        Long ecmId = this.validateAuthorizationToken().getHighestEcmId();
        data.put("ecmId",ecmId);
        data.put("timestamp", System.currentTimeMillis());
        try {
			String operationCodes = menuOperationFilter.menuOperations("examination");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("审核管理界面跳转，加载权限出现异常->" + e.getMessage());
		}
        return "modules/examination/manage";
    }
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String[] showColumns, String keyword, Long startTime, Long endTime,Boolean isAgree){
    	UserClaim userClaim = this.validateAuthorizationToken();
    	String sort = "applyStatus,lastModifyTime";
    	String order = "asc,desc";
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(0));
        Integer limit= getPageRequestData().getLimit();
        Integer page= getPageRequestData().getCurrentPage();
        Long userId = userClaim.getId();
        JSONObject res = examinationAPIFeign.querypage(keyword, startTime, endTime, null, userId, isAgree, sort, order, page, limit);
        this.dealException(res);
        JSONObject result = res.getJSONObject("result");
        JSONArray resultList = result.getJSONArray("resultList");
        JSONObject item;
        if(!resultList.isEmpty()) {
        	for(int i = 0; i < resultList.size(); i++) {
        		item = resultList.getJSONObject(i);
        		if(item.getBooleanValue("isSecurityCenter")) {
        			item.put("ecmName", "安全中心");
        		}
        	}
        	result.put("resultList", resultList);
        }
        data.put("data",result);
        return "tpl/examination/list";
    }
    
    @PostMapping("/verify")
	@ResponseBody
	public JSONObject verify(Long id, Long isAgree) {
    	UserClaim userClaim = this.validateAuthorizationToken();
    	Long userId = userClaim.getId();
		JSONObject res = examinationAPIFeign.verify(id, userId, isAgree);
		this.dealException(res);
		return res;
	}
    
  
    
    public JSONObject listTpl() {
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "examination", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
    
   
}
