package cn.cdyxtech.lab.controller.modules;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.util.UserClaim;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/branchs")
public class BranchsController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

    public JSONObject listTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "list-sbranch", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    public JSONObject formTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "form-sbranch", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    public JSONObject schoolBranchFormTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "form-sbranch", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("/index")
    public String index(Map<String,Object> data,String[] showColumns,String[] showOperations){
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId",ecmId);

        data.put("timestamp", System.currentTimeMillis());
        if(ArrayUtils.isNotEmpty(showColumns)){
            data.put("showColumns",JSONArray.toJSONString(showColumns));
        }
        if(ArrayUtils.isNotEmpty(showOperations)){
            data.put("showOperations",showOperations);
        }

        return "modules/branchs/index";
    }
    UserClaim uc = this.validateAuthorizationToken();
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String[] showColumns, String keyword, Integer ecmId){
        if (ecmId == null) {
            throw new EminException("404");
        }
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", listTpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
        JSONObject res = basicInfoApi.sbranchPage(ecmId, pr.getCurrentPage(), pr.getLimit(), keyword);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));

        if(showColumns!=null){
            data.put("showColumns",showColumns);
        }
        return "tpl/list";
    }

    
    @GetMapping("/form")
    public String form(Map<String,Object> data, Integer ecmId){
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", formTpl());

        Integer schoolEcmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        JSONObject info = new JSONObject();
        if (ecmId != null) {
            JSONObject res = basicInfoApi.branchInfo(ecmId);
            this.dealException(res);
            info = res.getJSONObject("result");
        }
        info.put("universityId", schoolEcmId);
        
        data.put("info", info);
        return "tpl/form";
    }

    @PostMapping("/remove")
    @ResponseBody
    public JSONObject remove(Map<String,Object> data, Integer ecmId){
        if (ecmId == null) {
            throw new EminException("404");
        }
        JSONObject res = basicInfoApi.removeBranch(ecmId);
        this.dealException(res);
        return res;
    }
}
