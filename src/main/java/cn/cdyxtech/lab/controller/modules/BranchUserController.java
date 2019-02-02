package cn.cdyxtech.lab.controller.modules;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.controller.HeaderCommonController;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.emin.base.exception.EminException;


@Controller
@RequestMapping("/branch-user")
public class BranchUserController extends HeaderCommonController {
   
    @GetMapping("/index")
    public String index(Map<String,Object> data,String[] showColumns,String[] showOperations){
        data.put("timestamp", System.currentTimeMillis());
        if(ArrayUtils.isNotEmpty(showColumns)){
            data.put("showColumns",JSONArray.toJSONString(showColumns));
        }
        if(ArrayUtils.isNotEmpty(showOperations)){
            data.put("showOperations",showOperations);
        }
        if (this.validateAuthorizationToken().getBranchEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getBranchEcmId().toString());
        data.put("ecmId", ecmId);
        data.put("ecmDeep", false);
        return "modules/user/index";
    }
}
