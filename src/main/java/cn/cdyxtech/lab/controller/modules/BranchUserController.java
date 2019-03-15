package cn.cdyxtech.lab.controller.modules;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.filter.MenuOperationFilter;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.emin.base.exception.EminException;


@Controller
@RequestMapping("/branch-user")
public class BranchUserController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(BranchUserController.class);
    @Autowired
	MenuOperationFilter menuOperationFilter;
   
    @GetMapping("/index")
    public String index(Map<String,Object> data,String[] showColumns,String[] showOperations){
        data.put("timestamp", System.currentTimeMillis());
        if(ArrayUtils.isNotEmpty(showColumns)){
            data.put("showColumns",JSONArray.toJSONString(showColumns));
        }
        if(ArrayUtils.isNotEmpty(showOperations)){
            data.put("showOperations",showOperations);
        }
        if (this.validateAuthorizationToken().getBranchEcmId() == null) {
            throw new EminException("404");
        }
        try {
			String operationCodes = menuOperationFilter.menuOperations("branch-user");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("院级人员界面跳转，加载权限出现异常->" + e.getMessage());
		}
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getBranchEcmId().toString());
        data.put("title", "院级人员");
        data.put("ecmId", ecmId);
        data.put("ecmDeep", false);
        return "modules/user/index";
    }
}
