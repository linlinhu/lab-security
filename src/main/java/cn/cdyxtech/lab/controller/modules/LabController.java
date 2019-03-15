package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.InterfaceProxyCloudAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.filter.MenuOperationFilter;
import cn.cdyxtech.lab.util.UserClaim;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/lab")
public class LabController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(LabController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

    @Autowired
    private InterfaceProxyCloudAPI proxyApi;
    @Autowired
	MenuOperationFilter menuOperationFilter;

    public JSONObject infoTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-lab", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    public JSONObject formTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "form-blab", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("/index")
    public String list(Map<String,Object> data){
        if (this.validateAuthorizationToken().getLabEcmId() == null) {
            throw new EminException("404");
        }
        try {
			String operationCodes = menuOperationFilter.menuOperations("lab");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("实验室信息界面跳转，加载权限出现异常->" + e.getMessage());
		}
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getLabEcmId().toString());
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", infoTpl());

        JSONObject res = basicInfoApi.labInfo(ecmId);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));

        JSONObject existApplyPrintRes = basicInfoApi.existPrintApply(ecmId);
        this.dealException(existApplyPrintRes);
        Integer existApplyId = existApplyPrintRes.getInteger("result");
        data.put("existApplyId", existApplyId);
        
        return "modules/lab/index";
    }

    @GetMapping("/form")
    public String form(Map<String,Object> data, Integer ecmId){
        if (ecmId == null) {
            throw new EminException("404");
        }
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", formTpl());
        JSONObject res = basicInfoApi.labInfo(ecmId);
        this.dealException(res);
        data.put("info", res.getJSONObject("result"));
        data.put("pageSubmit", 1);
        return "tpl/form";
    }

    @PostMapping("/applyPrint")
    @ResponseBody
    public JSONObject applyPrint(Integer ecmId) {
        Long userId = this.validateAuthorizationToken().getId();
        JSONObject res = new JSONObject();
        res = basicInfoApi.printApply(ecmId, userId);
        this.dealException(res);
        return res;
    }

    @GetMapping("/schedulePrint")
    public String schedulePrint(Map<String,Object> data, Integer ecmId) {
        JSONObject res = proxyApi.schedulePrint(ecmId);
        this.dealException(res);
        data.put("schedules", res.getJSONArray("result"));
        data.put("ecmId", ecmId);
        return "modules/lab/schedule";
    }

    @GetMapping("/confirmPrint")
    @ResponseBody
    public JSONObject confirmPrint(Integer ecmId) {
        UserClaim userClaim = this.validateAuthorizationToken();
        Long userId = userClaim.getId();
        if(ecmId == null) {
            ecmId = userClaim.getLabEcmId().intValue();
        }
        if(ecmId == null) {
            ecmId = userClaim.getLabEcmId().intValue();
        }
        JSONObject res = proxyApi.confirmPrint(ecmId, userId);
        this.dealException(res);

        return res;
    }
}
