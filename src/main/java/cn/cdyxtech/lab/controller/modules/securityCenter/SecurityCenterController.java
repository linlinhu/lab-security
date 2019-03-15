package cn.cdyxtech.lab.controller.modules.securityCenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.filter.MenuOperationFilter;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/security-center")
public class SecurityCenterController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(SecurityCenterController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
	MenuOperationFilter menuOperationFilter;

    public JSONObject infoTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-scenter", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    public JSONObject formTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "form-scenter", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("index")
    public String index(Map<String,Object> data, Long id){
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Long ecmId = Long.parseLong(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", infoTpl());
        JSONObject res = basicInfoApi.scInfo(ecmId);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));
        try {
			String operationCodes = menuOperationFilter.menuOperations("security-center");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("校级人员界面跳转，加载权限出现异常->" + e.getMessage());
		}
        return "modules/security-center/index";
    }

    @GetMapping("form")
    public String form(Map<String,Object> data, Long id){
        if (id == null) {
            throw new EminException("404");
        }
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", formTpl());
        JSONObject res = basicInfoApi.scInfo(this.validateAuthorizationToken().getHighestEcmId());
        this.dealException(res);
        data.put("info", res.getJSONObject("result"));
        data.put("pageSubmit", 1);
        return "tpl/form";
    }
}
