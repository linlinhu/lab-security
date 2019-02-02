package cn.cdyxtech.lab.controller.modules.statisReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/inspect-report")
public class InspectReportController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private SecurityCheckAPI securityCheckApi;

    @Value("${labApiGateway}")
    private String labAPIGateway;

    public JSONObject safetyOverviewTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-overview", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
    @GetMapping("charts")
    public String charts(Map<String,Object> data, Long id, Integer ecmId){
        JSONObject statisRes = securityCheckApi.inspectStatis(ecmId);
        this.dealException(statisRes);
        data.put("statisData", statisRes.getJSONObject("result"));
        return "modules/statis-report/inspect-report/charts";
    }

    @GetMapping("tablelist")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        data.put("downloadUrl", labAPIGateway + "/api-lab-security-check/check/exportCheckStatistics/" + ecmId);
        return "modules/statis-report/inspect-report/list";
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
