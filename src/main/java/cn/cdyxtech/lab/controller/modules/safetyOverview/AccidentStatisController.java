package cn.cdyxtech.lab.controller.modules.safetyOverview;

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
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/accident-statis")
public class AccidentStatisController extends HeaderCommonController {
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

    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
       
        if (this.validateAuthorizationToken().getPersonalHeigherEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getPersonalHeigherEcmId().toString());
        data.put("ecmId", ecmId);

        return "modules/safety-overview/accident-statis/index";
    }

    

    @GetMapping("/list")
    public String list(Map<String,Object> data, String keyword, Integer ecmId){
        
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(6));
        
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.accidentPage(ecmId, page, limit, keyword);
        data.put("data", res.getJSONObject("result"));
        // String resStr = "{\"resultList\":[{\"id\":1,\"name\":\"化学实验室镁粉爆炸事故调查\",\"createTime\":1544400000000,\"userName\":\"李四\"},{\"id\":2,\"name\":\"生物实验室物品燃烧事故调查\",\"createTime\":1544400000000,\"userName\":\"张三\"}],\"currentPage\":1,\"totalCount\":2}";
        // data.put("data", JSONObject.parseObject(resStr));
        return "tpl/list";
    }


}
