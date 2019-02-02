
package cn.cdyxtech.lab.controller.modules.qrcode;

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
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/application-list")
public class ApplicationListController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;

    @Autowired
    private BasicInfoAPI basicInfoApi;

    public JSONObject tpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "qrcode-manage", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("/manage")
    public String index(Map<String,Object> data,String[] showColumns,String[] showOperations, Integer ecmId){
        data.put("timestamp", System.currentTimeMillis());
        if(ArrayUtils.isNotEmpty(showColumns)){
            data.put("showColumns",JSONArray.toJSONString(showColumns));
        } else {
            JSONArray scs = new JSONArray();
            scs.add(0, "select");
            scs.add(1, "collegeName");
            scs.add(2, "createTime");
            scs.add(3, "laboratoryName");
            scs.add(4, "securityUserName");
            scs.add(5, "qrcode");
            scs.add(6, "operation");
            data.put("showColumns",JSONArray.toJSONString(scs));
        }
        if(ArrayUtils.isNotEmpty(showOperations)){
            data.put("showOperations",showOperations);
        }
        data.put("ecmId", ecmId);

        return "modules/qrcode/application-list";
    }
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String[] showColumns, String keyword, Integer ecmId){
        if (ecmId == null || this.validateAuthorizationToken().getId() == null) {
            throw new EminException("404");
        }
        Integer userId = Integer.parseInt(this.validateAuthorizationToken().getId().toString());
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", tpl().getJSONArray("groups").getJSONObject(1));
        PageRequest pr = getPageRequestData();
        JSONObject res = basicInfoApi.printApplyPage(ecmId, userId, pr.getCurrentPage(), pr.getLimit(), keyword);
        data.put("data", res.getJSONObject("result"));
        if(showColumns!=null){
            data.put("showColumns",showColumns);
        }
        return "tpl/list";
    }

}
