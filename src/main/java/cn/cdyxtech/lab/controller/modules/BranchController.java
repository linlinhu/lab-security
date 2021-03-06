package cn.cdyxtech.lab.controller.modules;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/branch")
public class BranchController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(SchoolController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
	MenuOperationFilter menuOperationFilter;

    public JSONObject infoTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-branch", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    public JSONObject formTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "form-branch", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
        if (this.validateAuthorizationToken().getBranchEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getBranchEcmId().toString());
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", infoTpl());
        JSONObject res = basicInfoApi.branchInfo(ecmId);
        this.dealException(res);
        try {
			String operationCodes = menuOperationFilter.menuOperations("branch");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("学院信息界面跳转，加载权限出现异常->" + e.getMessage());
		}
        data.put("data", res.getJSONObject("result"));
        return "modules/branch/index";
    }

    @GetMapping("form")
    public String form(Map<String,Object> data, Integer ecmId){
        if (ecmId == null) {
            throw new EminException("404");
        }
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", formTpl());
        JSONObject res = basicInfoApi.branchInfo(ecmId);
        this.dealException(res);
        data.put("info", res.getJSONObject("result"));
        data.put("pageSubmit", 1);
        return "tpl/form";
    }


}
