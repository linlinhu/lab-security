package cn.cdyxtech.lab.controller.modules.safetyChanged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;
 
@Controller
@RequestMapping("/safety-changed")
public class ScTodoController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
    private SecurityCheckAPI securityCheckApi;

    public JSONObject groupTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-changed", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    @GetMapping("/index")
    public String index(Map<String,Object> data){
        if (this.validateAuthorizationToken().getHighestEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getHighestEcmId().toString());
        data.put("ecmId", ecmId);
        if (this.validateAuthorizationToken().getSchoolEcmId() != null) {
            data.put("schoolEcmId", this.validateAuthorizationToken().getSchoolEcmId());
        }
        data.put("timestamp", System.currentTimeMillis());
        data.put("groups", groupTpl().getJSONArray("groups"));

        
        String changedItems = "[{\"code\":\"todo\",\"codeName\":\"待整改\",\"loadUrl\":\"safety-changed/tablelist/todo\"},{\"code\":\"done\",\"codeName\":\"整改历史\",\"loadUrl\":\"safety-changed/tablelist/done\"},{\"code\":\"redo\",\"codeName\":\"复查记录\",\"loadUrl\":\"safety-changed/tablelist/redo\"}]";
        data.put("changedItems", JSONObject.parseArray(changedItems));
        
        return "modules/safety-changed/index";
    }

    @GetMapping("tablelist/{code}")
    public String tablelist(Map<String,Object> data, Integer ecmId,
        String hiddenDangerGrade,
        String keyword,
        @PathVariable String code){

        data.put("timestamp", System.currentTimeMillis());
        data.put("ecmId", ecmId);
        data.put("hiddenDangerGrade", hiddenDangerGrade);
        data.put("keyword", keyword);

        return "modules/safety-changed/" + code;
    }
    

     
    @GetMapping("/list")
    public String list(Map<String,Object> data, 
        String hiddenDangerGrade, 
        Integer ecmId, 
        String keyword){
        
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getHighestEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getHighestEcmId().toString());
        }
        
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", groupTpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.inspectPage(ecmId, 0, null, hiddenDangerGrade, null, null, keyword, page, limit, null);
        // data.put("data", res.getJSONObject("result"));
        
        data.put("data", gradeReloadResult(res.getJSONObject("result")));
        return "tpl/waterfall-list";
    }
    
    @GetMapping("detail")
    public String detail(Map<String,Object> data, 
        Integer id, 
        Integer labid, 
        String detailTitle){

        data.put("id", id);
        data.put("labId", labid);
        data.put("detailTitle", detailTitle);

        return "tpl/inspect-detail/detail";
    }

}
