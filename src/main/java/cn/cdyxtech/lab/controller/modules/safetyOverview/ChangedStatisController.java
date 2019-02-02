package cn.cdyxtech.lab.controller.modules.safetyOverview;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cdyxtech.lab.constain.ApplicationConstain;
import cn.cdyxtech.lab.controller.HeaderCommonController;
import cn.cdyxtech.lab.feign.MelAPIFeign;
import cn.cdyxtech.lab.feign.SecurityCheckAPI;

import java.text.NumberFormat;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.dao.PageRequest;
import com.emin.base.exception.EminException;

@Controller
@RequestMapping("/changed-statis")
public class ChangedStatisController extends HeaderCommonController {
    @Autowired
    private MelAPIFeign melApiFeign;

    @Autowired
    private SecurityCheckAPI securityCheckApi;

    public JSONObject groupTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-changed", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    public JSONObject safetyOverviewTpl() {
        JSONObject res = new JSONObject();
        res = melApiFeign.dataModel(ApplicationConstain.SERVICE_ID, "safety-overview", "BROWSER");
        this.dealException(res);
        return res.getJSONObject("result");
    }

    private final static String RECTIFY_STATIS = "rectifyData";
    private final static String REVIEW_STATIS = "reviewData";
    @GetMapping("index")
    public String detail(Map<String,Object> data, Long id){
        data.put("timestamp", System.currentTimeMillis());
        if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
            throw new EminException("404");
        }
        Integer ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        data.put("schoolEcmId", ecmId);
        data.put("ecmId", ecmId);
        // 整改统计数据
        JSONObject rectifyRes = securityCheckApi.rectification(ecmId);
        this.dealException(rectifyRes);

        JSONObject rectifyData = this.parseChartData(rectifyRes.getJSONArray("result"), RECTIFY_STATIS);
        if(rectifyData != null) {
            data.put("unRectifyCount", rectifyData.getInteger("undoneCount"));
            data.put("rectifyCount", rectifyData.getInteger("doneCount"));
            data.put("rectifyTotal", rectifyData.getInteger("total"));
            data.put(RECTIFY_STATIS, rectifyData.toJSONString());
        }

        // 复查统计数据
        JSONObject reviewRes = securityCheckApi.reviewStatus(ecmId);
        this.dealException(reviewRes);
        JSONObject reviewData = this.parseChartData(reviewRes.getJSONArray("result"), REVIEW_STATIS);
        if(reviewData != null) {
            data.put("unReviewCount", rectifyData.getInteger("undoneCount"));
            data.put("reviewCount", rectifyData.getInteger("doneCount"));
            data.put("reviewTotal", rectifyData.getInteger("total"));
            data.put(REVIEW_STATIS, reviewData.toJSONString());
        }

        String changedItems = "[{\"code\":\"todo\",\"codeName\":\"待整改\",\"loadUrl\":\"changed-statis/tablelist/todo\"},{\"code\":\"done\",\"codeName\":\"已整改\",\"loadUrl\":\"changed-statis/tablelist/done\"},{\"code\":\"redo\",\"codeName\":\"复查记录\",\"loadUrl\":\"changed-statis/tablelist/redo\"}]";
        data.put("changedItems", JSONObject.parseArray(changedItems));

        return "modules/safety-overview/changed-statis/index";
    }

    @GetMapping("tablelist/{code}")
    public String tablelist(Map<String,Object> data, Integer ecmId,
                            String hiddenDangerGrade,
                            String keyword,
                            @PathVariable String code){
        data.put("timestamp", System.currentTimeMillis());
        data.put("ecmId", ecmId);
        data.put("hiddenDangerGrade", hiddenDangerGrade);
        data.put("keyword", keyword);
        return "modules/safety-overview/changed-statis/" + code;
    }
    @GetMapping("list")
    public String list(Map<String,Object> data,
                       String hiddenDangerGrade,
                       String keyword, Integer ecmId){

        if (ecmId == null) {
            if (this.validateAuthorizationToken().getSchoolEcmId() == null) {
                throw new EminException("404");
            }
            ecmId = Integer.parseInt(this.validateAuthorizationToken().getSchoolEcmId().toString());
        }

        data.put("tpl", safetyOverviewTpl().getJSONArray("groups").getJSONObject(2));
        PageRequest pr = getPageRequestData();
        Integer page = pr.getCurrentPage();
        Integer limit = pr.getLimit();
        JSONObject res = securityCheckApi.inspectPage(ecmId, 0, null, hiddenDangerGrade, null, null, keyword, page, limit, null);
        this.dealException(res);
        data.put("data", res.getJSONObject("result"));
        return "tpl/list";
    }

    @GetMapping("detail")
    public String detail(Map<String,Object> data,
                         Integer id,
                         Integer labId,
                         String detailTitle){
        data.put("id", id);
        data.put("labId", labId);
        data.put("detailTitle", detailTitle);
        return "tpl/inspect-detail/detail";
    }

    /**
     * 图表数据的转换以适配业务图表数据处理
     *
     * @return
     */
    private JSONObject parseChartData(JSONArray datasource, String type) {
        if(datasource == null || datasource.size() == 0) return null;
        JSONObject result = new JSONObject();
        int doneCount = 0;
        int unDoneCount = 0;
        int total = 0;
        JSONArray legendData = new JSONArray();
        JSONArray seriesData = new JSONArray();
        for(int i = 0 ; i < datasource.size(); i++) {
            JSONObject obj = datasource.getJSONObject(i);
            Integer rectificationStatus = obj.getInteger("rectificationStatus");
            if (rectificationStatus != null) {
                // 待整改/复查不通过
                if(rectificationStatus == 0) {
                    JSONObject unRectifyObj = new JSONObject();
                    Integer count =  obj.getInteger("count");
                    if(type.contentEquals(RECTIFY_STATIS)) {
                        unRectifyObj.put("name", "未整改");
                        unRectifyObj.put("value", count);
                        legendData.add("未整改");
                    } else {
                        unRectifyObj.put("name", "不通过");
                        unRectifyObj.put("value", count);
                        legendData.add("不通过");
                    }
                    unDoneCount = count;
                    total += count;
                    seriesData.add(unRectifyObj);
                }
                // 已整改/复查通过
                if(rectificationStatus == 1) {
                    JSONObject rectifyObj = new JSONObject();
                    Integer count =  obj.getInteger("count");
                    if(type.contentEquals(RECTIFY_STATIS)) {
                        rectifyObj.put("name", "已整改");
                        rectifyObj.put("value", count);
                        legendData.add("已整改");
                    } else {
                        rectifyObj.put("name", "通过");
                        rectifyObj.put("value", count);
                        legendData.add("通过");
                    }
                    doneCount = count;
                    total += count;
                    seriesData.add(rectifyObj);
                }
            }
        }
        String ratio = total == 0 ? "0" : getPercent(doneCount, total);
        result.put("ratio", ratio);
        result.put("total", total);
        result.put("doneCount", doneCount);
        result.put("unDoneCount", unDoneCount);
        result.put("seriesData", seriesData);
        return result;
    }

    private String getPercent(Integer num,Integer total ){
        String percent ;
        Double p3 = 0.0;
        if(total == 0){
            p3 = 0.0;
        }else{
            p3 = num*1.0/total;
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);//控制保留小数点后几位，2：表示保留2位小数点
        percent = nf.format(p3);
        return percent;
    }
}
