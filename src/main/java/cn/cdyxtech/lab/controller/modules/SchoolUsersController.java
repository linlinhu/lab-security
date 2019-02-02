package cn.cdyxtech.lab.controller.modules;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/school-users")
public class SchoolUsersController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

    public JSONObject listTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-branch", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    
    @GetMapping("/index")
    public String index(Map<String,Object> data){
        data.put("timestamp", System.currentTimeMillis());
        JSONArray scs = new JSONArray();
        scs.add(0, "select");
        scs.add(1, "name");
        scs.add(2, "mobile");
        scs.add(3, "idCard");
        scs.add(4, "operation");
        data.put("showColumns", JSONArray.toJSONString(scs));

        
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        
        data.put("ecmId", ecmId);
        data.put("ecmDeep", false);

        return "modules/user/index";
    }


}
