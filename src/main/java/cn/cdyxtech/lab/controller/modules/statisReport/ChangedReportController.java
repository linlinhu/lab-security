package cn.cdyxtech.lab.controller.modules.statisReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/changed-report")
public class ChangedReportController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;

    @Autowired
    private SecurityCheckAPI securityCheckApi;

    @Value("${labApiGateway}")
    private String labAPIGateway;

    public JSONObject groupTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-changed", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    public JSONObject safetyOverviewTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-overview", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    
    @GetMapping("charts")
    public String charts(Map<String,Object> data, Long id, Integer ecmId){
        JSONObject statisRes = securityCheckApi.changedStatis(ecmId);
        this.dealException(statisRes);
        data.put("statisData", statisRes.getJSONObject("result"));
        return "modules/statis-report/changed-report/charts";
    }


    @GetMapping("tablelist")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        // data.put("groups", groupTpl().getJSONArray("groups"));

        
        String changedItems = "[{\"code\":\"todo\",\"codeName\":\"待整改\",\"loadUrl\":\"changed-report/tablelist/todo\"},{\"code\":\"done\",\"codeName\":\"已整改\",\"loadUrl\":\"changed-report/tablelist/done\"},{\"code\":\"redo\",\"codeName\":\"复查记录\",\"loadUrl\":\"changed-report/tablelist/redo\"}]";
        data.put("changedItems", JSONObject.parseArray(changedItems));
        
        JSONObject downloadUrl = new JSONObject();
        downloadUrl.put("todo", labAPIGateway + "/api-lab-security-check/check/exportWaitingRectification/" + ecmId);
        downloadUrl.put("done", labAPIGateway + "/api-lab-security-check/rectification/exportRectificationStatistics/" + ecmId);
        downloadUrl.put("redo", labAPIGateway + "/api-lab-security-check/review/exportReviewStatistics/" + ecmId);
        data.put("downloadUrl", downloadUrl);
        
        return "modules/statis-report/changed-report/list";
    }

    
    @GetMapping("tablelist/{code}")
    public String tablelist(Map<String,Object> data, Integer ecmId,
        String hiddenDangerGrade,
        Long startTime,
        Long endTime,
        @PathVariable String code){

        data.put("timestamp", System.currentTimeMillis());
        data.put("ecmId", ecmId);
        data.put("hiddenDangerGrade", hiddenDangerGrade);
        data.put("startTime", startTime);
        data.put("endTime", endTime);

        return "modules/statis-report/changed-report/" + code;
    }


    @GetMapping("list")
    public String list(Map<String,Object> data,
        String hiddenDangerGrade,
        Long startTime,
        Long endTime,
        Integer ecmId){
            
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(2));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.inspectPage(ecmId, 0, null, hiddenDangerGrade, startTime, endTime, null, page, limit, null);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }
}
