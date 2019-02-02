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
import cn.cdyxtech.lab.feign.MelAPIFeign;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/lab")
public class LabController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

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
        return "tpl/form";
    }

    @PostMapping("/applyPrint")
    @ResponseBody
    public JSONObject applyPrint(Integer ecmId) {
        Integer userId = 1;
        JSONObject res = new JSONObject();
        res = basicInfoApi.printApply(ecmId, userId);
        this.dealException(res);
        return res;
    }
}
