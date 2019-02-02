package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
 
@Controller
@RequestMapping("/statis-report")
public class StatisReportController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
    private SecurityCheckAPI securityCheckApi;

    public JSONObject groupTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "security-accident", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    @GetMapping("/index")
    public String index(Map<String,Object> data){
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);

        /**
         * 菜单初始代码，写死的
         */
        String reportItems = "[{\"code\":\"labs-report\",\"codeName\":\"实验室统计\"},{\"code\":\"hazards-report\",\"codeName\":\"危险源统计\"},{\"code\":\"inspect-report\",\"codeName\":\"检查统计\"},{\"code\":\"changed-report\",\"codeName\":\"整改统计\"},{\"code\":\"problem-report\",\"codeName\":\"问题统计\"},{\"code\":\"accident-report\",\"codeName\":\"安全事故统计\"}]";
        data.put("reportItems", JSONObject.parseArray(reportItems));
        return "modules/statis-report/index";
    }

}
