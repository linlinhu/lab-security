package cn.cdyxtech.lab.controller.modules.safetyOverview;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.BasicInfoAPI;
import cn.cdyxtech.lab.feign.MelAPIFeign;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/hazards-statis")
public class HazardsStatisController extends HeaderCommonController {
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

    private static final String HAZARD_CATEGORY_STATIS_KEY = "hazardCategoryData";
    private static final String COLLEGE_STATIS_KEY = "collegeData";
    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("ecmId", ecmId);

        // 按学院统计数据-用学校ecmId
        JSONObject collegeRes = basicInfoApi.getHazardSourceSumStatistics(ecmId);
        this.dealException(collegeRes);
        JSONArray collegeList = collegeRes.getJSONArray("result");
        JSONObject collegeData = this.parseCollegeData(collegeList);
        data.put("sum", collegeData.getInteger("sum"));
        data.put(COLLEGE_STATIS_KEY, collegeData.toJSONString());

        // 按危险源分类统计数据-用学校ecmId
        JSONObject hazardCategoryRes = basicInfoApi.getHazardSourceCategoryStatistics(ecmId);
        this.dealException(hazardCategoryRes);
        JSONObject labLevelData = this.parseChartData(hazardCategoryRes.getJSONArray("result"));
        data.put(HAZARD_CATEGORY_STATIS_KEY, labLevelData.toJSONString());

        return "modules/safety-overview/hazards-statis/index";
    }

    private JSONObject parseCollegeData(JSONArray datasource) {
        JSONObject result = new JSONObject();
        JSONArray yAxisData = new JSONArray();
        JSONArray seriesData = new JSONArray();
        int total = 0;
        for(int i = 0 ; i < datasource.size(); i++) {
            JSONObject obj = datasource.getJSONObject(i);
            String name = obj.getString("name");
            int count = obj.getInteger("count");
            yAxisData.add(name);
            seriesData.add(count);
            total += count;
        }
        result.put("sum", total);
        result.put("xAxisData", yAxisData);
        result.put("seriesData", seriesData);
        return result;
    }

    /**
     * 图表数据的转换以适配业务图表数据处理
     *
     * @return
     */
    private JSONObject parseChartData(JSONArray datasource) {
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
        String dangerSource, 
        String keyword, Integer ecmId){
        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }
        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(1));
        
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = basicInfoApi.labPage(ecmId, page, limit, keyword, dangerSource);
        // String resStr = "{\"resultList\":[{\"dangerSourceName\":\"易燃品\",\"labName\":\"微生物实验室502-1\",\"laboratoryCategoryNames\":[{\"categoryName\":\"化学类\"}],\"labLevel\":\"Ⅰ级\",\"securityUserName\":\"张三\"},{\"dangerSourceName\":\"剧毒物\",\"labName\":\"药剂实验室302-2\",\"laboratoryCategoryNames\":[{\"categoryName\":\"生物类\"}],\"labLevel\":\"Ⅳ级\",\"securityUserName\":\"李四\"}],\"currentPage\":1,\"totalCount\":2}";
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }

}
