
package cn.cdyxtech.lab.controller.modules.qrcode;

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
import cn.cdyxtech.lab.filter.MenuOperationFilter;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/qrcode")
public class QrcodeController extends HeaderCommonController {
    private Logger logger = LoggerFactory.getLogger(QrcodeController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
	MenuOperationFilter menuOperationFilter;

    public JSONObject tpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "qrcode-manage", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", tpl());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null || this.validateAuthorizationToken().getId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        return "modules/qrcode/index";
    }

    @GetMapping("/manage")
    public String index(Map<String,Object> data, String[] showColumns, String[] showOperations, Integer ecmId){
        if (ecmId == null) {
            throw new EminException("404");
        }
        data.put("timestamp", System.currentTimeMillis());
        if(ArrayUtils.isNotEmpty(showColumns)){
            data.put("showColumns",JSONArray.toJSONString(showColumns));
        } else {
            JSONArray scs = new JSONArray();
            scs.add(0, "select");
            scs.add(1, "name");
            scs.add(2, "securityUserName");
            scs.add(3, "qrcode");
            scs.add(4, "operation");
            data.put("showColumns",JSONArray.toJSONString(scs));
        }
        if(ArrayUtils.isNotEmpty(showOperations)){
            data.put("showOperations",showOperations);
        }
        JSONObject res = basicInfoApi.sbranchPage(ecmId, 1, 100, null);
        this.dealException(res);
        data.put("branchs", res.getJSONObject("result").getJSONArray("resultList"));
        try {
			String operationCodes = menuOperationFilter.menuOperations("qrcode");
			data.put("operationCodes", operationCodes);
		} catch (Exception e) {
            logger.error("二维码管理界面跳转，加载权限出现异常->" + e.getMessage());
		}
        
        return "modules/qrcode/manage";
    }
    
    @GetMapping("/list")
    public String list(Map<String,Object> data, String[] showColumns, Long ecmId, Long branchId, String keyword){
    	if (this.validateAuthorizationToken().getId() == null) {
            throw new EminException("404");
        }
        if(branchId != null) {
            ecmId = branchId;
        }
        if (ecmId == null) {
            ecmId = this.validateAuthorizationToken().getSchoolEcmId();
            if (ecmId == null) {
                throw new EminException("404");
            }
        }
        Integer userId = Integer.parseInt(this.validateAuthorizationToken().getId().toString());
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", tpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
       /*  JSONObject res = basicInfoApi.printApplyPage(ecmId.intValue(), userId, pr.getCurrentPage(), pr.getLimit(), keyword); */
       JSONObject res = basicInfoApi.labsPageBySchoolId(ecmId.intValue(), pr.getCurrentPage(), pr.getLimit(), keyword, null, null);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));
        if(showColumns!=null){
            data.put("showColumns",showColumns);
        }
        return "tpl/list";
    }

    
    @GetMapping("/print")
    public String print(Map<String,Object> data, String[] showColumns, String[] sns){
        JSONObject res = new JSONObject();
        res = basicInfoApi.generateQrcode(sns);
        this.dealException(res);
        data.put("codes", res.getJSONArray("result"));
        return "modules/qrcode/print";
    }

    
    @PostMapping("/recordPrint")
    @ResponseBody
    public JSONObject recordPrint(String applyIds) {
        Integer userId = Integer.parseInt(this.validateAuthorizationToken().getId().toString());
        JSONObject res = new JSONObject();
        res = basicInfoApi.recordPrint(applyIds, userId);
        this.dealException(res);
        return res;
    }
    @PostMapping("/noticePrinted")
    @ResponseBody
    public JSONObject noticePrinted(String applyIds) {
        Integer userId = Integer.parseInt(this.validateAuthorizationToken().getId().toString());
        JSONObject res = new JSONObject();
        res = basicInfoApi.noticePrinted(applyIds, userId);
        this.dealException(res);
        return res;
    }
}
