package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.ExaminationAPIFeign;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.util.UserClaim;
import java.util.Map;

@Controller
@RequestMapping("/examination")
public class ExaminationController extends HeaderCommonController{
	@Autowired
    private MelAPIFeign melApiFeign;
	@Autowired
    private ExaminationAPIFeign examinationAPIFeign;
	
	
    
    @GetMapping("/index")
    public String index(Map<String,Object> data,Long type){
        Long ecmId = this.validateAuthorizationToken().getHighestEcmId();
        data.put("ecmId",ecmId);
        data.put("timestamp", System.currentTimeMillis());
        data.put("type", type);
        return "modules/examination/manage";
    }
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String[] showColumns, String keyword, Long startTime, Long endTime,Boolean isAgree){
    	UserClaim userClaim = this.validateAuthorizationToken();
    	String sort = "applyStatus,lastModifyTime";
    	String order = "asc,desc";
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(0));
        Integer limit= getPageRequestData().getLimit();
        Integer page= getPageRequestData().getCurrentPage();
        Long userId = userClaim.getId();
        JSONObject res = examinationAPIFeign.querypage(keyword, startTime, endTime, null, userId, isAgree, sort, order, page, limit);
        this.dealException(res);
        data.put("data",res.getJSONObject("result"));
        return "tpl/examination/list";
    }
    
    @PostMapping("/verify")
	@ResponseBody
	public JSONObject verify(Long id, Long isAgree) {
    	UserClaim userClaim = this.validateAuthorizationToken();
    	Long userId = userClaim.getId();
		JSONObject res = examinationAPIFeign.verify(id, userId, isAgree);
		this.dealException(res);
		return res;
	}
    
  
    
    public JSONObject listTpl() {
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "examination", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
    
   
}
