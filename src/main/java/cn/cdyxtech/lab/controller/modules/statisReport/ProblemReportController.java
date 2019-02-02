package cn.cdyxtech.lab.controller.modules.statisReport;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.OverviewStatisAPIFeign;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/problem-report")
public class ProblemReportController extends HeaderCommonController {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ProblemReportController.class);

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
        JSONObject statisRes = securityCheckApi.problemStatis(ecmId);
        this.dealException(statisRes);
        data.put("statisData", statisRes.getJSONArray("result"));
        return "modules/statis-report/problem-report/charts";
    }

    @GetMapping("tablelist")
    public String tablelist(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        data.put("downloadUrl", labAPIGateway + "/api-lab-security-check/check/exportProStatistics/" + ecmId);
        return "modules/statis-report/problem-report/list";
    }
    
    @GetMapping("list")
    public String list(Map<String,Object> data, 
        String hiddenDangerGrade, 
        String checkGrade, 
        String keyword, Integer ecmId){
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(7));
        
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.inspectPage(ecmId, null, null, hiddenDangerGrade, null, null, keyword, page, limit, checkGrade);
        data.put("data", res.getJSONObject("result"));
        // String resStr = "{\"resultList\":[{\"unqualifiedGrade\":\"严重隐患\",\"checkType\":\"6级\",\"unqualifiedLevelTwo\":\"实验室防爆\",\"describe\":\"对于产生可燃气体或蒸气的装置，应在其进、出口处安装阻火器。室内应加强通风，以使爆炸物浓度控制在爆炸下限值以\",\"labName\":\"嵌入式501-1\"},{\"unqualifiedGrade\":\"一般隐患\",\"checkType\":\"4级\",\"unqualifiedLevelTwo\":\"个人防护\",\"describe\":\"在特殊的实验室配备和使用呼吸器或面罩（如有挥发性毒物、溅射危险等），并正确选择种类；呼吸器或面罩在有效期内，不用时须密封放置\",\"labName\":\"嵌入式501-2\"}],\"currentPage\":1,\"totalCount\":2}";
        // data.put("data", JSONObject.parseObject(resStr));
        
        return "tpl/list";
    }
}
