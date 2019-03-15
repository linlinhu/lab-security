package cn.cdyxtech.lab.controller.modules.safetyOverview;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.facade.ECOFacade;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.util.JWTThreadLocalUtil;
import cn.cdyxtech.lab.vo.CheckDataVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hidden-danger-statis")
public class HiddenDangerStatisController extends HeaderCommonController {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(HiddenDangerStatisController.class);

    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

    @Autowired
    private ConfigFacade configFacade;
    @Autowired
    private ECOFacade ecoFacade;

    @Autowired
    private SecurityCheckAPI securityCheckApi;

    public JSONObject listTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-branch", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
        // 检查项
        Long topEcmId = JWTThreadLocalUtil.getRootEcmId();
        List<CheckDataVO> voList =configFacade.getCheckData(topEcmId);
        data.put("checkItemList", JSONArray.toJSON(voList));

        // 不符合项统计查询
        // Integer ecmId = 4;
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getPersonalHeigherEcmId().toString());
        JSONObject unqualifiedRes = securityCheckApi.getPageStatisticsByUnqualified(ecmId, 1, 100);
        this.dealException(unqualifiedRes);
        data.put("unqualifiedList", unqualifiedRes.getJSONObject("result").getJSONArray("resultList"));

        // 隐患等级统计查询
        JSONObject dangerGradeRes = securityCheckApi.getStatisticsByProblemGrade(ecmId);
        this.dealException(dangerGradeRes);

        data.put("dangerGradeList", dangerGradeRes.getJSONArray("result"));

        return "modules/safety-overview/hidden-danger-statis/index";
    }
}
