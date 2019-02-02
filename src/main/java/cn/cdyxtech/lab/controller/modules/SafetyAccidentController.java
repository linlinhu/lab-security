package cn.cdyxtech.lab.controller.modules;

import cn.cdyxtech.lab.controller.ResponseBack;
import cn.cdyxtech.lab.facade.SafetyAccidentFacade;
import cn.cdyxtech.lab.util.UserClaim;
import cn.cdyxtech.lab.vo.SafetyAccidentRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.constain.ConfigOption;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.InterfaceProxyCloudAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;
 
@Controller
@RequestMapping("/safety-accident")
public class SafetyAccidentController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;
    @Autowired
    private SecurityCheckAPI securityCheckApi;
    @Autowired
    private InterfaceProxyCloudAPI interfaceProxyCloudAPI;

    @Autowired
    private SafetyAccidentFacade safetyAccidentFacade;

    public JSONObject groupTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "security-accident", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    @GetMapping("/index")
    public String index(Map<String,Object> data){
        if (this.validateAuthorizationToken().getHighestEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getHighestEcmId().toString());
        data.put("ecmId", ecmId);
        if (this.validateAuthorizationToken().getSchoolEcmId() != null) {
            data.put("schoolEcmId", this.validateAuthorizationToken().getSchoolEcmId());
        }
        return "modules/safety-accident/index";
    }

     
    @GetMapping("/list")
    public String list(Map<String,Object> data, 
        String hiddenDangerGrade, 
        Integer ecmId, 
        String keyword){
        
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getHighestEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getHighestEcmId().toString());
        }
        
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", groupTpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.accidentPage(ecmId, page, limit, keyword);
        data.put("data", res.getJSONObject("result"));

        // String res = "{\"currentPage\":1,\"totalCount\":2,\"resultList\":[{\"id\":1,\"name\":\"化学实验室镁粉爆炸事故调查\",\"statusDesc\":\"进行中\",\"createTime\":1544400000000,\"address\":\"计算机学院嵌入式实验室502-1\",\"flockNames\":\"张三、王五、赵六、李四\",\"describe\":\"2018年9月10日上午在嵌入式实验室502-1发生爆炸事件，测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试测试数据测试测试数据测试测试数据测试测试数据测试测试数据测试测试数据测试\"},{\"id\":2,\"name\":\"化学实验室镁粉爆炸事故调查\",\"statusDesc\":\"进行中\",\"createTime\":1544400000000,\"address\":\"计算机学院嵌入式实验室502-1\",\"flockNames\":\"张三、王五、赵六、李四\",\"describe\":\"2018年9月10日上午在嵌入式实验室502-1发生爆炸事件，测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试测试数据测试测试数据测试测试数据测试测试数据测试测试数据测试测试数据测试\"}]}";
        // data.put("data", JSONObject.parseObject(res));
        return "tpl/waterfall-list";
    }
    
    @GetMapping("/detail")
    public String detail(Map<String,Object> data, 
        Long id){
        data.put("id",id);
        Long userId = this.validateAuthorizationToken().getId();
        data.put("isMember",safetyAccidentFacade.isAccidentTeamMember(id,userId));
        data.put("isFinished",safetyAccidentFacade.isAccidentFinished(id));
        return "modules/safety-accident/detail";
    }
    @GetMapping("/recordList")
    public String recordList(Map<String,Object> data,Long id,String stage){
        Long userId = this.validateAuthorizationToken().getId();

        data.put("userId",userId);
        List<SafetyAccidentRecordVO> recordList = safetyAccidentFacade.getSafetyAccidentRecordList(id,stage);
        data.put("recordList",recordList);
        data.put("stage",stage);
        return "modules/safety-accident/recordList";
    }

    @GetMapping("/recordForm")
    public String recordInfo(Map<String,Object> data,Long id,Long accId,String stage){
        if(id!=null){
            SafetyAccidentRecordVO vo = safetyAccidentFacade.recordDetail(id);
            data.put("record",vo);
        }

        data.put("id",id);
        data.put("accId",accId);
        data.put("stage",stage);
        return "modules/safety-accident/recordForm";
    }

    @PostMapping("/saveRecord")
    @ResponseBody
    public ResponseBack<Boolean> saveRecord(@RequestBody SafetyAccidentRecordVO vo){
        UserClaim c = this.validateAuthorizationToken();
        vo.setUserId(c.getId());
        vo.setUserName(c.getRealName()==null?"未知":c.getRealName());
        safetyAccidentFacade.saveSafetyAccidentRecord(vo);
        return ResponseBack.success(true);
    }

    @PostMapping("/finishAccident")
    @ResponseBody
    public ResponseBack<Boolean> finishAccident(Long id){
        safetyAccidentFacade.finishAccident(id);
        return ResponseBack.success(true);
    }

    @GetMapping("/form")
    public String form(Map<String,Object> data){
    	data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", formTpl());
        data.put("submitName", "完成");
        data.put("pageSubmit", 1);
        return "modules/safety-accident/form";
    }
    
    /**
     * 获取学院列表
     * @param ecmId 学校主体编号
     */
    
    @GetMapping("/get-college-list")
    public String getCollegeList(Map<String,Object> data, String itemToken, String value){
    	JSONObject res = interfaceProxyCloudAPI.getCollegeList(this.validateAuthorizationToken().getHighestEcmId());
		this.dealException(res);
		JSONArray result = res.getJSONArray("result");
		JSONArray arr = new JSONArray();
		JSONObject temp;
		JSONObject item;
		if(!result.isEmpty()) {
			for(int i = 0; i < result.size(); i++){
				item = result.getJSONObject(i);
				temp = new JSONObject();
				temp.put("name", item.getString("value"));
				temp.put("value", item.getString("id"));
				arr.add(temp);
			}
		};
        data.put("data", arr);
        data.put("itemToken", itemToken);
        data.put("value", value);
        data.put("max_selected_options", 1);
        return "tpl/combo";
    }
    
    public JSONObject formTpl() {
        String code = "form-security-accid";
        JSONObject res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, code, "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

}
