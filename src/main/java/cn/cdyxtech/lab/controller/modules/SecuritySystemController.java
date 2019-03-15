package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONObject;

import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.filter.MenuOperationFilter;
import cn.cdyxtech.lab.util.UserClaim;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/security-system")
public class SecuritySystemController extends HeaderCommonController{
	private Logger logger = LoggerFactory.getLogger(SchoolController.class);
	@Autowired
	BasicInfoAPI basicInfoAPI;
	@Autowired
	MenuOperationFilter menuOperationFilter;

    @GetMapping("/index")
    public ModelAndView index(Map<String,Object> data){
    	ModelAndView mv = new ModelAndView("modules/security-system/manage");
		mv.addObject("moculeCode","security-system");
		try {
			String operationCodes = menuOperationFilter.menuOperations("security-system");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("安全体系界面跳转，加载权限出现异常->" + e.getMessage());
		}
        return mv;
    }
    
    /**
     * 模板获取
     * @param serviceId 
     * @param code
     * @param sourceType
     * @return
     */
    @GetMapping("/get-securityTree")
	@ResponseBody
	public JSONObject getSecurityTree(String keyword) {
    	UserClaim userClaim = this.validateAuthorizationToken();
		JSONObject res = basicInfoAPI.getSecurityTree(userClaim.getHighestEcmId(),keyword);
		this.dealException(res);
		/*JSONArray result = res.getJSONArray("result");
		if(!result.isEmpty()) {
			for (int i = 0; i < result.size(); i++) {
	            JSONObject nodeJson = result.getJSONObject(i);
	            Integer level = nodeJson.getInteger("level");
	            if (null != level && level == 2) {
	                nodeJson.put("parentNodeId", null);
	            }
	        }
		}
		res.put("result", result);*/
		return res;
	}

}
