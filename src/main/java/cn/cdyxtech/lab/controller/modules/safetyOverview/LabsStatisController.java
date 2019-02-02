package cn.cdyxtech.lab.controller.modules.safetyOverview;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import com.alibaba.fastjson.JSONArray;
import cn.cdyxtech.lab.feign.MelAPIFeign;

import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/labs-statis")
public class LabsStatisController extends HeaderCommonController {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(LabsStatisController.class);
    @Autowired
    private MelAPIFeign melApiFeign;
    @Autowired
    private BasicInfoAPI basicInfoApi;

    public JSONObject safetyOverviewTpl() {
        JSONObject res = new JSONObject();

        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-overview", "BROWSER");
        this.dealException(res);

        return res.getJSONObject("result");
    }

    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);
        this.getStatisData(data, ecmId);
        return "modules/safety-overview/labs-statis/index";
    }

    private static final String COLLEGE_STATIS_KEY = "collegeData";
    private static final String CATEGORY_STATIS_KEY = "categoryData";
    private static final String LAB_LEVEL_STATIS_KEY = "labLevelData";
    private void getStatisData(Map<String,Object> data, Integer ecmId) {
        // 按学院统计数据
        JSONObject collegeRes = basicInfoApi.getLabStatisticsByCollege(ecmId);
        this.dealException(collegeRes);
        JSONArray collegeList = collegeRes.getJSONArray("result");
        JSONObject collegeData = this.parseCollegeData(collegeList);
        if(collegeData != null) {
            data.put(COLLEGE_STATIS_KEY, collegeData.toJSONString());
        }

        // 按分类统计数据
        JSONObject categoryRes = basicInfoApi.getLabStatisticsByCategory(ecmId);
        this.dealException(categoryRes);
        JSONObject categoryData = this.parseForChartData(collegeRes.getJSONArray("result"));
        if(categoryData != null) {
            data.put(CATEGORY_STATIS_KEY, categoryData.toJSONString());
        }

        // 按实验室等级统计数据
        JSONObject labLevelRes = basicInfoApi.getLabStatisticsByCategory(ecmId);
        this.dealException(labLevelRes);
        JSONObject labLevelData = this.parseForChartData(labLevelRes.getJSONArray("result"));
        if(labLevelData != null) {
            data.put(LAB_LEVEL_STATIS_KEY, labLevelData.toJSONString());
        }

    }

    private JSONObject parseCollegeData(JSONArray datasource) {
        if(datasource == null || datasource.size() == 0) return null;
        JSONObject result = new JSONObject();
        JSONArray yAxisData = new JSONArray();
        JSONArray seriesData = new JSONArray();
        int total = 0;
        for(int i = 0 ; i < datasource.size(); i++) {
            JSONObject obj = datasource.getJSONObject(i);
            String name = obj.getString("name");
            int count = obj.getInteger("count");
            total += count;
            yAxisData.add(name);
            seriesData.add(count);
        }
        result.put("total", total);
        result.put("yAxisData", yAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    /**
     * 图表数据的转换以适配业务图表数据处理
     *
     * @return
     */
    private JSONObject parseForChartData(JSONArray datasource) {
        int total = 0;
        JSONObject result = new JSONObject();
        JSONArray legendData = new JSONArray();
        JSONArray seriesData = new JSONArray();
        for(int i = 0 ; i < datasource.size(); i++) {
            JSONObject obj = datasource.getJSONObject(i);
            String name = obj.getString("name");
            int count = obj.getInteger("count");
            total += count;
            legendData.add(name);
            JSONObject newObj = new JSONObject();
            newObj.put("name", name);
            newObj.put("value", count);
            seriesData.add(newObj);
        }
        result.put("total", total);
        result.put("legendData", legendData);
        result.put("seriesData", seriesData);
        return result;
    }

    @GetMapping("list")
    public String list(Map<String,Object> data, 
        Integer ecmId,  
        String labType, 
        String labGrade, 
        String keyword){

        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        
        }
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(0));
        PageRequest pr = getPageRequestData();
        JSONObject res = basicInfoApi.labsPageBySchoolId(ecmId, pr.getCurrentPage(), pr.getLimit(), keyword, labType, labGrade);
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }


    
    public JSONObject infoTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "info-lab", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    @GetMapping("detail")
    public String list(Map<String,Object> data, Integer ecmId){
        data.put("timestamp", System.currentTimeMillis());
        data.put("tpl", infoTpl());

        JSONObject res = basicInfoApi.labInfo(ecmId);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));
        data.put("existApplyId", 100);
        data.put("showColumns", "mobile,name,role");
        data.put("showOperations", "search");
        return "modules/safety-overview/labs-statis/detail";
    }

}
