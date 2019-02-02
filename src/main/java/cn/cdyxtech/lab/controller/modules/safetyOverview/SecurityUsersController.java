package cn.cdyxtech.lab.controller.modules.safetyOverview;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.InterfaceProxyCloudAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/security-users")
public class SecurityUsersController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private InterfaceProxyCloudAPI ipcApi;

    public JSONObject safetyOverviewTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-overview", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
    
    @GetMapping("/index")
    public String index(Map<String,Object> data,String[] showColumns,String[] showOperations){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId0", ecmId);
        return "modules/safety-overview/security-users/index";
    }

    @GetMapping("list")
    public String list(Map<String,Object> data, 
        Integer ecmId0,   
        Integer ecmId,
        String keyword){

        if (ecmId0 == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId0 = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        
        }

        Integer type = ecmId == null ? 1 : 2;
        ecmId = ecmId == null ? ecmId0 : ecmId;

        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(8));
        PageRequest pr = getPageRequestData();
        JSONObject res = ipcApi.securityUsers(ecmId, type, keyword);
        this.dealException(res);
        JSONObject result = JSONObject.parseObject("{\"currentPage\":1,\"totalCount\":0,\"resultList\":[]}");
        result.put("totalCount", res.getJSONArray("result").size());
        result.put("resultList", res.getJSONArray("result"));
        data.put("data", result);
        return "tpl/list";
    }


}
