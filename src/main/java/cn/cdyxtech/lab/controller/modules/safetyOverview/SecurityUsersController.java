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
import cn.cdyxtech.lab.util.UserClaim;

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
    @Autowired
	private BasicInfoAPI basicInfoAPI;

    public JSONObject safetyOverviewTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-overview", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }
    
    @GetMapping("/index")
    public String index(Map<String,Object> data,String[] showColumns,String[] showOperations){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getPersonalHeigherEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getPersonalHeigherEcmId().toString());
        data.put("ecmId0", ecmId);
        return "modules/safety-overview/security-users/index";
    }

    @GetMapping("list")
    public String list(Map<String,Object> data, 
        Integer ecmId0,   
        Integer ecmId,
        String keyword){
        UserClaim userClaim = this.validateAuthorizationToken();
        Long heigherEcmId = userClaim.getPersonalHeigherEcmId();
        Integer type = 3; //默认是实验室 
        if (heigherEcmId == null) {
            throw new EminException("404");
        }
        if(userClaim.getHighestEcmIdType() == 4) {
            type = 4; //安全中心
        } else {
            if(heigherEcmId.equals(userClaim.getSchoolEcmId())) {
                type = 1; //学校
            } else if(heigherEcmId.equals(userClaim.getBranchEcmId())) {
                type = 2; //学院
            }
        }

        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(8));
        PageRequest pr = getPageRequestData();
        JSONObject res = ipcApi.securityUsers(heigherEcmId.intValue(), type, keyword);
        this.dealException(res);
        JSONArray resultList = res.getJSONArray("result");
        /* res = basicInfoAPI.scInfo(ecmId.longValue());
        this.dealException(res);
        Long scFlockId = res.getJSONObject("result").getLong("flockId");
        if(scFlockId != null) {
            res = ipcApi.securityUsers(scFlockId.intValue(), 4, keyword);
            this.dealException(res);
            JSONArray tempList = res.getJSONArray("result");
            if(!tempList.isEmpty()) {
                resultList.add(tempList.get(0));
            }
        } */
        JSONObject result = JSONObject.parseObject("{\"currentPage\":1,\"totalCount\":0,\"resultList\":[]}");
        result.put("totalCount", resultList.size());
        result.put("resultList", resultList);
        data.put("data", result);
        return "tpl/list";
    }


}
