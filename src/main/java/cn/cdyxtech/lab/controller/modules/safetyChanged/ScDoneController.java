package cn.cdyxtech.lab.controller.modules.safetyChanged;

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
@RequestMapping("/safety-changed-done")
public class ScDoneController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private SecurityCheckAPI securityCheckApi;

   
    public JSONObject groupTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-changed", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }


    @GetMapping("/list")
    public String list(Map<String,Object> data, 
        String hiddenDangerGrade, 
        Integer ecmId, 
        String keyword){
        
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        

        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", groupTpl().getJSONArray("groups").getJSONObject(1));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.changedPage(ecmId, null, hiddenDangerGrade, null, null, keyword, page, limit);
        // String resStr = "{\"resultList\":[{\"id\":1,\"unqualifiedLevelTwo\":\"危险源辨识\",\"labName\":\"微生物培植501-1\",\"unqualifiedLevelThree\":\"涉及剧毒品、病原微生物、放射性同位素、强磁等高危场所，具备符合要求的软硬件设施，并有明显的警示标识\",\"describe\":\"发现严重危险源，并没有警示\",\"opUserName\":\"张三\",\"unqualifiedGrade\":\"1级\",\"createTime\":1547683200000},{\"id\":2,\"unqualifiedLevelTwo\":\"用电基础安全\",\"labName\":\"嵌入式401-2\",\"unqualifiedLevelThree\":\"实验室电容量、插头插座与用电设备功率需匹配，不得私自改装；电源插座须固定\",\"describe\":\"私自改装电器\",\"opUserName\":\"张三\",\"unqualifiedGrade\":\"2级\",\"createTime\":1547596800000}],\"currentPage\":1,\"totalCount\":2}";
        
        data.put("data", gradeReloadResult(res.getJSONObject("result")));
        return "tpl/waterfall-list";
    }

}
