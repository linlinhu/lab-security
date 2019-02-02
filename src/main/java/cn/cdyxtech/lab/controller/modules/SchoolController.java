package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
 
@Controller
@RequestMapping("/school")
public class SchoolController extends HeaderCommonController {
    
    private Logger logger = LoggerFactory.getLogger(SchoolController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

    public JSONObject infoTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-school", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    public JSONObject formTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "form-school", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    public JSONObject schoolInfo(Integer ecmId) {
        JSONObject info = new JSONObject();
        JSONObject res = basicInfoApi.schoolInfo(ecmId);
        this.dealException(res);
        info = res.getJSONObject("result");
        return info;
    };
    
    @GetMapping("/index")
    public String index(Map<String,Object> data){
        logger.error("加载学校详情界面++++++++++++++++++++++++++++=");
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            logger.error("学校ecmId = " + this.validateAuthorizationToken().getSchoolEcmId() + "++++++++++++++++++++++++++++=");

            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", infoTpl());
        data.put("data", schoolInfo(ecmId));
        logger.error(JSONObject.toJSONString(data) + "====================================================");
        
        return "modules/school/index";
    }

    @GetMapping("/form")
    public String form(Map<String,Object> data, Integer ecmId){
        if (ecmId == null) {
            throw new EminException("404");
        }
        data.put("tpl", formTpl());
        data.put("timestamp", System.currentTimeMillis());
        data.put("info", schoolInfo(ecmId));

        JSONObject uploadParams = new JSONObject();
        JSONObject logo = new JSONObject();
        logo.put("fileNumberLimit", 1);
        logo.put("title", "请上传学校LOGO");
        uploadParams.put("logo", logo);
        data.put("uploadParams", uploadParams);

        data.put("pageSubmit", 1);
        return "tpl/form";
    }
}
