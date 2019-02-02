package cn.cdyxtech.lab.controller.modules.safetyOverview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/inspect-statis")
public class InspectStatisController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private SecurityCheckAPI securityCheckApi;


    public JSONObject safetyOverviewTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-overview", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        
        return "modules/safety-overview/inspect-statis/index";
    }

    
    @GetMapping("/list")
    public String list(Map<String,Object> data, 
        String hiddenDangerGrade, 
        Integer ecmId, String keyword){
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(5));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.inspectPage(ecmId, null, null, hiddenDangerGrade, null, null, keyword, page, limit, null);
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }

    
    
    @GetMapping("detail")
    public String detail(Map<String,Object> data, 
        Integer id, 
        Integer labId, 
        String detailTitle){

        data.put("id", id);
        data.put("labId", labId);
        data.put("detailTitle", detailTitle);

        return "tpl/inspect-detail/detail";
    }

}
