package cn.cdyxtech.lab.controller.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.facade.ConfigFacade;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.filter.MenuOperationFilter;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/branch-labs")
public class BranchLabsController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(BranchLabsController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
    private ConfigFacade configFacade;
    @Autowired
	MenuOperationFilter menuOperationFilter;
    
    public JSONObject listTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "list-blab", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    
    public JSONObject formTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "form-blab", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("/index")
    public String index(Map<String,Object> data){
        try {
			String operationCodes = menuOperationFilter.menuOperations("branch-labs");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("实验室管理界面跳转，加载权限出现异常->" + e.getMessage());
		}
        data.put("timestamp", System.currentTimeMillis());
        return "modules/branch/labs/index";
    }
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String keyword, String categoryName){
        if (this.validateAuthorizationToken().getBranchEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getBranchEcmId().toString());
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
        JSONObject res = basicInfoApi.labPage(ecmId, pr.getCurrentPage(), pr.getLimit(), keyword, categoryName);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }

    
    @GetMapping("/form")
    public String form(Map<String,Object> data, Integer ecmId){
        
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", formTpl());
        
        Integer branchEcmId = Integer.parseInt(this.validateAuthorizationToken().getBranchEcmId().toString());
        JSONObject info = new JSONObject();
        if (ecmId != null) {
            JSONObject res = basicInfoApi.labInfo(ecmId);
            this.dealException(res);
            info = res.getJSONObject("result");
        }
        info.put("collegeId", branchEcmId);
        data.put("info", info);
        data.put("pageSubmit", 1);
        return "tpl/form";
    }

    
    @GetMapping("/typelist")
    public String typelist(Map<String,Object> data, String itemToken, String value){
        data.put("data", configFacade.getConfigItems(ConfigOption.ConfigGroup.LAB_CATEGORY));
        data.put("itemToken", itemToken);
        data.put("value", value);
        data.put("max_selected_options", 1);
        return "tpl/combo";
    }

    
    @GetMapping("/dslist")
    public String dslist(Map<String,Object> data, String itemToken, String value){
        data.put("data", configFacade.getConfigItems(ConfigOption.ConfigGroup.DANGEROUS_SOURCE_CATEGORY));
        data.put("itemToken", itemToken);
        data.put("value", value);
        data.put("max_selected_options", 10);
        return "tpl/combo";
    }

    
    @GetMapping("/labGrades")
    public String labGrades(Map<String,Object> data, String itemToken, String value){
        String res = "[{\"name\":\"Ⅰ级\",\"value\":\"Ⅰ级\"},{\"name\":\"Ⅱ级\",\"value\":\"Ⅱ级\"},{\"name\":\"Ⅲ级\",\"value\":\"Ⅲ级\"},{\"name\":\"Ⅳ级\",\"value\":\"Ⅳ级\"}]";
        data.put("data", JSONObject.parseArray(res));
        data.put("itemToken", itemToken);
        data.put("value", value);
        data.put("max_selected_options", 1);
        return "tpl/combo";
    }
}
