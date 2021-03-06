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
import cn.cdyxtech.lab.filter.MenuOperationFilter;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/accident-report")
public class AccidentReportController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(AccidentReportController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private SecurityCheckAPI securityCheckApi;
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
        JSONObject res = securityCheckApi.accidentStatis(ecmId);

        data.put("statisData", res.getJSONObject("result").getJSONArray("countList"));
        return "modules/statis-report/accident-report/charts";
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
        data.put("downloadUrl", labAPIGateway + "/api-lab-security-check/accident/exportSafetyAccidentStatistics/" + ecmId);
        
        return "modules/statis-report/accident-report/list";
    }

    

    @GetMapping("/list")
    public String list(Map<String,Object> data, Integer ecmId, String keyword){
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(6));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.accidentPage(ecmId, page, limit, keyword);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }


}
