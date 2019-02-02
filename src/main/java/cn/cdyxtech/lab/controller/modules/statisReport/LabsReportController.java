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
@RequestMapping("/labs-report")
public class LabsReportController extends HeaderCommonController {
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

    /**
     * 图表数据统计
     * @param data
     * @param id
     * @param ecmId
     * @return
     */
    @GetMapping("charts")
    public String charts(Map<String,Object> data, Long id, Integer ecmId){
        JSONObject statisRes = basicInfoApi.labsStatis(ecmId);
        this.dealException(statisRes);
        data.put("statisData", statisRes.getJSONObject("result"));
        return "modules/statis-report/labs-report/charts";
    }

    @GetMapping("tablelist")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        data.put("downloadUrl", labAPIGateway + "/api-lab-basicinformation-extension/wholeLaboratory/exportLabStatistics/" + ecmId);
        
        return "modules/statis-report/labs-report/list";
    }

    

    @GetMapping("list")
    public String list(Map<String,Object> data, 
        Integer ecmId,  
        String labType, 
        String labGrade, 
        String keyword){

        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        
        }
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
        JSONObject res = basicInfoApi.labsPageBySchoolId(ecmId, pr.getCurrentPage(), pr.getLimit(), keyword, labType, labGrade);
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }

}
