package cn.cdyxtech.lab.controller.modules.statisReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/hazards-report")
public class HazardsReportController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    
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
        JSONObject statisRes = basicInfoApi.hazardStatis(ecmId);
        this.dealException(statisRes);
        data.put("statisData", statisRes.getJSONObject("result"));
        return "modules/statis-report/hazards-report/charts";
    }

    @GetMapping("tablelist")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        data.put("downloadUrl", labAPIGateway + "/api-lab-basicinformation-extension/wholeLaboratory/exportHSStatistics/" + ecmId);
        return "modules/statis-report/hazards-report/list";
    }


    @GetMapping("list")
    public String list(Map<String,Object> data, 
        String dangerSource, 
        String keyword, Integer ecmId){
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(1));
        
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = basicInfoApi.labPage(ecmId, page, limit, keyword, dangerSource);
        // String resStr = "{\"resultList\":[{\"dangerSourceName\":\"易燃品\",\"labName\":\"微生物实验室502-1\",\"laboratoryCategoryNames\":[{\"categoryName\":\"化学类\"}],\"labLevel\":\"Ⅰ级\",\"securityUserName\":\"张三\"},{\"dangerSourceName\":\"剧毒物\",\"labName\":\"药剂实验室302-2\",\"laboratoryCategoryNames\":[{\"categoryName\":\"生物类\"}],\"labLevel\":\"Ⅳ级\",\"securityUserName\":\"李四\"}],\"currentPage\":1,\"totalCount\":2}";
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }

}
