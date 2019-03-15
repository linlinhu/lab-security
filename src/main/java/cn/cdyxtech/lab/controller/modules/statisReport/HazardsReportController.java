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
import cn.cdyxtech.lab.filter.MenuOperationFilter;

import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/hazards-report")
public class HazardsReportController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(HazardsReportController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
	MenuOperationFilter menuOperationFilter;
    
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
        try {
			String operationCodes = menuOperationFilter.menuOperations("statis-report");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("统计报表管理界面跳转，加载权限出现异常->" + e.getMessage());
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
        JSONObject result = res.getJSONObject("result");
        JSONArray list = result.getJSONArray("resultList");
        JSONObject item;
        JSONArray dangerSourceList;
        ArrayList<String> dangerSourceNameList;
        String dangerSourceNameStr = new String();
        if(!list.isEmpty()) {
            for(int i = 0; i < list.size(); i++){
                item = list.getJSONObject(i);
                dangerSourceNameList = new ArrayList();
                dangerSourceList = item.getJSONArray("hazardSourceCategoryNames");
                if(dangerSourceList!=null && !dangerSourceList.isEmpty()) {
                    for(int j = 0; j < dangerSourceList.size(); j++) {
                        dangerSourceNameList.add(dangerSourceList.getJSONObject(j).getString("categoryName"));
                    }
                }
                dangerSourceNameStr = String.join("，", dangerSourceNameList);
                item.put("dangerSourceName", dangerSourceNameStr);
            
            }
            result.put("resultList", list);
        }
        
        data.put("data", result);
        return "tpl/list";
    }

}
