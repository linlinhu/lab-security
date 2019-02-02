package cn.cdyxtech.lab.controller.modules.statisReport;

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
@RequestMapping("/changed-report-done")
public class ChangedDoneReportController extends HeaderCommonController {
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

    @GetMapping("/list")
    public String list(Map<String,Object> data,
        String hiddenDangerGrade,
        Long startTime,
        Long endTime, Integer ecmId) {
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(3));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();

        JSONObject res = securityCheckApi.changedPage(ecmId, null, hiddenDangerGrade, startTime, endTime, null, page, limit);
        this.dealException(res);
        // String resStr = "{\"resultList\":[{\"id\":1,\"unqualifiedLevelTwo\":\"危险源辨识\",\"labName\":\"微生物实验室502-1\",\"createTime\":1544400000000,\"unqualifiedGrade\":\"1级\",\"describe\":\"发现严重源，并没有警示\",\"result\":\"通过\",\"opUserName\":\"张三\"},{\"id\":2,\"unqualifiedLevelTwo\":\"校级层面实验室安全管理制度\",\"labName\":\"嵌入式501-1\",\"createTime\":1544400000000,\"unqualifiedGrade\":\"普通\",\"describe\":\"没有张贴制度标语测试测试...\",\"result\":\"不通过\",\"opUserName\":\"李四\"}],\"currentPage\":1,\"totalCount\":2}";
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }

}
