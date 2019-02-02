package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;
 
@Controller
@RequestMapping("/safety-inspect")
public class SafetyInspectController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

    @Autowired
    private SecurityCheckAPI securityCheckApi;

    public JSONObject listTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "list-sinspect", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    public JSONObject labInfoTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-lab", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    
    @GetMapping("/index")
    public String index(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getHighestEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getHighestEcmId().toString());
        data.put("ecmId", ecmId);

        if (this.validateAuthorizationToken().getSchoolEcmId() != null) {
            data.put("schoolEcmId", this.validateAuthorizationToken().getSchoolEcmId());
        }
        
        return "modules/safety-inspect/index";
    }

     
    @GetMapping("/list")
    public String list(Map<String,Object> data, 
        String checkType, 
        String hiddenDangerGrade,
        Integer ecmId, 
        Long startTime, 
        Long endTime){
        
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getHighestEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getHighestEcmId().toString());
        }
        

        data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.inspectPage(ecmId, null, checkType, hiddenDangerGrade, startTime, endTime, null, page, limit, null);
        this.dealException(res);
        JSONObject result = res.getJSONObject("result");
        data.put("data", gradeReloadResult(result));
        return "tpl/waterfall-list";
    }

    
    @GetMapping("/detail")
    public String detail(Map<String,Object> data, 
        Integer id, 
        Integer labid, 
        String detailTitle){
            
        data.put("timestamp", System.currentTimeMillis());
        if (id != null) {
            data.put("tpl", labInfoTpl());
            JSONObject res = basicInfoApi.labInfo(labid);
            this.dealException(res);
            data.put("data", res.getJSONObject("result"));
        }
        if (labid != null) {
            JSONObject res = securityCheckApi.inspectDetail(id);
            this.dealException(res);
            data.put("histories", res.getJSONObject("result").getJSONArray("processList"));
        }
        data.put("detailTitle", detailTitle);
        return "modules/safety-inspect/detail";
    }

}
